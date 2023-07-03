package Nengcipe.NengcipeBackend.controller;

import Nengcipe.NengcipeBackend.domain.Member;
import Nengcipe.NengcipeBackend.domain.MemberRecipe;
import Nengcipe.NengcipeBackend.domain.Recipe;
import Nengcipe.NengcipeBackend.dto.MemberRecipeRequestDto;
import Nengcipe.NengcipeBackend.dto.MemberRecipeResponseDto;
import Nengcipe.NengcipeBackend.dto.ResultResponse;
import Nengcipe.NengcipeBackend.exception.NotFoundException;
import Nengcipe.NengcipeBackend.oauth2.PrincipalDetails;
import Nengcipe.NengcipeBackend.repository.MemberRecipeRepository;
import Nengcipe.NengcipeBackend.service.MemberRecipeService;
import Nengcipe.NengcipeBackend.service.MemberService;
import Nengcipe.NengcipeBackend.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/recipes")
@RequiredArgsConstructor
public class MemberRecipeController {
    private final MemberService memberService;
    private final MemberRecipeService memberRecipeService;
    private final RecipeService recipeService;
    private final MemberRecipeRepository memberRecipeRepository;


    /**
     * API : [POST] 레시피 스크랩 API
     * @param memberRecipeRequestDto
     * @param principalDetails
     * @return
     * @throws NotFoundException
     * @uri api/recipes/scrap
     */

    @PostMapping("/scrap")
    public ResponseEntity<Object> scrapRecipe(
            @RequestBody MemberRecipeRequestDto memberRecipeRequestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long memberId = principalDetails.getId();

        Member member = memberService.findById(memberId);
        Recipe recipe = recipeService.findRecipeById(memberRecipeRequestDto.getRecipeId());

        memberRecipeService.checkExist(member, recipe); //스크랩 여부 체크
        MemberRecipe memberRecipe = memberRecipeService.createScrapRecipe(member, recipe);
        MemberRecipeResponseDto response = MemberRecipeResponseDto.of(memberRecipe);

        ResultResponse res = ResultResponse.builder()
                .code(HttpStatus.OK.value())
                .message("레시피 스크랩 성공.")
                .result(response)
                .build();


        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    /**
     * API : [GET] 레시피 스크랩 목록 API
     * @param principalDetails
     * @return
     * @throws NotFoundException
     * @uri api/recipes/scrapList
     */

    @GetMapping("/scrapList")
    public ResponseEntity<ResultResponse> getScrapRecipe(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long memberId = principalDetails.getId();
        Member member = memberService.findById(memberId);

        ResultResponse res = ResultResponse.builder()
                .code(HttpStatus.OK.value())
                .message("레시피 스크랩 목록 성공")
                .result(principalDetails.getRecipeList())
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * API : [Delete] 레시피 스크랩 삭제 API
     *
     * @param memberRecipeRequestDto
     * @param principalDetails
     * @return
     * @throws NotFoundException
     * @uri api/recipes/scrapOut
     */
    @DeleteMapping("/scrapOut")
    public ResponseEntity<ResultResponse> deleteScrapRecipe(
            @RequestBody MemberRecipeRequestDto memberRecipeRequestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long memberId = principalDetails.getId();
        Member member = memberService.findById(memberId);
        Recipe recipe = recipeService.findRecipeById(memberRecipeRequestDto.getRecipeId());

        MemberRecipe memberRecipe = memberRecipeService.deleteScrapRecipe(member, recipe);
        MemberRecipeResponseDto response = MemberRecipeResponseDto.of(memberRecipe);

        ResultResponse res = ResultResponse.builder()
                .code(HttpStatus.OK.value())
                .message("레시피 스크랩삭제 성공")
                .result(response)
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);

    }
}