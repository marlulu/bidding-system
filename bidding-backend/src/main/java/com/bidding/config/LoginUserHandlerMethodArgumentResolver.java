package com.bidding.config;

import com.bidding.common.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 检查参数是否带有 @LoginUser 注解且类型为 Long
        return parameter.hasParameterAnnotation(LoginUser.class) && 
               parameter.getParameterType().isAssignableFrom(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        // 从拦截器存入的属性中获取 userId
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            // 如果拦截器没存入，尝试从 Header 再次解析（双重保险）
            // 这里暂不实现，依赖拦截器的存入逻辑
            return null;
        }
        return userId;
    }
}
