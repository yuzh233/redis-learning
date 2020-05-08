package xyz.yuzh.learning.redis.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 漏斗限流算法
 *
 * @author Harry Zhang
 * @since 2020/2/16 20:50
 */
public class FunnelRateLimiter {
    private Map<String, Funnel> funnelMap = new HashMap<>();

    public static void main(String[] args) {
        FunnelRateLimiter limiter = new FunnelRateLimiter();
        for (int i = 0; i < 20; i++) {
            // 流水速率 quota/s 设置为 0.1，一分钟最多 6 个。
            System.out.println(limiter.isActionAllowed("Harry", "reply", 6, 0.1f));
        }
    }

    public boolean isActionAllowed(String userId, String actionKey, int capacity, float leakingRate) {
        String key = String.format("%s:%s", actionKey, userId);
        Funnel funnel = funnelMap.get(key);
        if (null == funnel) {
            funnel = new Funnel(capacity, leakingRate);
            funnelMap.put(key, funnel);
        }
        return funnel.watering(1);
    }


    /**
     * 漏斗容器
     */
    static class Funnel {
        // 漏斗容量
        int capacity;
        // 漏嘴流水速率 quota/s
        float leakingRate;
        // 漏斗剩余容量
        int leftQuota;
        // 上一次流水时间
        long leakingTs;

        public Funnel(int capacity, float leakingRate) {
            this.capacity = capacity;
            this.leftQuota = capacity;
            this.leakingRate = leakingRate;
            this.leakingTs = System.currentTimeMillis();
        }

        /**
         * 漏斗漏水，释放空间
         */
        void makeWater() {
            long nowTs = System.currentTimeMillis();
            // 距离上一次漏水的时间差（上一次漏了多长时间）
            long deltaTs = nowTs - leakingTs;
            // 到这次漏水时，上一次漏水一共流了多少水（腾出了多少空间）
            int deltaQuota = (int) (deltaTs * leakingRate);
            // 间隔时间太长，整数数字过大溢出
            if (deltaQuota < 0) {
                this.leftQuota = capacity;
                this.leakingTs = nowTs;
                return;
            }
            // 腾出的空间太小，最小单位为 1
            if (deltaQuota < 1) {
                return;
            }
            this.leftQuota += deltaQuota;
            this.leakingTs = nowTs;
            if (this.leftQuota > this.capacity) {
                this.leftQuota = this.capacity;
            }
        }

        /**
         * 往漏斗灌水
         *
         * @param quota 灌入容量
         * @return 是否灌入成功
         */
        boolean watering(int quota) {
            makeWater();
            if (this.leftQuota >= quota) {
                this.leftQuota -= quota;
                return true;
            }
            return false;
        }
    }
}
