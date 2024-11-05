package com.narara.superboard.common.application.handler;

// 필요한 클래스들 임포트
import com.narara.superboard.common.entity.CustomUserDetails;
import com.narara.superboard.member.entity.Member;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

// 이 클래스는 Spring MVC에서 특정 조건에 맞는 메서드 매개변수를 주입하는 커스텀 리졸버입니다.
@Component  // 이 클래스를 Spring Bean으로 등록하여 사용하도록 설정
public class CustomMemberArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 메서드 파라미터가 @AuthenticationPrincipal 애노테이션을 가지고 있고,
     * 그 파라미터 타입이 Member 클래스인 경우 true를 반환합니다.
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 메서드 파라미터에 @AuthenticationPrincipal 애노테이션이 있는지 확인
        // 그리고 메서드 파라미터 타입이 Member 클래스인지 확인
        return parameter.getParameterAnnotation(AuthenticationPrincipal.class) != null
                && parameter.getParameterType().equals(Member.class);
    }

    // 이 메서드는 지원되는 파라미터일 때 해당 파라미터 값을 반환합니다.
    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        // SecurityContextHolder에서 현재 인증된 사용자 정보(Authentication)를 가져와 CustomUserDetails로 캐스팅
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // CustomUserDetails 객체에서 Member 객체를 추출하여 반환
        return customUserDetails.getMember();
    }
}
