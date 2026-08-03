package com.seckill.application;

import com.seckill.domain.SeckillMessage;
import com.seckill.infrastructure.SeckillKeys;
import com.seckill.infrastructure.SeckillOrderMapper;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RocketMQMessageListener(topic = "seckill-order-create", consumerGroup = "seckill-order-consumer")
public class OrderConsumer implements RocketMQListener<SeckillMessage> {
    private final SeckillOrderMapper mapper; private final StringRedisTemplate redis;
    public OrderConsumer(SeckillOrderMapper mapper, StringRedisTemplate redis) { this.mapper=mapper; this.redis=redis; }
    @Override @Transactional
    public void onMessage(SeckillMessage message) {
        if (mapper.consumed(message.getRequestId()) > 0) return;
        if (mapper.decreaseDbStock(message.getSkuId(), message.getQuantity()) == 0) { compensate(message); return; }
        mapper.createOrder("SO" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6), message.getUserId(), message.getActivityId(), message.getSkuId(), message.getQuantity(), message.getRequestId());
        mapper.recordConsumed(message.getRequestId());
        redis.opsForValue().set(SeckillKeys.result(message.getRequestId()), "UNPAID");
    }
    private void compensate(SeckillMessage message) { redis.opsForValue().increment(SeckillKeys.stock(message.getActivityId(), message.getSkuId())); redis.opsForSet().remove(SeckillKeys.buyers(message.getActivityId(), message.getSkuId()), String.valueOf(message.getUserId())); redis.opsForValue().set(SeckillKeys.result(message.getRequestId()), "FAILED"); }
}
