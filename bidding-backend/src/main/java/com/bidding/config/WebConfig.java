package com.bidding.config;

import com.bidding.common.Constants;
import com.bidding.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public HandlerInterceptor jwtInterceptor() {
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                // 放行 OPTIONS 请求
                if ("OPTIONS".equals(request.getMethod())) {
                    return true;
                }
                
                String token = request.getHeader(Constants.JWT_HEADER);
                log.info("拦截器接收到请求: {}, Token: {}", request.getRequestURI(), token != null ? "已携带" : "未携带");

                if (token != null && token.startsWith(Constants.JWT_PREFIX)) {
                    token = token.substring(Constants.JWT_PREFIX.length());
                    try {
                        if (jwtUtil.validateToken(token)) {
                            Long userId = jwtUtil.getUserIdFromToken(token);
                            String username = jwtUtil.getUsernameFromToken(token);
                            String role = jwtUtil.getRoleFromToken(token);
                            
                            log.info("Token 验证成功: userId={}, username={}", userId, username);
                            
                            request.setAttribute("userId", userId);
                            request.setAttribute("username", username);
                            request.setAttribute("role", role);
                            return true;
                        }
                    } catch (Exception e) {
                        log.error("Token 验证异常: {}", e.getMessage());
                    }
                }
                
                log.warn("Token 验证失败或未携带 Token, 路径: {}", request.getRequestURI());
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"未授权，请先登录\"}");
                return false;
            }
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
          .addPathPatterns("/api/**")
          .excludePathPatterns(
              "/api/auth/login", 
              "/api/auth/register",
              "/api/announcements",
              "/api/suppliers",
              "/api/policies"
          );
    }
}
