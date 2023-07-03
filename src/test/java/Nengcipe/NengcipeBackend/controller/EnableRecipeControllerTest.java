package Nengcipe.NengcipeBackend.controller;

import Nengcipe.NengcipeBackend.config.SecurityConfig;
import Nengcipe.NengcipeBackend.domain.Ingredient;
import Nengcipe.NengcipeBackend.domain.Member;
import Nengcipe.NengcipeBackend.domain.Recipe;
import Nengcipe.NengcipeBackend.oauth2.PrincipalDetails;
import Nengcipe.NengcipeBackend.service.MemberService;
import Nengcipe.NengcipeBackend.service.RecipeService;
import Nengcipe.NengcipeBackend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("레시피 추천 테스트")
@WebMvcTest(value = {EnableRecipeController.class},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
        }
)
@AutoConfigureMockMvc(addFilters = false)
class EnableRecipeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MemberService memberService;
    @MockBean
    private RecipeService recipeService;
    @MockBean
    private JwtUtil jwtUtil;

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
    @DisplayName("가능한 레시피 나열 테스트")
    void getMatchingRecipes() throws Exception {
        //given
        Member member = Member.builder()
                .memberId("abc123")
                .memberName("abc123")
                .password("123456").build();
        Recipe recipe1 = Recipe.builder()
                .id(1L)
                .recipeName("맛있는 요리 1")
                .recipeDetail("잘 만든다.")
                .recipeIngredName("돼지고기, 당근, 소고기")
                .recipeIngredAmount("500g, 1개, 300g")
                .imgUrl("url").build();
        Recipe recipe2 = Recipe.builder()
                .id(1L)
                .recipeName("맛있는 요리 1")
                .recipeDetail("잘 만든다.")
                .recipeIngredName("돼지고기, 당근, 소고기")
                .recipeIngredAmount("500g, 1개, 300g")
                .imgUrl("url").build();
        List<Recipe> recipeList = Arrays.asList(recipe1, recipe2);

        PrincipalDetails mock = Mockito.mock(PrincipalDetails.class);
        Ingredient ingredient1 = Ingredient.builder()
                .ingredName("당근")
                .quantity(1)
                .category("채소류")
                .expirationDate(LocalDate.now())
                .member(member).build();
        Ingredient ingredient2 = Ingredient.builder()
                .ingredName("돼지고기")
                .quantity(1)
                .category("육류")
                .expirationDate(LocalDate.now())
                .member(member).build();
        List<Ingredient> ingredientList = Arrays.asList(ingredient1, ingredient2);
        when(mock.getIngredientList()).thenReturn(ingredientList);
        when(recipeService.findMatchingRecipes(ingredientList)).thenReturn(recipeList);

        //when
        ResultActions ra = mockMvc.perform(get("/api/recipes/all"));

        //then
        ra.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("레시피 목록 로드 성공."));
    }

    @Test
    @DisplayName("레시피 상세 정보 조회 테스트")
    void getRecipeDetails() throws Exception {
        //given
        Recipe recipe = Recipe.builder()
                .id(1L)
                .recipeName("맛있는 요리 1")
                .recipeDetail("잘 만든다.")
                .recipeIngredName("돼지고기, 당근, 소고기")
                .recipeIngredAmount("500g, 1개, 300g")
                .imgUrl("url").build();
        when(recipeService.findRecipeById(1L)).thenReturn(recipe);

        //when
        ResultActions ra = mockMvc.perform(get("/api/recipes/1"));

        //then
        ra.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("레시피 상세 정보 로드 성공."))
                .andExpect(jsonPath("$.result.recipeId").value(1))
                .andExpect(jsonPath("$.result.recipeName").value("맛있는 요리 1"))
                .andExpect(jsonPath("$.result.recipeDetail").value("잘 만든다."))
                .andExpect(jsonPath("$.result.recipeIngredName").value("돼지고기, 당근, 소고기"))
                .andExpect(jsonPath("$.result.recipeIngredAmount").value("500g, 1개, 300g"))
                .andExpect(jsonPath("$.result.imgUrl").value("url"));
    }
}