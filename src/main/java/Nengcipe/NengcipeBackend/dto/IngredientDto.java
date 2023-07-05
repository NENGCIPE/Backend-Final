package Nengcipe.NengcipeBackend.dto;

import Nengcipe.NengcipeBackend.domain.Ingredient;
import Nengcipe.NengcipeBackend.domain.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class IngredientDto {
    private String ingredName;
    private String categoryName;
    private Integer quantity;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate expirationDate;

    public static Ingredient toEntity(IngredientDto ingredientDto, Member member) {
        return Ingredient.builder()
                .ingredName(ingredientDto.ingredName)
                .category(ingredientDto.categoryName)
                .quantity(ingredientDto.quantity)
                .expirationDate(ingredientDto.expirationDate)
                .member(member).build();
    }

}
