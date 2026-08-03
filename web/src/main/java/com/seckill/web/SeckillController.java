package com.seckill.web;

import com.seckill.application.SeckillService;
import com.seckill.common.ApiResult;
import com.seckill.domain.SeckillRequest;
import com.seckill.domain.SeckillResult;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@RestController @RequestMapping("/api/seckill")
public class SeckillController {
 private final SeckillService service; public SeckillController(SeckillService service){this.service=service;}
 @PostMapping("/{activityId}/token") public ApiResult<Map<String,String>> token(@AuthenticationPrincipal Long userId, @PathVariable Long activityId){ return ApiResult.ok(Collections.singletonMap("submitToken",service.createSubmitToken(userId))); }
 @PostMapping("/{activityId}") public ApiResult<SeckillResult> submit(@AuthenticationPrincipal Long userId,@PathVariable Long activityId,@Valid @RequestBody SeckillRequest request){return ApiResult.accepted(service.submit(userId,activityId,request));}
 @GetMapping("/result/{requestId}") public ApiResult<Map<String,String>> result(@PathVariable String requestId){return ApiResult.ok(Collections.singletonMap("status",service.result(requestId)));}
}
