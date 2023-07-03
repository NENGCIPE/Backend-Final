package Nengcipe.NengcipeBackend.controller;

import Nengcipe.NengcipeBackend.domain.Member;
import Nengcipe.NengcipeBackend.dto.MemberDto;
import Nengcipe.NengcipeBackend.service.IngredientService;
import Nengcipe.NengcipeBackend.service.MemberService;
import Nengcipe.NengcipeBackend.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
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

    @Test
    @WithAnonymousUser
    void registerMember() throws Exception {
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

        //then
        mockMvc.perform(
                post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberDto))
        ).andExpect(status().isCreated());

    }

    @Test
    void deleteMember() {
    }

    @Test
    void getMyRefrigerator() {
    }

    @Test
    void registerIngredient() {
    }

    @Test
    void deleteIngredient() {
    }

    @Test
    void authPractice() {
    }
}