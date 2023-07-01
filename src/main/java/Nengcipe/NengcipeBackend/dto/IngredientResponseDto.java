package Nengcipe.NengcipeBackend.dto;

import Nengcipe.NengcipeBackend.domain.Ingredient;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class IngredientResponseDto {
    private Long id;
    private String ingredName;
    private Integer quantity;
    private String categoryName;
    private LocalDate expirationDate;
    public static IngredientResponseDto of(Ingredient ingredient) {
        return IngredientResponseDto.builder()
                .id(ingredient.getId())
                .ingredName(ingredient.getIngredName())
                .quantity(ingredient.getQuantity())
                .expirationDate(ingredient.getExpiratioinDate())
                .categoryName(ingredient.getCategory()).build();
    }
}
