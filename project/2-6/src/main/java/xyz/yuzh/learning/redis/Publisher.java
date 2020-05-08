package xyz.yuzh.learning.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 消息发布者
 *
 * @author Harry Zhang
 * @since 2020/4/10 01:26
 */
public class Publisher {
    public static final JedisPool POOL = new JedisPool(new JedisPoolConfig(), "localhost");

    public static void main(String[] args) {
        Jedis jedis = POOL.getResource();
        jedis.publish("codehole", "python comes");
        jedis.publish("codehole", "java comes");
        jedis.publish("codehole", "golang comes");
    }

}
