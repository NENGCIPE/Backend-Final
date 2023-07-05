package Nengcipe.NengcipeBackend.util;

import Nengcipe.NengcipeBackend.dto.ResultResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Autowired
    private final ObjectMapper objectMapper;
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        System.out.println("exceptionalhandleing");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        ResultResponse res = ResultResponse.builder()
                .code(response.getStatus())
                .message(accessDeniedException.getMessage()).build();
        try {
            String s = objectMapper.writeValueAsString(res);
            PrintWriter writer = response.getWriter();
            writer.write(s);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
