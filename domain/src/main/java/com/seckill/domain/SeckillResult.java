package com.seckill.domain;

public class SeckillResult { private final String requestId; private final String status; public SeckillResult(String requestId, String status){this.requestId=requestId;this.status=status;} public String getRequestId(){return requestId;} public String getStatus(){return status;} }
