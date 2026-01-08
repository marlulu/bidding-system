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
                
                // 默认拦截所有未匹配 excludePathPatterns 的请求
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"未授权，请先登录\"}");
                return false;
            }
        }).addPathPatterns("/api/**")
          .excludePathPatterns(
              "/api/auth/login", 
              "/api/auth/register",
              "/api/announcements",  // 招标列表
              "/api/suppliers",      // 供应商列表
              "/api/policies"        // 政策列表
          );
    }
}
