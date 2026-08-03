package com.seckill.application;

import com.seckill.common.BusinessException;
import com.seckill.domain.SeckillMessage;
import com.seckill.domain.SeckillRequest;
import com.seckill.domain.SeckillResult;
import com.seckill.infrastructure.SeckillKeys;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.UUID;

@Service
public class SeckillService {
    private final StringRedisTemplate redis; private final DefaultRedisScript<Long> seckillLua; private final RocketMQTemplate rocketMQTemplate;
    public SeckillService(StringRedisTemplate redis, @Qualifier("seckillLua") DefaultRedisScript<Long> seckillLua, RocketMQTemplate rocketMQTemplate) { this.redis=redis; this.seckillLua=seckillLua; this.rocketMQTemplate=rocketMQTemplate; }
    public String createSubmitToken(Long userId) { String token=UUID.randomUUID().toString().replace("-", ""); redis.opsForValue().set(SeckillKeys.token(userId, token), "1", java.time.Duration.ofMinutes(5)); return token; }
    public SeckillResult submit(Long userId, Long activityId, SeckillRequest request) {
        if (request.getQuantity() != 1) throw new BusinessException(400, "每位用户每个票档限购一张");
        Long result=redis.execute(seckillLua, Arrays.asList(SeckillKeys.stock(activityId,request.getSkuId()), SeckillKeys.buyers(activityId,request.getSkuId()), SeckillKeys.token(userId,request.getSubmitToken())), String.valueOf(userId));
        if (result == null || result == 1) throw new BusinessException(409, "票已售罄");
        if (result == 2) throw new BusinessException(409, "请勿重复购买");
        if (result == 3) throw new BusinessException(400, "提交令牌无效或已过期");
        String requestId=UUID.randomUUID().toString().replace("-", "");
        redis.opsForValue().set(SeckillKeys.result(requestId), "QUEUED", java.time.Duration.ofHours(24));
        try { rocketMQTemplate.convertAndSend("seckill-order-create", new SeckillMessage(requestId,userId,activityId,request.getSkuId(),request.getQuantity())); }
        catch (RuntimeException e) { redis.opsForValue().increment(SeckillKeys.stock(activityId, request.getSkuId())); redis.opsForSet().remove(SeckillKeys.buyers(activityId, request.getSkuId()), String.valueOf(userId)); redis.opsForValue().set(SeckillKeys.result(requestId), "FAILED"); throw new BusinessException(503, "排队服务暂不可用"); }
        return new SeckillResult(requestId, "QUEUED");
    }
    public String result(String requestId) { String value=redis.opsForValue().get(SeckillKeys.result(requestId)); return value == null ? "NOT_FOUND" : value; }
}
