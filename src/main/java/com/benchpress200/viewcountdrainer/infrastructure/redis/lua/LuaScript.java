package com.benchpress200.viewcountdrainer.infrastructure.redis.lua;

import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

public class LuaScript {
    public static final RedisScript<Long> DELETE_IF_ZERO =
            new DefaultRedisScript<>(
                    """
                    local v = redis.call('GET', KEYS[1])
                    if v == ARGV[1] then
                        return redis.call('DEL', KEYS[1])
                    end
                    return 0
                    """,
                    Long.class
            );

    private LuaScript() {}
}
