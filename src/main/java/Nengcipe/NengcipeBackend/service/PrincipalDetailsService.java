package Nengcipe.NengcipeBackend.service;

import Nengcipe.NengcipeBackend.domain.Member;
import Nengcipe.NengcipeBackend.oauth2.PrincipalDetails;
import Nengcipe.NengcipeBackend.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PrincipalDetailsService implements UserDetailsService{
    private final MemberRepository memberRepository;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String memberId) {
        Optional<Member> member = memberRepository.findByMemberId(memberId);
        return new PrincipalDetails(member.get());
    }
}