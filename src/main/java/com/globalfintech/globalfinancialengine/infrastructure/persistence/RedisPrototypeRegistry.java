package com.globalfintech.globalfinancialengine.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RedisPrototypeRegistry {
    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;

    public RedisPrototypeRegistry(StringRedisTemplate redis) {
        this.redis = redis;
        this.mapper = new ObjectMapper();
    }

    public void register(String key, Object template) {
        try {
            redis.opsForValue().set(
                "globalfintech:prototype:" + key,
                mapper.writeValueAsString(template)
            );
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                "No se pudo serializar la plantilla", e);
        }
    }

    public <T> Optional<T> find(String key, Class<T> type) {
        String json = redis.opsForValue().get(
            "globalfintech:prototype:" + key);

        if (json == null) return Optional.empty();

        try {
            return Optional.of(mapper.readValue(json, type));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                "No se pudo recuperar la plantilla", e);
        }
    }

    public void remove(String key) {
        redis.delete("globalfintech:prototype:" + key);
    }
}
