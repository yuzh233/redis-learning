package xyz.yuzh.learning.redis.service;

import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Harry Zhang
 * @since 2020/4/24 00:05
 */
public class RedisSentinelClient {
    private static final JedisSentinelPool SENTINEL_POOL;

    static {
        Set<String> sentinels = new HashSet<>();
        sentinels.add("localhost:16379");
        sentinels.add("localhost:26379");
        sentinels.add("localhost:36379");
        // mymaster 是在 sentinel.conf 里面配置的主节点别名
        SENTINEL_POOL = new JedisSentinelPool("mymaster", sentinels, new JedisPoolConfig(), 5000);
    }

    /**
     * 注意：部署在 docker 时，sentinel返回的主节点 IP 是容器 IP，应用无法直接通过容器内的 IP 连接到主节点。
     * <p>
     * 解决方案：
     * 1. 应用和 redis 都属于同一宿主机同一个容器网络，可以直接通过容器IP互联。
     * 2. 应用和 redis 容器不在一个宿主机，设置 redis 容器网络模式为 host，应用和访问普通宿主机一样访问。
     * 3. 不采用 docker 部署。
     */
    public static void main(String[] args) {
        HostAndPort currentHostMaster = SENTINEL_POOL.getCurrentHostMaster();
        System.out.println("当前主节点：" + currentHostMaster.getHost() + "，当前端口：" + currentHostMaster.getPort());
        // try 的自动资源管理，finally 自动 jedis.close()
        try (Jedis jedis = SENTINEL_POOL.getResource()) {
            ScanParams scanParams = new ScanParams();
            scanParams.count(1000);
            scanParams.match("*");
            String cursor = "0";
            do {
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                cursor = scanResult.getCursor();
                List<String> result = scanResult.getResult();
                System.out.println("cursor: " + cursor);
                System.out.println("result: " + result);
            } while (!"0".equalsIgnoreCase(cursor));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
