package Nengcipe.NengcipeBackend.repository;

import Nengcipe.NengcipeBackend.domain.Member;
import Nengcipe.NengcipeBackend.domain.MemberRecipe;
import Nengcipe.NengcipeBackend.domain.Recipe;
import io.jsonwebtoken.lang.Assert;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@Transactional
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.ANY)
@DataJpaTest
@DisplayName("회원 - 레시피 중간 테이블 테스트")
class MemberRecipeRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private MemberRecipeRepository memberRecipeRepository;
    @Test
    @DisplayName("저장 테스트")
    void saveTest() {
        Member member = Member.builder()
                .memberId("abc123")
                .memberName("abc123")
                .password("123456").build();
        Recipe recipe = Recipe.builder()
                .id(1L)
                .recipeName("돼지볶음")
                .recipeDetail("잘 볶는다.")
                .imgUrl("https://image")
                .recipeIngredName("돼지고기, 양파")
                .recipeIngredAmount("500g, 1개")
                .build();
        memberRepository.save(member);
        recipeRepository.save(recipe);
        MemberRecipe memberRecipe = MemberRecipe.myMemberRecipe()
                .member(member)
                .recipe(recipe).build();
        MemberRecipe saveMemberRecipe = memberRecipeRepository.save(memberRecipe);
        assertThat(saveMemberRecipe.getRecipe()).isEqualTo(recipe);
        assertThat(saveMemberRecipe.getMember()).isEqualTo(member);

    }
    @Test
    @DisplayName("조회 테스트")
    void findTest() {
        Member member = Member.builder()
                .memberId("abc123")
                .memberName("abc123")
                .password("123456").build();
        Recipe recipe = Recipe.builder()
                .id(1L)
                .recipeName("돼지볶음")
                .recipeDetail("잘 볶는다.")
                .imgUrl("https://image")
                .recipeIngredName("돼지고기, 양파")
                .recipeIngredAmount("500g, 1개")
                .build();
        memberRepository.save(member);
        recipeRepository.save(recipe);
        MemberRecipe memberRecipe = MemberRecipe.myMemberRecipe()
                .member(member)
                .recipe(recipe).build();
        memberRecipeRepository.save(memberRecipe);
        Optional<MemberRecipe> findMemberRecipe = memberRecipeRepository.findByMemberAndRecipe(member, recipe);
        assertThat(findMemberRecipe.get().getMember()).isEqualTo(member);
        assertThat(findMemberRecipe.get().getRecipe()).isEqualTo(recipe);

    }

}