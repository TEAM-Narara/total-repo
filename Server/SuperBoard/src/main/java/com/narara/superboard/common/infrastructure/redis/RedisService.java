package com.narara.superboard.common.infrastructure.redis;

public interface RedisService {
    String getData(String key);
    void setData(String key,String value);
    boolean existData(String key);
    void setDataExpire(String key, String value, long duration);
    void deleteData(String key);

}
