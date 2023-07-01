package Nengcipe.NengcipeBackend.repository;

import Nengcipe.NengcipeBackend.domain.Recipe;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@Transactional
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.ANY)
@DataJpaTest
@DisplayName("레시피 테스트")
class RecipeRepositoryTest {
    /*
    레시피 저장은 AWS Lambda 함수에서 매일 자정마다 실시.
    id는 "만개의 레시피" url 번호를 의미. Auto increment가 아님.
    * */
    @Autowired
    private RecipeRepository recipeRepository;

    @BeforeEach
    void beforeEach() {
        Recipe recipe1 = Recipe.builder()
                .id(1L)
                .recipeName("돼지볶음")
                .recipeDetail("잘 볶는다.")
                .imgUrl("https://image")
                .recipeIngredName("돼지고기, 양파")
                .recipeIngredAmount("500g, 1개")
                .build();
        Recipe recipe2 = Recipe.builder()
                .id(2L)
                .recipeName("소고기볶음")
                .recipeDetail("잘 볶는다.")
                .imgUrl("https://image")
                .recipeIngredName("소고기, 양파")
                .recipeIngredAmount("500g, 1개")
                .build();
        recipeRepository.save(recipe1);
        recipeRepository.save(recipe2);
    }

    @Test
    @DisplayName("조회 테스트")
    void findById() {
        Optional<Recipe> findRecipe = recipeRepository.findById(1L);
        assertThat(findRecipe.get().getRecipeName()).isEqualTo("돼지볶음");
        assertThat(findRecipe.get().getRecipeDetail()).isEqualTo("잘 볶는다.");
        assertThat(findRecipe.get().getImgUrl()).isEqualTo("https://image");
        assertThat(findRecipe.get().getRecipeIngredName()).isEqualTo("돼지고기, 양파");
        assertThat(findRecipe.get().getRecipeIngredAmount()).isEqualTo("500g, 1개");
    }
    @Test
    @DisplayName("모두 조회 테스트")
    void findAll() {
        List<Recipe> all = recipeRepository.findAll();
        assertThat(all.size()).isEqualTo(2);
    }

}