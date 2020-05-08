package xyz.yuzh.learning.redis;

import xyz.yuzh.learning.redis.service.RedisLock;

/**
 * 使用 Redis 分布式锁，设置自动续期。
 *
 * @author Harry Zhang
 * @since 2020/2/4 20:16
 */
public class UsingRedisLockExpire {
    public static void main(String[] args) {
        new Thread(new BusinessService2()).start();
        new Thread(new BusinessService2()).start();
    }
}

// 业务类
class BusinessService2 implements Runnable {
    public static final RedisLock redisLock = new RedisLock();

    @Override
    public void run() {
        boolean lock = redisLock.lock("lock:daemon", Thread.currentThread().getName(), 30);
        System.out.println(Thread.currentThread().getName() + " 获取锁？" + lock);
        if (lock) {
            try {
                // 一个守护线程用来续期
                RollOver daemonThread = new RollOver(redisLock, "lock:daemon", 30);
                daemonThread.setDaemon(true);
                daemonThread.start();

                System.out.println("执行业务方法...");
                for (int i = 0; i < 50; i++) {
                    try {
                        System.out.println(i);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                boolean unlock = redisLock.unlock("lock:daemon", Thread.currentThread().getName());
                System.out.println(Thread.currentThread().getName() + "释放锁？" + unlock);
            }
        }
    }
}

// 续期类
class RollOver extends Thread {
    private RedisLock jedis;
    private String key;
    // 每次续期时间
    private Integer incrBy;

    RollOver(RedisLock jedis, String key, Integer incrBy) {
        this.jedis = jedis;
        this.key = key;
        this.incrBy = incrBy;
    }

    @Override
    public void run() {
        int i = incrBy;
        while (true) {
            if (i == 10) {
                Long re = jedis.expire(key, incrBy);
                i = incrBy;
                if (re != 1) {
                    break;
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i--;
        }
    }
}