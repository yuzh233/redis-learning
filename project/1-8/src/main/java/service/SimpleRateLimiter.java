package service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

/**
 * @author Harry Zhang
 * @since 2020/2/15 20:57
 */
public class SimpleRateLimiter implements RateLimiter {
    private Jedis jedis;

    public SimpleRateLimiter(Jedis jedis) {
        this.jedis = jedis;
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        SimpleRateLimiter limiter = new SimpleRateLimiter(jedis);
        for (int i = 0; i < 20; i++) {
            System.out.println(limiter.isActionAllowed("Harry", "reply", 60, 5));
        }
    }

    @Override
    public boolean isActionAllowed(String userId, String actionKey, int period, int maxCount) {
        String key = String.format("hist:%s:%s", userId, actionKey);
        long nowTs = System.currentTimeMillis(); // 毫秒
        Pipeline pipeline = jedis.pipelined(); // 使用管道批量执行指令
        pipeline.multi();
        pipeline.zadd(key, nowTs, "" + nowTs);
        pipeline.zremrangeByScore(key, 0, nowTs - period * 1000); // 移除"当前时间前60秒"以前的所有 member。单位转换为毫秒
        Response<Long> count = pipeline.zcard(key);
        pipeline.expire(key, period + 1); // period+1 秒后过期
        pipeline.exec();
        pipeline.close();
        return count.get() <= maxCount; // 限定最大次数只能为 maxCount
    }

}
