package com.narara.superboard.common.config.docs;
import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Component
public class SwaggerCodeBlockTransformer extends SwaggerIndexPageTransformer {
    /**
     * Instantiates a new Swagger index transformer.
     *
     * @param swaggerUiConfig           the swagger ui config
     * @param swaggerUiOAuthProperties  the swagger ui o auth properties
     * @param swaggerUiConfigParameters the swagger ui config parameters
     * @param swaggerWelcomeCommon      the swagger welcome common
     * @param objectMapperProvider      the object mapper provider
     */
    public SwaggerCodeBlockTransformer(SwaggerUiConfigProperties swaggerUiConfig, SwaggerUiOAuthProperties swaggerUiOAuthProperties, SwaggerUiConfigParameters swaggerUiConfigParameters, SwaggerWelcomeCommon swaggerWelcomeCommon, ObjectMapperProvider objectMapperProvider) {
        super(swaggerUiConfig, swaggerUiOAuthProperties, swaggerUiConfigParameters, swaggerWelcomeCommon, objectMapperProvider);
    }

    // < constructor >

    @Override
    public Resource transform(HttpServletRequest request, Resource resource,
                              ResourceTransformerChain transformer) throws IOException {
        // index.html을 수정하여 theme-material.css를 포함
        if (resource.toString().contains("index.html")) {
            try (InputStream is = resource.getInputStream();
                 BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

                String html = br.lines().collect(Collectors.joining("\n"));

                // theme-material.css를 추가하는 링크 태그 (절대 경로로 설정)
                String themeLink = "<link rel=\"stylesheet\" type=\"text/css\" href=\"/theme-material.css\">";

                // </head> 태그 바로 앞에 theme-material.css 링크 삽입
                String transformedHtml = html.replace("</head>", themeLink + "\n</head>");

                // 변환된 HTML 반환
                return new TransformedResource(resource, transformedHtml.getBytes());
            }
        }
        return super.transform(request, resource, transformer);
    }


}