package com.narara.superboard.common.config;

import com.narara.superboard.common.application.handler.CustomMemberArgumentResolver;
import com.narara.superboard.common.config.docs.SwaggerCodeBlockTransformer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final CustomMemberArgumentResolver customMemberArgumentResolver;
    private final SwaggerCodeBlockTransformer swaggerCodeBlockTransformer;

    public WebMvcConfig(CustomMemberArgumentResolver customMemberArgumentResolver, SwaggerCodeBlockTransformer swaggerCodeBlockTransformer) {
        this.customMemberArgumentResolver = customMemberArgumentResolver;
        this.swaggerCodeBlockTransformer = swaggerCodeBlockTransformer;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(customMemberArgumentResolver);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .resourceChain(true)
                .addTransformer(swaggerCodeBlockTransformer);
    }
}
