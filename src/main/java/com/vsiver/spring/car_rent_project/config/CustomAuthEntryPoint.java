package com.vsiver.spring.car_rent_project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsiver.spring.car_rent_project.dtos.InfoMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;


@Slf4j
@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("Auth error");
        InfoMessage re = new InfoMessage();
        re.setInfo(authException.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        OutputStream responseStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(responseStream, re);
        responseStream.flush();
    }
}
