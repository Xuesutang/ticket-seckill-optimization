package com.seckill.infrastructure;
import org.apache.ibatis.annotations.*;
@Mapper public interface UserMapper {
 @Select("select id from sys_user where username=#{username} and password=#{password} limit 1") Long login(@Param("username") String username,@Param("password") String password);
}
