package xyz.yuzh.learning.redis.service;

import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import xyz.yuzh.learning.redis.domain.Person;

import java.util.Arrays;

/**
 * @author Harry Zhang
 * @since 2020/2/2 19:42
 */
public class StringOperation {

    // not threadsafe
    // Jedis redis = new Jedis("localhost");

    // threadsafe
    public static final JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");

    public static void main(String[] args) {
        Person harry = new Person();
        harry.setName("Harry");
        harry.setAge(22);
        harry.setHobby(Arrays.asList("Java", "Groovy", "Python"));

        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set("Harry", serializer(harry));

            String redisHarry = jedis.get("Harry");
            System.out.println(redisHarry);
            System.out.println(deserializer(redisHarry));
        } finally {
            if (null != jedis) jedis.close();
        }

        // ... when closing your application:
        pool.close();
    }

    public static Person deserializer(String json) {
        return JSONObject.parseObject(json, Person.class);
    }

    public static String serializer(Person person) {
        return JSONObject.toJSONString(person);
    }

}
