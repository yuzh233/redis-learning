package xyz.yuzh.learning.redis.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.Set;

/**
 * @author Harry Zhang
 * @since 2020/2/6 22:37
 */
public class RedisDelayingQueue<T> {

    private Type taskType = new TypeReference<TaskItem<T>>() {}.getType();

    private Jedis jedis;
    private String queueKey;

    public RedisDelayingQueue(Jedis jedis, String queueKey) {
        this.jedis = jedis;
        this.queueKey = queueKey;
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        RedisDelayingQueue<String> queue = new RedisDelayingQueue<>(jedis, "q-demo");

        Thread producer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                queue.delay("codehole " + i);
            }
        });
        Thread consumer = new Thread(queue::loop);

        producer.start();
        consumer.start();
        try {
            producer.join();
            System.out.println("等 producer 先执行完，再执行 consumer...");
        } catch (InterruptedException ignored) {
        }
    }

    // producer
    public void delay(T msg) {
        System.out.println("> producer");
        TaskItem<T> task = new TaskItem<>();
        task.msg = msg;
        String messageBody = JSON.toJSONString(task);
        // 放入延迟队列，5s后开始被消费
        jedis.zadd(queueKey, System.currentTimeMillis() + 5000, messageBody);
    }

    // consumer
    public void loop() {
        System.out.println("> consumer");
        while (!Thread.interrupted()) {
            // 以当前时间为截止时间作为 score 去查 zset。
            Set<String> values = jedis.zrangeByScore(queueKey, 0, System.currentTimeMillis(), 0, 1);
            if (values.isEmpty()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
                continue;
            }
            String messageBody = values.iterator().next();
            // zrem 方法是多线程任务只执行一次的关键。删除是原子操作，如果删除失败，说明其他线程已经消费了此消息。
            if (jedis.zrem(queueKey, messageBody) > 0) {
                TaskItem<T> task = JSON.parseObject(messageBody, taskType);
                this.handleMsg(task.msg);
            }
        }
    }

    private void handleMsg(T task) {
        System.out.println("处理消息，task：" + task);
    }

    static class TaskItem<T> {
        public String id;
        public T msg;
    }

}
