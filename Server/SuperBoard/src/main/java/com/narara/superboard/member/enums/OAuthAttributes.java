package com.narara.superboard.member.enums;

import com.narara.superboard.member.interfaces.dto.MemberProfile;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuthAttributes {
    NAVER("naver", (attributes) -> {
        Map<String, Object> response = (Map<String, Object>) attributes.getOrDefault("response", Map.of());
        return new MemberProfile(
                (String) response.get("nickname"),
                (String) response.get("email")
        );
    }),

    // 수정하기
    GITHUB("github", (attributes) -> {
        String login = (String) attributes.get("login");
        String email = (String) attributes.get("email");
        return new MemberProfile(
                login,
                email != null ? email : login // email이 null인 경우 login을 대신 사용
        );
    });

    private final String registrationId;
    private final Function<Map<String, Object>, MemberProfile> of;

    OAuthAttributes(String registrationId, Function<Map<String, Object>, MemberProfile> of) {
        this.registrationId = registrationId;
        this.of = of;
    }

    public static MemberProfile extract(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> registrationId.equals(provider.registrationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No provider found with registrationId " + registrationId))
                .of.apply(attributes);
    }

}
