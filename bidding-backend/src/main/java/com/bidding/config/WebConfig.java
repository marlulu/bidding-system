package com.bidding.config;

import com.bidding.common.Constants;
import com.bidding.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                if ("OPTIONS".equals(request.getMethod())) {
                    return true;
                }
                
                String token = request.getHeader(Constants.JWT_HEADER);
                if (token != null && token.startsWith(Constants.JWT_PREFIX)) {
                    token = token.substring(Constants.JWT_PREFIX.length());
                    if (jwtUtil.validateToken(token)) {
                        request.setAttribute("userId", jwtUtil.getUserIdFromToken(token));
                        request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
                        request.setAttribute("role", jwtUtil.getRoleFromToken(token));
                        return true;
                    }
                }
                
                // 检查是否是公开接口
                String path = request.getRequestURI();
                if (path.equals("/api/announcements") || path.equals("/api/suppliers") || path.equals("/api/policies")) {
                    return true;
                }
                
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"未授权，请先登录\"}");
                return false;
            }
        }).addPathPatterns("/api/**")
          .excludePathPatterns("/api/auth/login", "/api/auth/register");
    }
}
