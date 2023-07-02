package Nengcipe.NengcipeBackend.service;

import Nengcipe.NengcipeBackend.domain.Ingredient;
import Nengcipe.NengcipeBackend.domain.Member;
import Nengcipe.NengcipeBackend.domain.Recipe;
import Nengcipe.NengcipeBackend.exception.NotFoundException;
import Nengcipe.NengcipeBackend.repository.RecipeRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {
    @InjectMocks
    private RecipeService recipeService;
    @Mock
    private RecipeRepository recipeRepository;
    @Nested
    @DisplayName("레시피 조회 테스트")
    class findRecipeTest {
        @Test
        @DisplayName("가지고 있는 재료들을 사용하는 레시피 조회 테스트")
        void matchingRecipeTest() {
            //given
            //내가 가지고 있는 재료들
            Member member = Member.builder()
                    .memberId("abc123")
                    .memberName("abc123")
                    .password("123456").build();
            Ingredient ingredient1 = Ingredient.builder()
                    .ingredName("돼지고기")
                    .category("육류")
                    .quantity(1)
                    .member(member)
                    .build();
            Ingredient ingredient2 = Ingredient.builder()
                    .ingredName("양파")
                    .category("채소류")
                    .quantity(1)
                    .member(member)
                    .build();

            ArrayList<Ingredient> myIngredients = new ArrayList<>(Arrays.asList(ingredient1, ingredient2));
            //레포지토리에 저장되어 있는 레시피들
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
            Recipe recipe3 = Recipe.builder()
                    .recipeName("맛있는 요리 3")
                    .recipeDetail("잘 만든다.")
                    .recipeIngredName("돼지고기")
                    .recipeIngredAmount("500g")
                    .imgUrl("url").build();

            List<Recipe> recipes = new ArrayList<>(Arrays.asList(recipe1, recipe2, recipe3));

            when(recipeRepository.findAll()).thenReturn(recipes);

            //when
            List<Recipe> matchingRecipes = recipeService.findMatchingRecipes(myIngredients);

            //then
            assertThat(matchingRecipes.size()).isEqualTo(3);
        }
        @Test
        @DisplayName("레시피 하나 조회 테스트")
        void findById() {
            //given
            Recipe recipe = Recipe.builder()
                    .recipeName("맛있는 요리 1")
                    .recipeDetail("잘 만든다.")
                    .recipeIngredName("돼지고기, 당근, 소고기")
                    .recipeIngredAmount("500g, 1개, 300g")
                    .imgUrl("url").build();
            when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

            //when
            Recipe returnRecipe = recipeService.findRecipeById(1L);

            //then
            assertThat(returnRecipe.getRecipeName()).isEqualTo(recipe.getRecipeName());
            assertThat(returnRecipe.getRecipeDetail()).isEqualTo(recipe.getRecipeDetail());
            assertThat(returnRecipe.getRecipeIngredName()).isEqualTo(recipe.getRecipeIngredName());
            assertThat(returnRecipe.getRecipeIngredAmount()).isEqualTo(recipe.getRecipeIngredAmount());
            assertThat(returnRecipe.getImgUrl()).isEqualTo(recipe.getImgUrl());
        }

    }
    @Test
    @DisplayName("없는 레시피 조회시 예외 반환")
    void recipeNotFoundTest() {
        //given
        when(recipeRepository.findById(1L)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(
                ()->recipeService.findRecipeById(1L)
        ).isInstanceOf(NotFoundException.class)
                .hasMessageContaining("레시피 : 찾을 수 없음");
    }

}
