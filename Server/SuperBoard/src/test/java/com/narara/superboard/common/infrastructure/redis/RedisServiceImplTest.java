package com.narara.superboard.common.infrastructure.redis;

import com.narara.superboard.common.exception.redis.*;
import com.narara.superboard.common.infrastructure.redis.RedisServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisServiceImplTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisServiceImpl redisService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("getData 성공 케이스 - Redis에서 데이터 조회")
    void testGetData() {
        // Given
        String key = "testKey";
        String expectedValue = "testValue";

        // When
        when(valueOperations.get(key)).thenReturn(expectedValue);

        // Then
        String actualValue = redisService.getData(key);
        assertEquals(expectedValue, actualValue);
        verify(valueOperations, times(1)).get(key);
    }

//    @Test
//    @DisplayName("getData 실패 케이스 - 키가 존재하지 않을 때 null 반환")
//    void testGetDataWhenKeyDoesNotExist() {
//        // Given
//        String key = "nonExistentKey";
//
//        // When: Redis에서 null 반환
//        when(valueOperations.get(key)).thenReturn(null);
//
//        // Then: 값이 없을 때 null을 반환하는지 확인
//        assertNull(redisService.getData(key), "데이터가 없을 때 null이어야 합니다.");
//        verify(valueOperations, times(1)).get(key);
//    }

    @Test
    @DisplayName("setData 성공 케이스 - 만료 시간 없이 데이터 저장")
    void testSetDataWithoutExpireSuccess() {
        String key = "testKey";
        String value = "testValue";

        // 정상적으로 데이터가 저장되는 경우
        redisService.setData(key, value);

        // 데이터가 정상적으로 저장되었는지 확인
        verify(valueOperations, times(1)).set(key, value);
    }

    @Test
    @DisplayName("getData 실패 케이스 - 데이터가 없을 때 예외 발생")
    void testGetDataKeyNotFound() {
        String key = "nonExistentKey";

        // Redis에서 null 반환
        when(valueOperations.get(key)).thenReturn(null);

        // 데이터가 없으므로 RedisDataNotFoundException 예외 발생 예상
        assertThrows(RedisDataNotFoundException.class, () -> redisService.getData(key));
    }

    @Test
    @DisplayName("setData 실패 케이스 - 데이터 저장 실패 시 예외 발생")
    void testSetDataFail() {
        String key = "testKey";
        String value = "testValue";

        // When: Redis에서 예외 발생
        doThrow(new RedisDataSaveException()).when(valueOperations).set(key, value);

        // Then: 저장 실패 예외 발생 확인
        assertThrows(RedisDataSaveException.class, () -> redisService.setData(key, value));
    }

    @Test
    @DisplayName("setData 실패 케이스 - Null 키로 데이터 저장 시 예외 발생")
    void testSetDataNullKey() {
        String key = null;
        String value = "testValue";

        // When: Redis에서 예외 발생
        doThrow(new RedisKeyNotFoundException()).when(valueOperations).set(key, value);

        assertThrows(RedisKeyNotFoundException.class, () -> redisService.setData(key, value));
    }

    @Test
    @DisplayName("setData 실패 케이스 - Null 값으로 데이터 저장 시 예외 발생")
    void testSetDataNullValue() {
        String key = "key";
        String value = null;

        // When: Redis에서 예외 발생
        doThrow(new RedisValueNotFoundException()).when(valueOperations).set(key, value);

        assertThrows(RedisValueNotFoundException.class, () -> redisService.setData(key, value));
    }

    @Test
    @DisplayName("existData 성공 케이스 - Redis에서 키가 존재하는 경우")
    void testExistData() {
        // Given
        String key = "testKey";

        // When: Redis에서 키가 존재하는 경우
        when(redisTemplate.hasKey(key)).thenReturn(true);

        // Then: 키가 존재하는지 확인
        assertTrue(redisService.existData(key));
        verify(redisTemplate, times(1)).hasKey(key);
    }

    @Test
    @DisplayName("existData 실패 케이스 - 키가 존재하지 않는 경우")
    void testExistDataWhenKeyDoesNotExist() {
        // Given
        String key = "nonExistentKey";

        // When: Redis에서 키가 없는 경우
        when(redisTemplate.hasKey(key)).thenReturn(false);

        // Then: 키가 없을 때 false를 반환하는지 확인
        assertFalse(redisService.existData(key));
        verify(redisTemplate, times(1)).hasKey(key);
    }

    @Test
    @DisplayName("setDataExpire 성공 케이스 - 만료 시간 설정 시 데이터 저장")
    void testSetDataExpireSuccess() {
        String key = "testKey";
        String value = "testValue";
        long timeoutInSeconds = 60;

        // 정상적으로 데이터가 저장되는 경우
        redisService.setDataExpire(key, value, timeoutInSeconds);
        Duration duration = Duration.ofSeconds(timeoutInSeconds);

        // 데이터와 만료 시간이 정상적으로 설정되었는지 확인
        verify(valueOperations, times(1)).set(key, value, timeoutInSeconds, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("setDataExpire 실패 케이스 - 만료 시간 설정 실패 시 예외 발생")
    void testSetDataExpireFail() {
        String key = "testKey";
        String value = "testValue";
        long timeoutInSeconds = 60;

        // Redis에서 예외 발생
        doThrow(new RuntimeException("Redis 만료 설정 오류")).when(valueOperations).set(key, value, timeoutInSeconds, TimeUnit.SECONDS);

        // 만료 시간 설정 실패 예외 발생
        assertThrows(RedisDataSaveException.class, () -> redisService.setDataExpire(key, value, timeoutInSeconds));
    }

    @Test
    @DisplayName("setDataExpire 실패 케이스 - 잘못된 만료 시간 설정 시 예외 발생")
    void testSetDataExpireWithInvalidTimeout() {
        String key = "testKey";
        String value = "testValue";
        long invalidTimeout = -1;

        // When & Then: 만료 시간이 유효하지 않을 때 예외 발생 확인
        assertThrows(RedisInvalidDurationFormatException.class, () -> redisService.setDataExpire(key, value, invalidTimeout));
    }

    @Test
    @DisplayName("setDataExpire 실패 케이스 - 만료 시간이 0일 때 예외 발생")
    void testSetDataExpireWithZeroTimeout() {
        String key = "testKey";
        String value = "testValue";
        long zeroTimeout = 0;

        // When & Then: 만료 시간이 0일 때 예외 발생 확인
        assertThrows(RedisInvalidDurationFormatException.class, () -> redisService.setDataExpire(key, value, zeroTimeout));
    }

    @Test
    @DisplayName("setDataExpire 실패 케이스 - Redis 연결 실패 시 예외 발생")
    void testSetDataExpireWithConnectionFailure() {
        String key = "testKey";
        String value = "testValue";
        long timeoutInSeconds = 60;

        // Redis에서 예외 발생
        doThrow(new RedisConnectionFailureException("Redis 연결 실패")).when(valueOperations).set(key, value, timeoutInSeconds, TimeUnit.SECONDS);

        // 예외 발생 확인
        assertThrows(RedisDataSaveException.class, () -> redisService.setDataExpire(key, value, timeoutInSeconds));
    }

    @Test
    @DisplayName("deleteData 성공 케이스 - 데이터 삭제 성공")
    void testDeleteDataSuccess() {
        String key = "testKey";

        // Redis에서 정상적으로 삭제되는 경우
        when(redisTemplate.delete(key)).thenReturn(true);

        // 삭제가 성공했는지 확인
        redisService.deleteData(key);
        verify(redisTemplate, times(1)).delete(key);
    }

    @Test
    @DisplayName("deleteData 실패 케이스 - 데이터가 존재하지 않을 때 삭제 요청")
    void testDeleteDataWhenKeyDoesNotExist() {
        String key = "nonExistentKey";

        // Redis에서 데이터가 존재하지 않을 경우에도 delete 호출
        when(redisTemplate.delete(key)).thenReturn(false);

        // 데이터가 없지만 예외 없이 실행되는지 확인
        assertDoesNotThrow(() -> redisService.deleteData(key), "데이터가 존재하지 않아도 예외가 발생하지 않아야 합니다.");

        // delete가 호출되었는지 확인
        verify(redisTemplate, times(1)).delete(key);
    }

    @Test
    @DisplayName("deleteData 실패 케이스 - 데이터 삭제 실패 시 예외 발생")
    void testDeleteDataFail() {
        String key = "testKey";

        // Redis에서 삭제 실패
        doThrow(new RuntimeException("Redis 삭제 오류")).when(redisTemplate).delete(key);

        // 삭제 실패 예외 발생
        assertThrows(RedisDataDeleteException.class, () -> redisService.deleteData(key));
    }
}
