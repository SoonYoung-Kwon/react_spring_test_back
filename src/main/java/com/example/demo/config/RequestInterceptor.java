package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class RequestInterceptor implements HandlerInterceptor{ // 여기에서 token 생성할 수 있도록 참조해와도 되나?
                                                                // preHandle에 걸렸다면 accessToken은 항상 새로 발급하도록 하여야 하는가?
                                                                // 인증이 필요한 곳에서 발급 vs 그렇지 않은곳도 발급할 수 있도록

    private final TokenUtils tokenUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if(HttpMethod.OPTIONS.matches(request.getMethod())){
            return true;
        }

        System.out.println("Check preHandle");

        String accessToken = request.getHeader("ACCESS_TOKEN");

        if(accessToken != null) {
            System.out.println("Has TOKEN");
            return true;
        }

        System.out.println("Has Not TOKEN");
        return false;
    }
}
