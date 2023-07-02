package Nengcipe.NengcipeBackend.service;

import Nengcipe.NengcipeBackend.domain.Ingredient;
import Nengcipe.NengcipeBackend.domain.Member;
import Nengcipe.NengcipeBackend.dto.IngredientDto;
import Nengcipe.NengcipeBackend.exception.NotAuthorizedException;
import Nengcipe.NengcipeBackend.repository.IngredientRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * 이미 존재하는 재료 추가 : Ingredient 엔티티의 quantity 증가.
 * 존재하지 않는 재료 추가 : IngredientRepository에 저장
 */
@DisplayName("재료 테스트")
@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {
    @InjectMocks
    private IngredientService ingredientService;
    @Mock
    private IngredientRepository ingredientRepository;

    @Nested
    @DisplayName("재료 추가 테스트")
    class registerIngredientTest {
        @Test
        @DisplayName("새 재료 추가")
        void newIngredient() {
            //given
            IngredientDto ingredientDto = IngredientDto.builder()
                    .ingredName("돼지고기")
                    .quantity(1)
                    .categoryName("육류")
                    .expirationDate(LocalDate.now())
                    .build();
            Member member = Member.builder()
                    .memberId("abc123")
                    .memberName("abc123")
                    .password("123456").build();
            when(ingredientRepository.findByIngredNameAndMember(ingredientDto.getIngredName(), member))
                    .thenReturn(Optional.empty());

            //when
            Ingredient ingredient = ingredientService.registerIngredient(ingredientDto, member);

            //then
            assertThat(ingredient.getMember()).isEqualTo(member);
            assertThat(ingredient.getCategory()).isEqualTo(ingredientDto.getCategoryName());
            assertThat(ingredient.getQuantity()).isEqualTo(ingredientDto.getQuantity());
            assertThat(ingredient.getIngredName()).isEqualTo(ingredientDto.getIngredName());
            assertThat(ingredient.getExpiratioinDate()).isEqualTo(ingredientDto.getExpirationDate());

        }
        @Test
        @DisplayName("기존 재료 추가")
        void alreadyIngredient() {
            //given
            //inputIngredientDto : 입력 값
            //returnIngredientDto : 기존 존재하는 재료의 quantity가 1 증가
            IngredientDto inputIngredientDto = IngredientDto.builder()
                    .ingredName("돼지고기")
                    .quantity(1)
                    .categoryName("육류")
                    .expirationDate(LocalDate.now())
                    .build();
            Member member = Member.builder()
                    .memberId("abc123")
                    .memberName("abc123")
                    .password("123456").build();
            IngredientDto returnIngredientDto = IngredientDto.builder()
                    .ingredName("돼지고기")
                    .quantity(2)
                    .categoryName("육류")
                    .expirationDate(LocalDate.now())
                    .build();
            Ingredient ingred = IngredientDto.toEntity(returnIngredientDto, member);
            when(ingredientRepository.findByIngredNameAndMember(inputIngredientDto.getIngredName(), member))
                    .thenReturn(Optional.of(ingred));

            //when
            Ingredient ingredient = ingredientService.registerIngredient(inputIngredientDto, member);

            //then 수량이 1인 곳에 2를 추가했으니 수량이 3이 되어야 함.
            assertThat(ingredient.getMember()).isEqualTo(member);
            assertThat(ingredient.getCategory()).isEqualTo(inputIngredientDto.getCategoryName());
            assertThat(ingredient.getQuantity()).isEqualTo(inputIngredientDto.getQuantity()+returnIngredientDto.getQuantity());
            assertThat(ingredient.getIngredName()).isEqualTo(inputIngredientDto.getIngredName());
            assertThat(ingredient.getExpiratioinDate()).isEqualTo(inputIngredientDto.getExpirationDate());

        }

    }

    @Nested
    @DisplayName("재료 삭제 테스트")
    class deleteIngredientTest {
        @Test
        @DisplayName("다른 회원 재료를 삭제할 때 예외 발생")
        void notAuthorizedTest() {
            //given
            IngredientDto ingredientDto = IngredientDto.builder()
                    .ingredName("돼지고기")
                    .quantity(1)
                    .categoryName("육류")
                    .expirationDate(LocalDate.now())
                    .build();
            Member member = Member.builder()
                    .memberId("abc123")
                    .memberName("abc123")
                    .password("123456").build();
            Member member2 = Member.builder()
                    .memberId("abcd1234")
                    .memberName("abc123")
                    .password("123456").build();

            Ingredient alreadyIngredient = IngredientDto.toEntity(ingredientDto, member);
            when(ingredientRepository.findById(1L))
                    .thenReturn(Optional.of(alreadyIngredient));

            //then
            assertThatThrownBy(
                    ()->ingredientService.deleteIngredient(member2, 1L)
            ).isInstanceOf(NotAuthorizedException.class).hasMessageContaining("접근 권한 없음");

        }
        @Test
        @DisplayName("수량이 2이상 재료 삭제")
        void alreadyIngredient() {
            //given
            IngredientDto ingredientDto = IngredientDto.builder()
                    .ingredName("돼지고기")
                    .quantity(2)
                    .categoryName("육류")
                    .expirationDate(LocalDate.now())
                    .build();
            Member member = Member.builder()
                    .memberId("abc123")
                    .memberName("abc123")
                    .password("123456").build();
            Ingredient alreadyIngredient = IngredientDto.toEntity(ingredientDto, member);
            when(ingredientRepository.findById(1L))
                    .thenReturn(Optional.of(alreadyIngredient));

            //when
            Ingredient ingredient = ingredientService.deleteIngredient(member, 1L);

            //then 삭제하면 quantity 1 감소해야 함.
            assertThat(ingredient.getMember()).isEqualTo(member);
            assertThat(ingredient.getCategory()).isEqualTo(ingredientDto.getCategoryName());
            assertThat(ingredient.getQuantity()).isEqualTo(ingredientDto.getQuantity()-1);
            assertThat(ingredient.getIngredName()).isEqualTo(ingredientDto.getIngredName());
            assertThat(ingredient.getExpiratioinDate()).isEqualTo(ingredientDto.getExpirationDate());
        }

    }

}