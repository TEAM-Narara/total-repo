package com.narara.superboard;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * MockSuperBoardUnitTests
 *
 * 이 인터페이스는 Mockito를 사용한 단위 테스트에서 공통 설정을 제공하는 역할을 합니다.
 *
 * <p>
 * 이 인터페이스를 구현하는 테스트 클래스는 Mockito 설정이 자동으로 적용되며,
 * @Mock, @InjectMocks 등의 Mockito 어노테이션을 사용할 수 있습니다.
 * </p>
 *
 * <p>
 * 주요 장점:
 * 1. 재사용성: 여러 테스트 클래스에서 반복되는 Mockito 설정을 방지.
 * 2. 테스트 간소화: 추가 설정 없이 @Mock, @InjectMocks 등을 쉽게 사용 가능.
 * 3. 코드 간결화: 반복적인 Mockito 설정을 추상화하여 코드가 더 깔끔해짐.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
public interface MockSuperBoardUnitTests {
}
