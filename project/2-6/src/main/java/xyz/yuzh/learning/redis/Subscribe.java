package xyz.yuzh.learning.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

/**
 * 消息订阅者
 *
 * @author Harry Zhang
 * @since 2020/4/10 01:26
 */
public class Subscribe {
    public static final JedisPool POOL = new JedisPool(new JedisPoolConfig(), "localhost");

    public static void main(String[] args) {
        Jedis jedis = POOL.getResource();
        // jedis.subscribe(new Subscriber(), "codehole");
        jedis.psubscribe(new Subscriber(), "code*");
    }

    static class Subscriber extends JedisPubSub {
        @Override
        public void onMessage(String channel, String message) {
            System.out.println(String.format("[接收到消息] channel: %s, message: %s", channel, message));
        }

        @Override
        public void onPMessage(String pattern, String channel, String message) {
            System.out.println(String.format("[接收到模式匹配消息] pattern: %s, channel: %s, message: %s", pattern, channel,
                    message));
        }

        @Override
        public void onSubscribe(String channel, int subscribedChannels) {
            System.out.println(String.format("[订阅成功] channel: %s, subscribedChannels: %s", channel,
                    subscribedChannels));
        }

        @Override
        public void onPSubscribe(String pattern, int subscribedChannels) {
            System.out.println(String.format("[模式订阅成功] pattern: %s, subscribedChannels: %s", pattern,
                    subscribedChannels));
        }
    }
}
