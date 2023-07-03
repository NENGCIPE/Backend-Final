package Nengcipe.NengcipeBackend.controller;

import Nengcipe.NengcipeBackend.config.SecurityConfig;
import Nengcipe.NengcipeBackend.domain.Ingredient;
import Nengcipe.NengcipeBackend.domain.Member;
import Nengcipe.NengcipeBackend.dto.IngredientDto;
import Nengcipe.NengcipeBackend.dto.MemberDto;
import Nengcipe.NengcipeBackend.oauth2.PrincipalDetails;
import Nengcipe.NengcipeBackend.service.IngredientService;
import Nengcipe.NengcipeBackend.service.MemberService;
import Nengcipe.NengcipeBackend.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.nio.file.Files.delete;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@DisplayName("유저 컨트롤러 테스트")
@WebMvcTest(value = {MemberController.class},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
        }
)
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MemberService memberService;
    @MockBean
    private IngredientService ingredientService;
    @MockBean
    private JwtUtil jwtUtil;
    @Nested
    @DisplayName("등록 테스트")
    class registerMemberTest{
        @Test
        @DisplayName("성공 테스트")
        void successTest() throws Exception {
            //given
            MemberDto memberDto = MemberDto.builder()
                    .memberId("abc123")
                    .memberName("abc123")
                    .password("123456").build();
            Member member = Member.builder()
                    .memberId("abc123")
                    .memberName("abc123")
                    .password("123456").build();
            when(memberService.registerMember(any(MemberDto.class))).thenReturn(member);

            //when
            ResultActions rs = mockMvc.perform(
                    post("/api/users")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(objectMapper.writeValueAsString(memberDto))
            );
            //then
            rs.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(201))
                    .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
                    .andExpect(jsonPath("$.result.memberId").value("abc123"));
        }
        @Test
        @DisplayName("id가 null일 경우 에러")
        void MemberIdNullTest() throws Exception {
            //given
            MemberDto memberDto = MemberDto.builder()
                    .memberName("abc123")
                    .password("123456").build();
            Member member = Member.builder()
                    .memberName("abc123")
                    .password("123456").build();
            when(memberService.registerMember(any(MemberDto.class))).thenReturn(member);

            //when
            ResultActions rs = mockMvc.perform(
                    post("/api/users")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(objectMapper.writeValueAsString(memberDto))
            );
            //then
            rs.andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("아이디는 필수 값입니다."));
        }

    }


    @Test
    @DisplayName("삭제 성공 테스트")
    void deleteMember() throws Exception {
        //given
        //SecurityContext에 Authentication 저장
        MemberDto memberDto = MemberDto.builder()
                .memberId("abc123")
                .password("123456").build();
        Member member = Member.builder()
                .memberId("abc123")
                .memberName("abc123")
                .password("123456").build();
        when(memberService.deleteMember(null)).thenReturn(member);
        PrincipalDetails principalDetails = new PrincipalDetails(member);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principalDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //when
        ResultActions rs = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(memberDto)
                        ));

        //then
        rs.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("회원 탈퇴가 완료되었습니다."))
                .andExpect(jsonPath("$.result.memberId").value("abc123"));
    }

    @Test
    @DisplayName("내 재료 가져오기 테스트")
    void getMyRefrigerator() throws Exception {
        //given
        //SecurityContext에 Authentication 저장
        Member member = Member.builder()
                .memberId("abc123")
                .memberName("abc123")
                .password("123456").build();
        PrincipalDetails principalDetails = new PrincipalDetails(member);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principalDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //stubbing으로 ingredientList 반환
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        Ingredient ingredient1 = Ingredient.builder()
                .ingredName("돼지고기")
                .quantity(1)
                .category("육류")
                .expirationDate(LocalDate.now())
                .member(member).build();
        Ingredient ingredient2 = Ingredient.builder()
                .ingredName("돼지고기")
                .quantity(1)
                .category("육류")
                .expirationDate(LocalDate.now())
                .member(member).build();
        ingredients.add(ingredient1);
        ingredients.add(ingredient2);
        PrincipalDetails mock = Mockito.mock(PrincipalDetails.class);
        when(mock.getIngredientList()).thenReturn(ingredients);

        //when
        ResultActions ra = mockMvc.perform(get("/api/users/fridge"));

        //then
        ra.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("내 냉장고 정보 가져오기 성공."));

    }

    @Test
    @DisplayName("재료 등록 테스트")
    void registerIngredient() throws Exception {
        //given
        Member member = Member.builder()
                .memberId("abc123")
                .memberName("abc123")
                .password("123456").build();
        IngredientDto ingredientDto = IngredientDto.builder()
                .ingredName("돼지고기")
                .quantity(1)
                .categoryName("육류")
                .expirationDate(LocalDate.now()).build();
        PrincipalDetails principalDetails = new PrincipalDetails(member);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principalDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Ingredient ingredient = IngredientDto.toEntity(ingredientDto, member);
        when(memberService.findById(null)).thenReturn(member);
        when(ingredientService.registerIngredient(any(IngredientDto.class), any(Member.class))).thenReturn(ingredient);

        //when
        ResultActions ra = mockMvc.perform(post("/api/users/fridge")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(objectMapper.writeValueAsString(ingredientDto)));

        //then
        ra.andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("냉장고에 재료 저장 성공."))
                .andExpect(jsonPath("$.result.ingredName").value("돼지고기"))
                .andExpect(jsonPath("$.result.quantity").value(1))
                .andExpect(jsonPath("$.result.categoryName").value("육류"));
    }

    @Test
    void deleteIngredient() throws Exception {
        //given
        Member member = Member.builder()
                .memberId("abc123")
                .memberName("abc123")
                .password("123456").build();
        Ingredient ingredient = Ingredient.builder()
                .ingredName("돼지고기")
                .quantity(1)
                .category("육류")
                .expirationDate(LocalDate.now())
                .member(member).build();
        PrincipalDetails principalDetails = new PrincipalDetails(member);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principalDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(memberService.findById(null)).thenReturn(member);
        when(ingredientService.deleteIngredient(member, 1L)).thenReturn(ingredient);

        //when
        ResultActions ra = mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/fridge?id=1"));

        //then
        ra.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("냉장고에 재료 삭제 성공."))
                .andExpect(jsonPath("$.result.ingredName").value("돼지고기"))
                .andExpect(jsonPath("$.result.quantity").value(1))
                .andExpect(jsonPath("$.result.categoryName").value("육류"));
    }

}