package com.seckill.infrastructure;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SeckillOrderMapper {
    @Select("select stock from ticket_sku where id=#{skuId}")
    Integer stock(@Param("skuId") Long skuId);
    @Select("select count(1) from mq_consume_record where request_id=#{requestId} and topic='seckill-order-create' and status=1")
    int consumed(@Param("requestId") String requestId);
    @Insert("insert into mq_consume_record(request_id,topic,status,consume_time) values(#{requestId},'seckill-order-create',1,now())")
    int recordConsumed(@Param("requestId") String requestId);
    @Update("update ticket_sku set stock=stock-#{quantity} where id=#{skuId} and stock>=#{quantity}")
    int decreaseDbStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);
    @Insert("insert into seckill_order(order_no,user_id,activity_id,ticket_sku_id,quantity,amount,status,request_id,create_time,update_time) values(#{orderNo},#{userId},#{activityId},#{skuId},#{quantity},0,'UNPAID',#{requestId},now(),now())")
    int createOrder(@Param("orderNo") String orderNo, @Param("userId") Long userId, @Param("activityId") Long activityId, @Param("skuId") Long skuId, @Param("quantity") Integer quantity, @Param("requestId") String requestId);
    @Select("select status from seckill_order where request_id=#{requestId}")
    String orderStatus(@Param("requestId") String requestId);
}
