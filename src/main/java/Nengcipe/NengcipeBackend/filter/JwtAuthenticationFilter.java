package Nengcipe.NengcipeBackend.filter;

import Nengcipe.NengcipeBackend.dto.JwtResponse;
import Nengcipe.NengcipeBackend.dto.MemberDto;
import Nengcipe.NengcipeBackend.dto.MemberResponseDto;
import Nengcipe.NengcipeBackend.dto.ResultResponse;
import Nengcipe.NengcipeBackend.exception.NotFoundException;
import Nengcipe.NengcipeBackend.oauth2.PrincipalDetails;
import Nengcipe.NengcipeBackend.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;


    //로그인을 시도할 때 실행
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {
        try {
            MemberDto memberDto = objectMapper.readValue(request.getInputStream(), MemberDto.class); //request로 들어온 JSON 형식을 MemberDto로 가져옴
            if (memberDto.getMemberId() == null) {
                writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "아이디는 필수 값입니다.");
                return null;
            } else if (memberDto.getPassword() == null) {
                writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "비밀번호는 필수 값입니다.");
                return null;

            }
            log.info("id : {} 로그인 시도", memberDto.getMemberId());
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(memberDto.getMemberId(), memberDto.getPassword());
            Authentication authenticate = authenticationManager.authenticate(token);
            return authenticate;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InternalAuthenticationServiceException e) {
            writeErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,e.getMessage());
        }

        return null;
    }
    //인증을 성공하면 실행
    //response Authorization header에 jwt를 담아서 보내줌
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult)
    {
        PrincipalDetails member = (PrincipalDetails) authResult.getPrincipal();
        String jwt = jwtUtil.createJwt(member.getMemberId(), member.getId());
        log.info("id : {} 로그인 성공", member.getMemberId());
        //서블릿으로 JSON 응답
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        ResultResponse res = ResultResponse.builder()
                .code(HttpServletResponse.SC_OK)
                .message("로그인 성공")
                .result(new JwtResponse("Bearer ")+jwt).build();
        try {
            String s = objectMapper.writeValueAsString(res);
            PrintWriter writer = response.getWriter();
            writer.write(s);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
//    @Override
//    protected void unsuccessfulAuthentication(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            AuthenticationException failed
//    ) {
//        //비밀번호 틀림
//        writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, failed.getMessage());
//    }
    private void writeErrorResponse(HttpServletResponse response, int status, String message) {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        ResultResponse res = ResultResponse.builder()
                .code(status)
                .message(message).build();
        try {
            String s = objectMapper.writeValueAsString(res);
            PrintWriter writer = response.getWriter();
            writer.write(s);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}