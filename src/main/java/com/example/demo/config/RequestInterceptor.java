package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class RequestInterceptor implements HandlerInterceptor{

    private final TokenUtils tokenUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(HttpMethod.OPTIONS.matches(request.getMethod())){
            return true;
        }

        System.out.println("Check preHandle");

        String accessToken = request.getHeader("ACCESS_TOKEN");

        if(accessToken != null) {
            if(tokenUtils.isValidToken(accessToken, "ACCESS")) {
                System.out.println("Has TOKEN");
                return true;
            }
        }

        System.out.println("Has Not TOKEN");
        return false;
    }
}
