package Nengcipe.NengcipeBackend.service;

import Nengcipe.NengcipeBackend.domain.Ingredient;
import Nengcipe.NengcipeBackend.domain.Member;
import Nengcipe.NengcipeBackend.domain.Recipe;
import Nengcipe.NengcipeBackend.exception.NotFoundException;
import Nengcipe.NengcipeBackend.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;

    /**
     * 내 재료에 맞는 레시피를 랜덤으로 10개 추출
     */
    public List<Recipe> findMatchingRecipes(List<Ingredient> ingredient) {

        List<Recipe> CrawlingRecipeList = recipeRepository.findAll();
        List<Recipe> matchingRecipes = new ArrayList<>();

        for (Recipe recipeIngredient : CrawlingRecipeList) {
            boolean flag = false;
            for (Ingredient ingredientNameList : ingredient) {
                String[] ingredArr = recipeIngredient.getRecipeIngredName().split(",");
                for (String value : ingredArr) {
                    if (ingredientNameList.getIngredName().contains(value)) {
                        flag = true;
                        break;
                    }
                }
                if (flag == true) {
                    matchingRecipes.add(recipeIngredient);
                    break;
                }
            }
        }
        ArrayList<Recipe> randomList = new ArrayList<>();
        Random random = new Random();
        if (matchingRecipes.size() > 0) {
            for (int i = 0; i < 12 && matchingRecipes.size() >0; i++) {
                System.out.println(matchingRecipes.size());
                int randomInt = random.nextInt(matchingRecipes.size());
                Recipe randomRecipe = matchingRecipes.get(randomInt);
                randomList.add(randomRecipe);
                matchingRecipes.remove(randomInt);

            }

        }


        return randomList;
    }


    public Recipe findRecipeById(Long id) {
        Optional<Recipe> recipe = recipeRepository.findById(id);
        /** recipe를 찾지 못한다면, NotFoundException 발생 **/
        if (recipe.isEmpty()) {
            throw new NotFoundException("레시피", null);
        }
        return recipe.get();
    }
}



