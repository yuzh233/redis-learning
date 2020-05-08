package service;

/**
 * @author Harry Zhang
 * @since 2020/2/16 11:41
 */
public interface RateLimiter {

    /**
     * 简单限流接口
     *
     * @param userId    用户ID
     * @param actionKey 行为ID
     * @param period    时间周期
     * @param maxCount  最大允许次数
     * @return 是否被允许操作
     */
    boolean isActionAllowed(String userId, String actionKey, int period, int maxCount);

}
