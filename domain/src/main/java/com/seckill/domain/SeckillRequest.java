package com.seckill.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SeckillRequest {
    @NotNull private Long skuId;
    @NotBlank private String submitToken;
    @Min(1) private Integer quantity = 1;
    public Long getSkuId() { return skuId; } public void setSkuId(Long skuId) { this.skuId = skuId; }
    public String getSubmitToken() { return submitToken; } public void setSubmitToken(String submitToken) { this.submitToken = submitToken; }
    public Integer getQuantity() { return quantity; } public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
