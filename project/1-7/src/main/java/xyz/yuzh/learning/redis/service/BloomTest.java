package xyz.yuzh.learning.redis.service;

import io.rebloom.client.Client;

/**
 * @author Harry Zhang
 * @since 2020/2/15 12:23
 */
public class BloomTest {
    public static void main(String[] args) {
        Client client = new Client("localhost", 6379);
        client.delete("codehole");
        for (int i = 0; i < 100000; i++) {
            client.add("codehole", "user" + i);
            // boolean ret = client.exists("codehole", "user" + i); 查找已存在的元素，不会误判。
            boolean ret = client.exists("codehole", "user" + (i + 1)); // 查找不存在的元素，会出现误判。
            if (ret) {
                System.out.println("到第 " + i + " 个元素时出现误判");
                break;
            }
        }
        client.close();
    }
}
