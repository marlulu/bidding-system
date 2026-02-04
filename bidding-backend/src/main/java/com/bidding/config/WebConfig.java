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
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
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
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                    throws Exception {
                if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                    return true;
                }

                String requestURI = request.getRequestURI();
                log.info("[AuthInterceptor] 处理请求: {}", requestURI);

                String token = request.getHeader(Constants.JWT_HEADER);
                if (token != null && token.startsWith(Constants.JWT_PREFIX)) {
                    token = token.substring(Constants.JWT_PREFIX.length());
                    try {
                        if (jwtUtil.validateToken(token)) {
                            Long userId = jwtUtil.getUserIdFromToken(token);
                            String role = jwtUtil.getRoleFromToken(token);
                            log.info("[AuthInterceptor] Token有效 - URI: {}, userId: {}, role: {}", requestURI, userId,
                                    role);
                            request.setAttribute("userId", userId);
                            request.setAttribute("role", role);
                            if ("SUPPLIER".equals(role)) {
                                Long supplierId = jwtUtil.getSupplierIdFromToken(token);
                                request.setAttribute("supplierId", supplierId);
                            }
                            return true;
                        } else {
                            log.warn("[AuthInterceptor] Token无效或过期 - URI: {}", requestURI);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return false;
                        }
                    } catch (Exception e) {
                        log.error("[AuthInterceptor] Token解析异常 - URI: {}, 错误: {}", requestURI, e.getMessage());
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return false;
                    }
                } else {
                    log.warn("[AuthInterceptor] 请求未携带有效Token - URI: {}, Token存在: {}", requestURI, (token != null));
                }

                // 对于需要登录的接口，如果没 Token 则返回 401
                // 由于我们有部分接口是公开的（如登录），且已通过 excludePathPatterns 排除
                // 这里如果没 Token，暂时放行，由 Controller 判断。
                // 但如果 Token 存在但无效，上面已经拦截了。
                return true;
            }
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/**", "/error", "/static/**", "/files/**", "/common/download",
                        "/common/view",
                        "/swagger-ui/**",
                        "/v3/api-docs/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 注册自定义参数解析器
        resolvers.add(loginUserHandlerMethodArgumentResolver);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射上传文件的路径
        // 映射上传文件的路径
        String userDir = System.getProperty("user.dir");
        // 确保路径中的反斜杠转换为正斜杠 (Windows兼容性)
        String uploadPath = "file:" + userDir.replace("\\", "/") + "/uploads/";

        log.info("Configuring resource handler for path: {}", uploadPath);

        registry.addResourceHandler("/files/**")
                .addResourceLocations(uploadPath);
    }
}
