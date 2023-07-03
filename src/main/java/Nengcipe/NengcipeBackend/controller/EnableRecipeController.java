package Nengcipe.NengcipeBackend.controller;

import Nengcipe.NengcipeBackend.domain.Ingredient;
import Nengcipe.NengcipeBackend.domain.Member;
import Nengcipe.NengcipeBackend.domain.Recipe;
import Nengcipe.NengcipeBackend.dto.MatchingRecipeResponseDto;
import Nengcipe.NengcipeBackend.dto.ResultResponse;
import Nengcipe.NengcipeBackend.exception.NotFoundException;
import Nengcipe.NengcipeBackend.oauth2.PrincipalDetails;
import Nengcipe.NengcipeBackend.service.*;
import Nengcipe.NengcipeBackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class EnableRecipeController {

    private final RecipeService recipeService;
    private final MemberService memberService;

    private final JwtUtil jwtUtil;

    @GetMapping("/all")
    public ResponseEntity<ResultResponse> getMatchingRecipes(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) throws NotFoundException {
        /*  Crawling 레시피와 재료 비교하여 레시피 목록 나열하기  */
        List<Ingredient> ingredients = principalDetails.getIngredientList();
        List<Recipe> matchingRecipes = recipeService.findMatchingRecipes(ingredients);
        List<MatchingRecipeResponseDto> response = new ArrayList<>();

        for (Recipe recipe : matchingRecipes) {
            MatchingRecipeResponseDto tmp = MatchingRecipeResponseDto.of(recipe);
            response.add(tmp);
        }

        ResultResponse res = ResultResponse.builder()
                .code(HttpStatus.OK.value())
                .message("레시피 목록 로드 성공.")
                .result(response)
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<ResultResponse> getRecipeDetails(
            @PathVariable Long recipeId
    ) throws NotFoundException {
        Recipe recipe = recipeService.findRecipeById(recipeId);
        MatchingRecipeResponseDto response = MatchingRecipeResponseDto.of(recipe);
        ResultResponse res = ResultResponse.builder()
                .code(HttpStatus.OK.value())
                .message("레시피 상세 정보 로드 성공.")
                .result(response).build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
