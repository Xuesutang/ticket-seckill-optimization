package com.seckill.domain;

import java.io.Serializable;
public class SeckillMessage implements Serializable {
    private String requestId; private Long userId; private Long activityId; private Long skuId; private Integer quantity;
    public SeckillMessage() { }
    public SeckillMessage(String requestId, Long userId, Long activityId, Long skuId, Integer quantity) { this.requestId=requestId; this.userId=userId; this.activityId=activityId; this.skuId=skuId; this.quantity=quantity; }
    public String getRequestId(){return requestId;} public void setRequestId(String v){requestId=v;} public Long getUserId(){return userId;} public void setUserId(Long v){userId=v;} public Long getActivityId(){return activityId;} public void setActivityId(Long v){activityId=v;} public Long getSkuId(){return skuId;} public void setSkuId(Long v){skuId=v;} public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;}
}
