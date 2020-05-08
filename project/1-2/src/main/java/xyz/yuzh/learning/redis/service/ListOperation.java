package xyz.yuzh.learning.redis.service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Harry Zhang
 * @since 2020/2/4 13:19
 */
public class ListOperation {
    private static final JedisPool POOL = new JedisPool(new JedisPoolConfig(), "localhost");

    public static void main(String[] args) {
        Jedis jedis = null;
        try {
            jedis = POOL.getResource();
            System.out.println("rpush: " + jedis.rpush("books", "Java", "Python", "Golang"));
            System.out.println("llen: " + jedis.llen("books"));
            System.out.println("lpop: " + jedis.lpop("books"));
            System.out.println("lpop: " + jedis.lpop("books"));
            System.out.println("lpop: " + jedis.lpop("books"));
            System.out.println("lpop: " + jedis.lpop("books"));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        POOL.close();
    }
}
