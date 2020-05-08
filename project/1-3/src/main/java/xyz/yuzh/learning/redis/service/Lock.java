package xyz.yuzh.learning.redis.service;

/**
 * @author Harry Zhang
 * @since 2020/2/4 13:46
 */
public interface Lock {

    boolean lock(String key);

    boolean lock(String key, Integer expire);

    boolean lock(String key, String value);

    boolean lock(String key, String value, Integer expire);

    boolean unlock(String key);

    boolean unlock(String key, String value);

    Long expire(String key, Integer expire);

}
