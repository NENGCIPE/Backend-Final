package Nengcipe.NengcipeBackend.service;

import Nengcipe.NengcipeBackend.domain.Member;
import Nengcipe.NengcipeBackend.dto.MemberDto;
import Nengcipe.NengcipeBackend.dto.MemberResponseDto;
import Nengcipe.NengcipeBackend.exception.DuplicationException;
import Nengcipe.NengcipeBackend.exception.NotFoundException;
import Nengcipe.NengcipeBackend.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BCryptPasswordEncoder encoder;

    @Nested
    @DisplayName("회원 가입")
    class registerMemberTest {
        @Test
        @DisplayName("성공 테스트")
        void successTest() {
            //given
            MemberDto memberDto = MemberDto.builder()
                    .memberId("abc123")
                    .password("123456").build();
            Member member = MemberDto.toEntity(memberDto);
            when(memberRepository.save(any(Member.class))).thenReturn(member);
            when(encoder.encode(anyString())).thenReturn("encodePassword");
            when(memberRepository.findByMemberId("abc123")).thenReturn(Optional.empty());
            //when

            Member saveMember = memberService.registerMember(memberDto);

            //then
            assertThat(saveMember.getMemberId()).isEqualTo(member.getMemberId());
            assertThat(saveMember.getMemberName()).isEqualTo(member.getMemberName());
        }

        @Test
        @DisplayName("아이디 중복 실패 테스트")
        void idDuplicationTest() {
            //given
            MemberDto memberDto = MemberDto.builder()
                    .memberId("abc123")
                    .password("123456").build();
            Member member = MemberDto.toEntity(memberDto);
            when(memberRepository.findByMemberId("abc123")).thenReturn(Optional.of(member));

            //then
            assertThatThrownBy(
                    ()->memberService.registerMember(memberDto)
            ).isInstanceOf(DuplicationException.class)
                            .hasMessageContaining("유저 아이디 : 중복 에러");
        }
    }
    @Nested
    @DisplayName("회원 탈퇴")
    class deleteMemberTest {
        @Test
        @DisplayName("성공 테스트")
        void successTest() {
            //given
            Member member = Member.builder()
                    .memberId("abc123")
                    .memberName("abc123")
                    .password("123456").build();
            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
            //when
            //void는 subbing 할 필요가 없음
            Member deleteMember = memberService.deleteMember(1L);

            //then
            assertThat(deleteMember.getMemberId()).isEqualTo(member.getMemberId());
            assertThat(deleteMember.getMemberName()).isEqualTo(member.getMemberName());
        }

        @Test
        @DisplayName("아이디 없음 실패 테스트")
        void idNotFoundTest() {
            //given
            Member member = Member.builder()
                    .memberId("abc123")
                    .memberName("abc123")
                    .password("123456").build();
            when(memberRepository.findById(1L)).thenReturn(Optional.empty());

            //then
            assertThatThrownBy(
                    () -> memberService.deleteMember(1L)
            ).isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("유저 아이디 : 찾을 수 없음");
        }
    }
    @Nested
    @DisplayName("회원 조회")
    class findMemberTest {
        @Test
        @DisplayName("성공 테스트")
        void successTest() throws NotFoundException {
            //given
            Member member = Member.builder()
                    .memberId("abc123")
                    .memberName("abc123")
                    .password("123456").build();
            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
            //when
            //void는 subbing 할 필요가 없음
            Member deleteMember = memberService.findById(1L);

            //then
            assertThat(deleteMember.getMemberId()).isEqualTo(member.getMemberId());
            assertThat(deleteMember.getMemberName()).isEqualTo(member.getMemberName());
        }

        @Test
        @DisplayName("아이디 없음 실패 테스트")
        void idNotFoundTest() {
            //given
            Member member = Member.builder()
                    .memberId("abc123")
                    .memberName("abc123")
                    .password("123456").build();
            when(memberRepository.findById(1L)).thenReturn(Optional.empty());

            //then
            assertThatThrownBy(
                    () -> memberService.findById(1L)
            ).isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("유저 아이디 : 찾을 수 없음");
        }
    }
}