package Nengcipe.NengcipeBackend.oauth2;

import Nengcipe.NengcipeBackend.domain.Ingredient;
import Nengcipe.NengcipeBackend.domain.Member;
import Nengcipe.NengcipeBackend.domain.MemberRecipe;
import Nengcipe.NengcipeBackend.domain.Recipe;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class PrincipalDetails implements OAuth2User , UserDetails {
//    private Member member;
    private Long id;
    private String memberId;
    private String memberName;
    private String password;
    private List<Ingredient> ingredientList;
    private List<MemberRecipe> recipeList;
    private Map<String, Object> attributes;

    public PrincipalDetails(Member member) {
        this.id=member.getId();
        this.memberId=member.getMemberId();
        this.memberName = member.getMemberName();
        this.password = member.getPassword();
        this.ingredientList = new ArrayList<>();
        this.recipeList = new ArrayList<>();
        member.getIngredientList().stream().forEach(
                e->
                this.ingredientList.add(e)
        );
        member.getMemberRecipeList().stream().forEach(
                e->
                        this.recipeList.add(e)
        );
    }

    public PrincipalDetails(Member member, Map<String, Object> attributes) {
        this.id=member.getId();
        this.memberId=member.getMemberId();
        this.memberName = member.getMemberName();
        this.ingredientList = member.getIngredientList();
        this.recipeList = member.getMemberRecipeList();
        this.attributes=attributes;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "ROLE_USER";
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.memberName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "name";
    }
}
