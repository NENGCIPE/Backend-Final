package Nengcipe.NengcipeBackend.service;

import Nengcipe.NengcipeBackend.domain.Member;
import Nengcipe.NengcipeBackend.dto.MemberDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberService memberService;
    @Test
    @DisplayName("회원 가입 테스트")
    void registerMemberTest() {
        //given

        MemberDto member = MemberDto.builder()
                .memberId("abcd")
                .password("123456").build();
        Member member1 = memberService.registerMember(member);
        Assertions.assertThat(member1.getMemberId()).isEqualTo(member.getMemberId());
    }
}