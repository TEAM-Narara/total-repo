package com.narara.superboard.common.config.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;


@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "슈퍼보드 통합 서버 API 명세서",
                description = """
                        ## 401: 만료된 토큰 or 유효하지 않은 토큰 사용
                        ## 403: 사용자 권한 없음
                        ## 404: 잘못된 입력
                        ## 500: 서버 에러
                        """,
                version = "v1"
        ),
        servers = {
                @Server(url = "/", description = "통합 API 서버")
        },
        tags = {
                @Tag(name = "1. 인증"),
                @Tag(name = "2. 이메일"),
                @Tag(name = "3. 회원"),
                @Tag(name = "4. 워크스페이스"),
                @Tag(name = "5. 보드"),
                @Tag(name = "6. 리스트"),
                @Tag(name = "7. 카드"),
                @Tag(name = "8. 댓글"),
                @Tag(name = "9. 첨부파일"),
                @Tag(name = "a. 워크스페이스 회원"),
                @Tag(name = "b. 보드 회원"),
                @Tag(name = "c. 리스트 회원"),
                @Tag(name = "d. 카드 회원"),
                @Tag(name = "e. 라벨"),
                @Tag(name = "f. 카드 라벨"),
                @Tag(name = "g. 회원 배경"),
                @Tag(name = "h. FCM 토큰"),
        }
)
public class SwaggerConfiguration {

    @Bean
    public SwaggerCodeBlockTransformer swaggerCodeBlockTransformer(
            SwaggerUiConfigProperties swaggerUiConfig,
            SwaggerUiOAuthProperties swaggerUiOAuthProperties,
            SwaggerUiConfigParameters swaggerUiConfigParameters,
            SwaggerWelcomeCommon swaggerWelcomeCommon,
            ObjectMapperProvider objectMapperProvider) {

        return new SwaggerCodeBlockTransformer(swaggerUiConfig, swaggerUiOAuthProperties,
                swaggerUiConfigParameters, swaggerWelcomeCommon, objectMapperProvider);
    }

    /**
     * 이 코드는 ObjectMapper의 네이밍 전략을 snake_case로 설정한 후,
     * 해당 ObjectMapper를 사용하는 **ModelResolver**를 Spring 빈으로 등록합니다.
     * 이렇게 설정하면, Swagger나 다른 API 문서화 도구에서 snake_case를 사용하여 모델의 필드 이름을 처리하게 됩니다.
     * @param objectMapper
     * @return
     */
    @Bean
    public ModelResolver modelResolver(ObjectMapper objectMapper) {
        return new ModelResolver(objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE));
    }


    @Bean
    public OpenAPI customOpenAPI() {
        final String jwtSchemeName = "jwtAuth";

        // JWT 토큰을 위한 SecurityScheme 정의
        SecurityScheme jwtScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

//        SecurityRequirement 정의
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(jwtSchemeName);

        return new OpenAPI()
                .components(new Components()
                                .addSecuritySchemes(jwtSchemeName, jwtScheme)

                )
                .security(Collections.singletonList(securityRequirement));
    }

}
