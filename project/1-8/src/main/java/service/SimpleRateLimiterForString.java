package service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

/**
 * @author Harry Zhang
 * @since 2020/2/16 13:24
 */
public class SimpleRateLimiterForString implements RateLimiter {
    private Jedis jedis;

    public SimpleRateLimiterForString(Jedis jedis) {
        this.jedis = jedis;
    }

    public static void main(String[] args) {
        SimpleRateLimiterForString limiter = new SimpleRateLimiterForString(new Jedis());
        for (int i = 0; i < 20; i++) {
            System.out.println(limiter.isActionAllowed("Jessica", "reply", 60, 5));
        }
    }

    @Override
    public boolean isActionAllowed(String userId, String actionKey, int period, int maxCount) {
        String key = String.format("rate:%s:%s", actionKey, userId);
        String count = jedis.get(key);
        if (null == count || 0 == count.length()) {
            SetParams setParams = new SetParams();
            setParams.ex(period + 1);
            jedis.set(key, "1", setParams);
            return true;
        }
        Long currentCount = jedis.incr(key);
        return currentCount <= maxCount;
    }
}
