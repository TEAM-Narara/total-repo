package com.narara.superboard.common.application.validator;

import com.narara.superboard.common.exception.NotFoundNameException;
import com.narara.superboard.common.interfaces.dto.NameHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("이름 검증에 대한 단위 테스트")
class NameValidatorTest {

    /**
     * @InjectMocks와 new 키워드의 차이점
     * 1. 직접 객체 생성 (new):
     *    - 객체를 수동으로 생성한다.
     *    - 의존성 주입을 수동으로 처리해야 한다.
     *    - 단순한 클래스나 의존성이 없는 클래스에 적합.
     *
     * 2. @InjectMocks:
     *    - Mockito가 객체를 자동으로 생성하고, @Mock으로 선언된 의존성을 자동으로 주입한다.
     *    - 의존성이 있는 클래스에 적합하며, 테스트에서 모킹된 의존성을 쉽게 관리할 수 있다.
     */
    private NameValidator nameValidator; // InjectMocks 대신 직접 객체를 생성
    private NameHolder nameHolder;

    @BeforeEach
    void setUp() {
        nameValidator = new NameValidator(); // 직접 객체 생성
        nameHolder = Mockito.mock(NameHolder.class);  // NameHolder 인터페이스 모킹
    }

    @ParameterizedTest
    @CsvSource({
            "null",    // 이름이 null인 경우
            "''"       // 이름이 빈 문자열인 경우
    })
    @DisplayName("이름이 null이거나 빈 문자열이면 NotFoundNameException 예외 발생")
    void testValidateNameIsEmpty_Exception(String invalidName) {
        // given: null 문자열을 실제 null 값으로 변환
        String name = "null".equals(invalidName) ? null : invalidName;
        NameHolder nameHolder = new SimpleNameHolder(name); // 실제 구현체 사용

        // then
        assertThrows(NotFoundNameException.class, () -> nameValidator.validateNameIsEmpty(nameHolder));
    }

    @ParameterizedTest
    @CsvSource({
            "'ValidName'",    // 유효한 이름
            "'Another ValidName'"  // 또 다른 유효한 이름
    })
    @DisplayName("이름이 유효할 경우 예외가 발생하지 않음")
    void testValidateNameIsEmpty_Success(String validName) {
        // given
        NameHolder nameHolder = new SimpleNameHolder(validName); // 실제 구현체 사용

        // then: 예외가 발생하지 않음
        assertDoesNotThrow(() -> nameValidator.validateNameIsEmpty(nameHolder));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("카드 이름이 비어있거나 null일 때 예외 발생")
    void shouldFailWhenCardNameIsEmptyOrNull(String cardName) {
        // given
        NameHolder nameHolder = new SimpleNameHolder(cardName); // 실제 구현체 사용

        // when & then
        NotFoundNameException exception = assertThrows(NotFoundNameException.class, () -> {
            nameValidator.validateCardNameIsEmpty(nameHolder);
        });

        // then
        assertEquals("카드의 이름(이)가 존재하지 않습니다. 이름(을)를 작성해주세요.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Valid Name", "Test Card"})
    @DisplayName("성공 로직: 카드 이름이 정상적으로 존재할 때 예외 없음")
    void shouldPassWhenCardNameIsValid(String cardName) {
        // given
        NameHolder nameHolder = new SimpleNameHolder(cardName); // 실제 구현체 사용

        // when & then
        assertDoesNotThrow(() -> nameValidator.validateCardNameIsEmpty(nameHolder));
    }

    record SimpleNameHolder(String name) implements NameHolder {
    }

}
