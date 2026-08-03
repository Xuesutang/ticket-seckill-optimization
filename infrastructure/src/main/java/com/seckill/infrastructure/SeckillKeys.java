package com.seckill.infrastructure;

public final class SeckillKeys {
    private SeckillKeys() { }
    public static String stock(Long activityId, Long skuId) { return "seckill:stock:" + activityId + ":" + skuId; }
    public static String buyers(Long activityId, Long skuId) { return "seckill:buyers:" + activityId + ":" + skuId; }
    public static String token(Long userId, String token) { return "seckill:token:" + userId + ":" + token; }
    public static String result(String requestId) { return "seckill:result:" + requestId; }
}
