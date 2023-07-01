package Nengcipe.NengcipeBackend.repository;

import Nengcipe.NengcipeBackend.domain.Member;
import Nengcipe.NengcipeBackend.exception.DuplicationException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.ANY)
@DataJpaTest
@DisplayName("회원 테스트")
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Test
    @DisplayName("저장 테스트")
    public void saveTest() {
        Member member = Member.builder()
                .memberId("abc123")
                .memberName("abc123")
                .password("123456")
                .build();
        Member save = memberRepository.save(member);
        assertThat(member.getMemberId()).isEqualTo(save.getMemberId());
        assertThat(member.getMemberName()).isEqualTo(save.getMemberName());
    }
    @Test
    @DisplayName("삭제 테스트")
    void deleteTest() {
        Member member = Member.builder()
                .memberId("abc123")
                .memberName("abc123")
                .password("123456")
                .build();
        memberRepository.save(member);
        memberRepository.delete(member);
        Optional<Member> findMember = memberRepository.findByMemberId(member.getMemberId());
        assertThat(findMember.isEmpty()).isTrue();

    }

    @Test
    @DisplayName("검색 테스트")
    void findByMemberId() {

        Member member = Member.builder()
                .memberId("abc123")
                .memberName("abc123")
                .password("123456")
                .build();
        memberRepository.save(member);
        Optional<Member> findMember = memberRepository.findByMemberId(member.getMemberId());
        assertThat(findMember.get().getMemberId()).isEqualTo(member.getMemberId());
        assertThat(findMember.get().getMemberName()).isEqualTo(member.getMemberName());
    }
}