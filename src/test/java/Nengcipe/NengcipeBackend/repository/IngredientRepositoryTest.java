package Nengcipe.NengcipeBackend.repository;

import Nengcipe.NengcipeBackend.domain.Category;
import Nengcipe.NengcipeBackend.domain.Ingredient;
import Nengcipe.NengcipeBackend.domain.Member;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@Transactional
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.ANY)
@DataJpaTest
class IngredientRepositoryTest {
    @Autowired
    IngredientRepository ingredientRepository;

    @Test
    @DisplayName("재료 저장 테스트")
    void saveTest() {
        //given
        Member member = Member.builder()
                .memberId("abc123")
                .memberName("abc123")
                .password("123456")
                .build();
        Category category = Category.builder()
                .categoryName("육류")
                .build();
        Ingredient ingredient = Ingredient.builder()
                .ingredName("우유")
                .quantity(1)
                .expirationDate(LocalDate.now())
                .category(category)
                .member(member).build();

        //when
        Ingredient saveIngredient = ingredientRepository.save(ingredient);

        //then
        assertThat(saveIngredient.getId()).isEqualTo(ingredient.getId());
        assertThat(saveIngredient.getQuantity()).isEqualTo(ingredient.getQuantity());
        assertThat(saveIngredient.getExpiratioinDate()).isEqualTo(ingredient.getExpiratioinDate());
        assertThat(saveIngredient.getMember()).isEqualTo(ingredient.getMember());
        assertThat(saveIngredient.getCategory()).isEqualTo(ingredient.getCategory());
    }
    @Test
    @DisplayName("재료 삭제 테스트")
    void deleteTest() {
        //given
        Member member = Member.builder()
                .memberId("abc123")
                .memberName("abc123")
                .password("123456")
                .build();
        Category category = Category.builder()
                .categoryName("육류")
                .build();
        Ingredient ingredient = Ingredient.builder()
                .ingredName("우유")
                .quantity(1)
                .expirationDate(LocalDate.now())
                .category(category)
                .member(member).build();
        Ingredient saveIngredient = ingredientRepository.save(ingredient);

        //when
        ingredientRepository.delete(saveIngredient);

        //then
        Optional<Ingredient> findIngredient = ingredientRepository.findById(saveIngredient.getId());
        assertThat(findIngredient.isEmpty()).isTrue();

    }
    @Test
    @DisplayName("재료 pk 조회 테스트")
    void findTest() {
        //given
        Member member = Member.builder()
                .memberId("abc123")
                .memberName("abc123")
                .password("123456")
                .build();
        Category category = Category.builder()
                .categoryName("육류")
                .build();
        Ingredient ingredient = Ingredient.builder()
                .ingredName("우유")
                .quantity(1)
                .expirationDate(LocalDate.now())
                .category(category)
                .member(member).build();
        Ingredient saveIngredient = ingredientRepository.save(ingredient);

        //when
        Ingredient findIngredient = ingredientRepository.findById(saveIngredient.getId()).get();
        //then
        assertThat(findIngredient.getId()).isEqualTo(ingredient.getId());
        assertThat(findIngredient.getQuantity()).isEqualTo(ingredient.getQuantity());
        assertThat(findIngredient.getExpiratioinDate()).isEqualTo(ingredient.getExpiratioinDate());
        assertThat(findIngredient.getMember()).isEqualTo(ingredient.getMember());
        assertThat(findIngredient.getCategory()).isEqualTo(ingredient.getCategory());

    }
    @Test
    @DisplayName("재료 이름 조회 테스트")
    void findByNameTest() {
        //given
        Member member = Member.builder()
                .memberId("abc123")
                .memberName("abc123")
                .password("123456")
                .build();
        Category category = Category.builder()
                .categoryName("육류")
                .build();
        Ingredient ingredient = Ingredient.builder()
                .ingredName("우유")
                .quantity(1)
                .expirationDate(LocalDate.now())
                .category(category)
                .member(member).build();
        Ingredient saveIngredient = ingredientRepository.save(ingredient);

        //when
        Ingredient findIngredient = ingredientRepository.findByIngredNameAndMember(
                saveIngredient.getIngredName(),
                saveIngredient.getMember()
        ).get();
        //then
        assertThat(findIngredient.getId()).isEqualTo(ingredient.getId());
        assertThat(findIngredient.getQuantity()).isEqualTo(ingredient.getQuantity());
        assertThat(findIngredient.getExpiratioinDate()).isEqualTo(ingredient.getExpiratioinDate());
        assertThat(findIngredient.getMember()).isEqualTo(ingredient.getMember());
        assertThat(findIngredient.getCategory()).isEqualTo(ingredient.getCategory());

    }


}