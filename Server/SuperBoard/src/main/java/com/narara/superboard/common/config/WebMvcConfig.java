package com.narara.superboard.common.config;

import com.narara.superboard.common.application.handler.CustomMemberArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final CustomMemberArgumentResolver customMemberArgumentResolver;

    public WebMvcConfig(CustomMemberArgumentResolver customMemberArgumentResolver) {
        this.customMemberArgumentResolver = customMemberArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(customMemberArgumentResolver);
    }
}
