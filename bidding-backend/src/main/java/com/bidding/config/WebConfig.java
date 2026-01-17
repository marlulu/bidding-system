package com.bidding.config;

import com.bidding.common.Constants;
import com.bidding.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private LoginUserHandlerMethodArgumentResolver loginUserHandlerMethodArgumentResolver;

    @Bean
    public HandlerInterceptor jwtInterceptor() {
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                    return true;
                }
                
                String token = request.getHeader(Constants.JWT_HEADER);
                if (token != null && token.startsWith(Constants.JWT_PREFIX)) {
                    token = token.substring(Constants.JWT_PREFIX.length());
                    try {
                        if (jwtUtil.validateToken(token)) {
                            Long userId = jwtUtil.getUserIdFromToken(token);
                            String role = jwtUtil.getRoleFromToken(token);
                            log.info("[AuthInterceptor] 成功解析 Token, userId: {}, role: {}", userId, role);
                            // 存入 request 属性，供参数解析器使用
                            request.setAttribute("userId", userId);
                            request.setAttribute("role", role);
                            log.info("[AuthInterceptor] 已将 userId 和 role 存入 request 属性");
                            // 如果是供应商，则从 token 中获取 supplierId
                            if ("SUPPLIER".equals(role)) {
                                Long supplierId = jwtUtil.getSupplierIdFromToken(token);
                                request.setAttribute("supplierId", supplierId);
                            }
                            return true;
                        }
                    } catch (Exception e) {
                        log.error("Token 解析失败: {}", e.getMessage());
                    }
                }
                
                // 对于需要登录的接口，如果没 Token 则返回 401
                // 注意：addInterceptors 中已配置了排除路径
                return true; // 先放行，让 Controller 层的 @LoginUser 处理 null 情况或由业务逻辑判断
            }
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/api/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 注册自定义参数解析器
        resolvers.add(loginUserHandlerMethodArgumentResolver);
    }
}
