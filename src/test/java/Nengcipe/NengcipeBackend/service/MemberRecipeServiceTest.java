package Nengcipe.NengcipeBackend.service;

import Nengcipe.NengcipeBackend.domain.Member;
import Nengcipe.NengcipeBackend.domain.MemberRecipe;
import Nengcipe.NengcipeBackend.domain.Recipe;
import Nengcipe.NengcipeBackend.exception.DuplicationException;
import Nengcipe.NengcipeBackend.exception.NotFoundException;
import Nengcipe.NengcipeBackend.repository.MemberRecipeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
@DisplayName("스크랩 테스트")
class MemberRecipeServiceTest {
    @InjectMocks
    private MemberRecipeService memberRecipeService;
    @Mock
    private MemberRecipeRepository memberRecipeRepository;
    @Nested
    @DisplayName("스크랩 등록 테스트")
    class registerTest {

        @Test
        @DisplayName("성공 테스트")
        void createScrapRecipe() {
            //given
            Member member = Member.builder()
                    .memberId("abc123")
                    .memberName("abc123")
                    .password("123456").build();
            Recipe recipe = Recipe.builder()
                    .recipeName("맛있는 요리 1")
                    .recipeDetail("잘 만든다.")
                    .recipeIngredName("돼지고기, 당근, 소고기")
                    .recipeIngredAmount("500g, 1개, 300g")
                    .imgUrl("url").build();
            MemberRecipe memberRecipe = MemberRecipe.builder()
                    .member(member)
                    .recipe(recipe).build();

            when(memberRecipeRepository.save(any(MemberRecipe.class))).thenReturn(memberRecipe);

            //when
            MemberRecipe scrapRecipe = memberRecipeService.createScrapRecipe(member, recipe);

            //then
            assertThat(scrapRecipe.getRecipe().getRecipeName()).isEqualTo("맛있는 요리 1");
            assertThat(scrapRecipe.getMember().getMemberName()).isEqualTo("abc123");
        }

        @Test
        @DisplayName("중복 에러 테스트")
        void duplicationTest() {
            //given
            Member member = Member.builder()
                    .memberId("abc123")
                    .memberName("abc123")
                    .password("123456").build();
            Recipe recipe = Recipe.builder()
                    .recipeName("맛있는 요리 1")
                    .recipeDetail("잘 만든다.")
                    .recipeIngredName("돼지고기, 당근, 소고기")
                    .recipeIngredAmount("500g, 1개, 300g")
                    .imgUrl("url").build();
            MemberRecipe memberRecipe = MemberRecipe.builder()
                    .member(member)
                    .recipe(recipe).build();
            when(memberRecipeRepository.findByMemberAndRecipe(member, recipe)).thenReturn(Optional.of(memberRecipe));

            //then
            assertThatThrownBy(
                    ()->memberRecipeService.createScrapRecipe(member, recipe)
            ).isInstanceOf(DuplicationException.class).hasMessageContaining("스크랩 레시피 : 중복 에러");
        }
    }

    @Nested
    @DisplayName("스크랩 취소 테스트")
    class deleteTest {
        @Test
        @DisplayName("성공 테스트")
        void deleteScrapRecipe() {
            //given
            Member member = Member.builder()
                    .memberId("abc123")
                    .memberName("abc123")
                    .password("123456").build();
            Recipe recipe = Recipe.builder()
                    .recipeName("맛있는 요리 1")
                    .recipeDetail("잘 만든다.")
                    .recipeIngredName("돼지고기, 당근, 소고기")
                    .recipeIngredAmount("500g, 1개, 300g")
                    .imgUrl("url").build();
            MemberRecipe memberRecipe = MemberRecipe.builder()
                    .member(member)
                    .recipe(recipe).build();
            when(memberRecipeRepository.findByMemberAndRecipe(member, recipe)).thenReturn(Optional.of(memberRecipe));

            //when
            MemberRecipe scrapRecipe = memberRecipeService.deleteScrapRecipe(member, recipe);


            //then
            assertThat(scrapRecipe.getRecipe().getRecipeName()).isEqualTo("맛있는 요리 1");
            assertThat(scrapRecipe.getMember().getMemberName()).isEqualTo("abc123");
        }
        @Test
        @DisplayName("NotFound 예외 테스트")
        void deleteScrapRecipeExist() {
            //given
            Member member = Member.builder()
                    .memberId("abc123")
                    .memberName("abc123")
                    .password("123456").build();
            Recipe recipe = Recipe.builder()
                    .recipeName("맛있는 요리 1")
                    .recipeDetail("잘 만든다.")
                    .recipeIngredName("돼지고기, 당근, 소고기")
                    .recipeIngredAmount("500g, 1개, 300g")
                    .imgUrl("url").build();
            when(memberRecipeRepository.findByMemberAndRecipe(member, recipe)).thenReturn(Optional.empty());

            //then
            assertThatThrownBy(
                    ()->memberRecipeService.deleteScrapRecipe(member, recipe)
            ).isInstanceOf(NotFoundException.class).hasMessageContaining("스크랩 레시피 : 찾을 수 없음");

        }
    }

}