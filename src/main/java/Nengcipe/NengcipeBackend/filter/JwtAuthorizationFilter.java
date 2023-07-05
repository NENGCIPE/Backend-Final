package Nengcipe.NengcipeBackend.filter;

import Nengcipe.NengcipeBackend.domain.Ingredient;
import Nengcipe.NengcipeBackend.domain.Member;
import Nengcipe.NengcipeBackend.dto.MemberResponseDto;
import Nengcipe.NengcipeBackend.dto.ResultResponse;
import Nengcipe.NengcipeBackend.exception.NotFoundException;
import Nengcipe.NengcipeBackend.oauth2.PrincipalDetails;
import Nengcipe.NengcipeBackend.repository.MemberRepository;
import Nengcipe.NengcipeBackend.service.MemberService;
import Nengcipe.NengcipeBackend.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberService memberService, JwtUtil jwtUtil) {
        super(authenticationManager);
        this.memberService=memberService;
        this.jwtUtil=jwtUtil;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String jwt = request.getHeader("Authorization");
        //jwt가 없거나 Bearer로 시작하지 않으면 거부
        if (jwt == null || !jwt.startsWith("Bearer")) {

            chain.doFilter(request, response);
            return;
        }
        //Bearer를 제거하고 jwt 값만 가져옴
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        //만료 여부 체크
        if (jwtUtil.isExpired(token)) {
            log.info("토큰이 만료되었습니다.");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("utf-8");
            ResultResponse res = ResultResponse.builder()
                    .code(HttpServletResponse.SC_FORBIDDEN)
                    .message("토큰 만료")
                    .result(null).build();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String s = objectMapper.writeValueAsString(res);
                PrintWriter writer = response.getWriter();
                writer.write(s);
                return;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        //이 아래 코드가 실행된다는 뜻은 유효한 토큰이라는 뜻
//        Member member = memberService.findById(id);
//        Member member = memberService.findByIdWithIngredients(id);
        PrincipalDetails principalDetails = null;
        Long id = jwtUtil.getId(token);
        String memberId = jwtUtil.getMemberId(token);
        try {
            principalDetails = memberService.findPrincipalDetailsById(id);

        } catch (NotFoundException e) {
            MemberResponseDto responseDto = MemberResponseDto.builder().memberId(memberId).build();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("utf-8");
            log.info("id : {} 해당 유저가 없습니다.", memberId);
            ResultResponse res = ResultResponse.builder()
                    .code(HttpServletResponse.SC_NOT_FOUND)
                    .message("유저 아이디 : 찾지 못 함")
                    .result(responseDto).build();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String s = objectMapper.writeValueAsString(res);
                PrintWriter writer = response.getWriter();
                writer.write(s);
                return;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("id : {} 접근 권한이 존재합니다.", memberId);
        request.setAttribute("token", token);
        chain.doFilter(request, response);
    }

}