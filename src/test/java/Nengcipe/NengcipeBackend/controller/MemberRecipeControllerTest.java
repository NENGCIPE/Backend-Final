package Nengcipe.NengcipeBackend.controller;

import Nengcipe.NengcipeBackend.config.SecurityConfig;
import Nengcipe.NengcipeBackend.domain.Member;
import Nengcipe.NengcipeBackend.domain.MemberRecipe;
import Nengcipe.NengcipeBackend.domain.Recipe;
import Nengcipe.NengcipeBackend.dto.MemberRecipeRequestDto;
import Nengcipe.NengcipeBackend.oauth2.PrincipalDetails;
import Nengcipe.NengcipeBackend.repository.MemberRecipeRepository;
import Nengcipe.NengcipeBackend.service.MemberRecipeService;
import Nengcipe.NengcipeBackend.service.MemberService;
import Nengcipe.NengcipeBackend.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("스크랩 컨트롤러 테스트")
@WebMvcTest(value = {MemberRecipeController.class},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
        }
)
@AutoConfigureMockMvc(addFilters = false)
class MemberRecipeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MemberService memberService;
    @MockBean
    private MemberRecipeService memberRecipeService;
    @MockBean
    private RecipeService recipeService;
    @MockBean
    private MemberRecipeRepository memberRecipeRepository;


    @BeforeEach
    void beforeEach() {
        // 각 테스트 실행 전에 Authentication을 SecurityContext에 넣어둠.
        Member member = Member.builder()
                .memberId("abc123")
                .memberName("abc123")
                .password("123456").build();
        when(memberService.deleteMember(null)).thenReturn(member);
        PrincipalDetails principalDetails = new PrincipalDetails(member);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principalDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    @Test
    void scrapRecipe() throws Exception {
        //given
        MemberRecipeRequestDto memberRecipeRequestDto = new MemberRecipeRequestDto(1L);
        Member member = Member.builder()
                .memberId("abc123")
                .memberName("abc123")
                .password("123456").build();
        when(memberService.findById(null)).thenReturn(member);

        Recipe recipe = Recipe.builder()
                .id(1L)
                .recipeName("맛있는 요리 1")
                .recipeDetail("잘 만든다.")
                .recipeIngredName("돼지고기, 당근, 소고기")
                .recipeIngredAmount("500g, 1개, 300g")
                .imgUrl("url").build();
        when(recipeService.findRecipeById(1L)).thenReturn(recipe);

        MemberRecipe memberRecipe = MemberRecipe.builder()
                .member(member)
                .recipe(recipe).build();
        when(memberRecipeService.createScrapRecipe(member, recipe)).thenReturn(memberRecipe);

        //when
        ResultActions ra = mockMvc.perform(post("/api/recipes/scrap")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(objectMapper.writeValueAsString(memberRecipeRequestDto)));

        //then
        ra.andExpect(status().isCreated());
    }

    @Test
    void getScrapRecipe() throws Exception {
        //given
        Member member = Member.builder()
                .memberId("abc123")
                .memberName("abc123")
                .password("123456").build();
        when(memberService.findById(null)).thenReturn(member);

        PrincipalDetails mock = Mockito.mock(PrincipalDetails.class);
        Recipe recipe1 = Recipe.builder()
                .recipeName("맛있는 요리 1")
                .recipeDetail("잘 만든다.")
                .recipeIngredName("돼지고기, 당근, 소고기")
                .recipeIngredAmount("500g, 1개, 300g")
                .imgUrl("url").build();
        Recipe recipe2 = Recipe.builder()
                .recipeName("맛있는 요리 2")
                .recipeDetail("잘 만든다.")
                .recipeIngredName("양파, 소고기")
                .recipeIngredAmount("1개, 300g")
                .imgUrl("url").build();
        MemberRecipe memberRecipe1 = MemberRecipe.builder()
                .member(member)
                .recipe(recipe1).build();
        MemberRecipe memberRecipe2 = MemberRecipe.builder()
                .member(member)
                .recipe(recipe2).build();

        List<MemberRecipe> recipes = new ArrayList<>(Arrays.asList(memberRecipe1, memberRecipe2));
        when(mock.getRecipeList()).thenReturn(recipes);

        //when
        ResultActions ra = mockMvc.perform(get("/api/recipes/scrapList"));

        //then
        ra.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("레시피 스크랩 목록 성공"));

    }

    @Test
    void deleteScrapRecipe() throws Exception {
        //given
        MemberRecipeRequestDto memberRecipeRequestDto = new MemberRecipeRequestDto(1L);
        Member member = Member.builder()
                .memberId("abc123")
                .memberName("abc123")
                .password("123456").build();
        when(memberService.findById(null)).thenReturn(member);

        Recipe recipe = Recipe.builder()
                .recipeName("맛있는 요리 1")
                .recipeDetail("잘 만든다.")
                .recipeIngredName("돼지고기, 당근, 소고기")
                .recipeIngredAmount("500g, 1개, 300g")
                .imgUrl("url").build();
        when(recipeService.findRecipeById(1L)).thenReturn(recipe);

        MemberRecipe memberRecipe = MemberRecipe.builder()
                .member(member)
                .recipe(recipe).build();
        when(memberRecipeService.deleteScrapRecipe(member, recipe)).thenReturn(memberRecipe);

        //when
        ResultActions ra = mockMvc.perform(delete("/api/recipes/scrapOut")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(objectMapper.writeValueAsString(memberRecipeRequestDto)));

        //then
        ra.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("레시피 스크랩삭제 성공"));

    }
}