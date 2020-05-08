package xyz.yuzh.learning.redis.service;

import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import xyz.yuzh.learning.redis.domain.Person;

import java.util.Arrays;
import java.util.List;

/**
 * @author Harry Zhang
 * @since 2020/2/2 20:22
 */
public class HashOperation {
    public static final JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");

    public static void main(String[] args) {
        Person jessica = new Person();
        jessica.setName("Jessica");
        jessica.setAge(22);
        jessica.setHobby(Arrays.asList("Java", "Groovy", "Python"));

        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.hset("Jessica", "name", jessica.getName());
            jedis.hset("Jessica", "age", jessica.getAge().toString());
            jedis.hset("Jessica", "hobby", JSONObject.toJSONString(jessica.getHobby()));

            System.out.println(jedis.hget("Jessica", "name"));
            System.out.println(jedis.hget("Jessica", "age"));
            System.out.println(JSONObject.parseObject(jedis.hget("Jessica", "hobby"), List.class));
        } finally {
            if (null != jedis) jedis.close();
        }
        pool.close();
    }
}
