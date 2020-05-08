package xyz.yuzh.learning.redis.service.rediscell;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.output.ArrayOutput;
import io.lettuce.core.protocol.CommandArgs;
import io.lettuce.core.protocol.LettuceCharsets;
import io.lettuce.core.protocol.ProtocolKeyword;

import java.util.List;

/**
 * Redis-Cell 模块测试测试类，使用 Lettuce 客户端连接工具包。
 *
 * @author Harry Zhang
 * @since 2020/2/17 21:26
 */
public class RedisCellLimiter {
    public static void main(String[] args) throws InterruptedException {
        RedisClient client = RedisClient.create("redis://localhost");
        StatefulRedisConnection<String, String> connection = client.connect();
        RedisCommands<String, String> commands = connection.sync();

        RedisCodec<String, String> codec = StringCodec.UTF8;

        int count = 0;
        while (count <= 60) {
            List<Object> list = commands.dispatch(
                    Throttle.Throttle,
                    new ArrayOutput<>(codec),
                    // 首次允许两次（初始容量+1），后续每 60s 内允许 5 次。
                    new CommandArgs<>(codec).add("reply:john").add(1).add(5).add(60));

            System.out.println("[是否允许：" + list.get(0) + ", 总容量：" + list.get(1) + ", 剩余容量：" + list.get(2) +
                    ", 下一次重试等待时间：" + list.get(3) + ", 全部空余等待时间：" + list.get(4) + "]");

            Thread.sleep(1000);
            count++;
        }

        connection.close();
        client.shutdown();
    }

    enum Throttle implements ProtocolKeyword {
        Throttle("cl.throttle");

        public final byte[] bytes;

        Throttle(String name) {
            bytes = name.getBytes(LettuceCharsets.ASCII);
        }

        @Override
        public byte[] getBytes() {
            return bytes;
        }
    }


}
