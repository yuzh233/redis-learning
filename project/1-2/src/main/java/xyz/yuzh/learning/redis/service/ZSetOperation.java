package xyz.yuzh.learning.redis.service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;
import java.util.HashMap;

/**
 * @author Harry Zhang
 * @since 2020/2/4 13:19
 */
public class ZSetOperation {
    private static final JedisPool POOL = new JedisPool(new JedisPoolConfig(), "localhost");

    public static void main(String[] args) {
        Jedis jedis = null;
        try {
            jedis = POOL.getResource();
            jedis.zadd("score", 1.01, "Tom");
            jedis.zadd("score", 1.02, "Jack");
            jedis.zadd("score", 60.234, "Jessica");
            jedis.zadd("score", 100, "Harry");
            HashMap<String, Double> zadds = new HashMap<String, Double>();
            zadds.put("John", 32.31);
            zadds.put("Lisa", 90.1);
            jedis.zadd("score", zadds);

            jedis.zrange("score", 0, -1).forEach(System.out::println);
            System.out.println("------");
            jedis.zrangeWithScores("score", 0, -1).forEach(member -> {
                System.out.println(member.getElement() + ", " + member.getScore());
            });
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        POOL.close();
    }
}
