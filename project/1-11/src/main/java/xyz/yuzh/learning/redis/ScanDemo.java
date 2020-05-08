package xyz.yuzh.learning.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * @author Harry Zhang
 * @since 2020/2/20 12:00
 */
public class ScanDemo {
    public static void main(String[] args) {
        RedisClient client = RedisClient.create("redis://localhost");
        StatefulRedisConnection<String, String> connection = client.connect();
        RedisCommands<String, String> commands = connection.sync();

        for (int i = 0; i < 10000; i++) {
            String re = commands.set(String.format("key%d", i), String.valueOf(i));
            System.out.println(re);
        }

        connection.close();
        client.shutdown();
    }
}
