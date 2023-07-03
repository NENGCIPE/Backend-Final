package Nengcipe.NengcipeBackend.service;

import Nengcipe.NengcipeBackend.domain.Member;
import Nengcipe.NengcipeBackend.domain.MemberRecipe;
import Nengcipe.NengcipeBackend.domain.Recipe;
import Nengcipe.NengcipeBackend.dto.ResultResponse;
import Nengcipe.NengcipeBackend.exception.DuplicationException;
import Nengcipe.NengcipeBackend.exception.NotFoundException;
import Nengcipe.NengcipeBackend.repository.MemberRecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberRecipeService {

    private final MemberRecipeRepository memberRecipeRepository;

    /**
     * API : [POST] 레시피 스크랩 API
     * @param member
     * @param recipe
     * @return newMemberRecipe
     */
    public MemberRecipe createScrapRecipe(Member member, Recipe recipe){
        Optional<MemberRecipe> delMemberRecipe = memberRecipeRepository.findByMemberAndRecipe(member, recipe);
        if (delMemberRecipe.isPresent()) {
            throw new DuplicationException("스크랩 레시피", null);
        }
        MemberRecipe newMemberRecipe = MemberRecipe.builder()
                .member(member) //member_id에 해당하는 member 객체 (jwt)
                .recipe(recipe) //recipe_id에 해당하는 recipe 객체 (findbyid)
                .build();
        return memberRecipeRepository.save(newMemberRecipe); //newMemberRecipe란 객체를 저장소에 저장하는 역할

    }

    public void checkExist(Member member, Recipe recipe) {
        Optional<MemberRecipe> find = memberRecipeRepository.findByMemberAndRecipe(member, recipe);
        if (find.isPresent()) {
            ResultResponse errRes;
            errRes = ResultResponse.builder()
                    .code(HttpStatus.CONFLICT.value())
                    .message("이미 스크랩된 레시피입니다.")
                    .result(find.get().getRecipe().getId())
                    .build();
            throw new DuplicationException("스크랩 레시피", find.get());
        }
    }


    /**
     * API : [DELETE] 레시피 스크랩 삭제 API
     * @param member
     * @param recipe
     * @return
     * @throws NotFoundException
     */
    public MemberRecipe deleteScrapRecipe(Member member, Recipe recipe) {
        Optional<MemberRecipe> delMemberRecipe = memberRecipeRepository.findByMemberAndRecipe(member, recipe);
        if (delMemberRecipe.isEmpty()) {
            throw new NotFoundException("스크랩 레시피", null);
        }
        memberRecipeRepository.delete(delMemberRecipe.get());
        return delMemberRecipe.get();
    }



}