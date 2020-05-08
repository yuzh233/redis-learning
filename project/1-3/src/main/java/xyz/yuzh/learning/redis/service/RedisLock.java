package xyz.yuzh.learning.redis.service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Harry Zhang
 * @since 2020/2/4 13:48
 */
public class RedisLock implements Lock {
    private static final JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
    private static Map<Long, Jedis> jedisMap = new HashMap<>();

    @Override
    public boolean lock(String key) {
        return lock(key, Thread.currentThread().getName(), 30);
    }

    @Override
    public boolean lock(String key, Integer expire) {
        return lock(key, Thread.currentThread().getName(), expire);
    }

    @Override
    public boolean lock(String key, String value) {
        return lock(key, value, 30);
    }

    @Override
    public boolean lock(String key, String value, Integer expire) {
        Jedis jedis = getResource();
        if (null == jedis) {
            return false;
        }
        // boolean lock = "OK".equalsIgnoreCase(jedis.set(key, value, "nx", "ex", expire));
        SetParams setParams = new SetParams();
        setParams.nx().ex(expire);
        boolean lock = "OK".equalsIgnoreCase(jedis.set(key, value, setParams));
        if (!lock) {
            closeJedisInstance();
        }
        return lock;
    }

    @Override
    public boolean unlock(String key) {
        return unlock(key, null);
    }

    @Override
    public boolean unlock(String key, String value) {
        Jedis jedis = getResource();
        try {
            if (null == jedis) {
                return false;
            }

            String val = jedis.get(key);
            if (null == val) {
                return true;
            }

            if (null == value) {
                return 1 == jedis.del(key);
            }

            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] " +
                    "then" +
                    "   return redis.call('del', KEYS[1])" +
                    "else " +
                    "   return 0 " +
                    "end";
            Long result = (Long) jedis.eval(luaScript, Collections.singletonList(key), Collections.singletonList(value));
            return 1 == result;
        } finally {
            this.closeJedisInstance();
        }
    }

    @Override
    public Long expire(String key, Integer expire) {
        Jedis jedis = getResource();
        if (null == jedis) {
            return 0L;
        }
        Long re = jedis.expire(key, expire);
        if (1 != re) {
            closeJedisInstance();
        }
        return re;
    }

    /**
     * 获取 Jedis 实例
     * <p>
     * 注意一个线程一个实例，每个线程的 jedis 实例会放入到 jedisMap 缓存起来。
     */
    private Jedis getResource() {
        Jedis jedis = jedisMap.get(Thread.currentThread().getId());
        if (null != jedis) {
            return jedis;
        }
        jedis = pool.getResource();
        jedisMap.put(Thread.currentThread().getId(), jedis);
        return jedis;
    }

    private void closeJedisInstance() {
        Jedis jedis = jedisMap.get(Thread.currentThread().getId());
        if (null != jedis) {
            jedis.close();
            jedisMap.remove(Thread.currentThread().getId());
        }
    }

}

