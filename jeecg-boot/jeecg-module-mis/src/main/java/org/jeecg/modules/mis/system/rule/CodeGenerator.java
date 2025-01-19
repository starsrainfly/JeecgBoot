package org.jeecg.modules.mis.system.rule;

import cn.hutool.core.date.DateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

import static org.jeecg.modules.mis.utils.DateTimeUtil.convertSecondsToTimeString;
import static org.jeecg.modules.mis.utils.DateTimeUtil.convertTimeToSeconds;

///编码生成类
@Slf4j
@Component
public class CodeGenerator {
    @Autowired
    private RedisUtil redisUtil;

    public String GeneratorOrderNo(String sourceType){

        //获取当前时间
        DateTime currentTime  = new DateTime();
        //格式化当前时间为【年的后2位+月+日】
        String originDateStr = new SimpleDateFormat("yyyyMMdd").format(currentTime );
        //计算当前时间走过的秒
//        DateTime startTime =  new SimpleDateFormat("yyyyMMdd").parse(new SimpleDateFormat("yyyyMMdd").format(originDate));
//        long differSecond = (currentTime.getTime() - startTime.getTime()) / 1000;
        String totalSeconds = convertTimeToSeconds(currentTime);
        //获取【年的后2位+月+日+秒】，秒的长度不足补充0
        String yyMMddSecond = originDateStr + totalSeconds;

        //获取【业务编码】 + 【年的后2位+月+日+秒】，作为自增key；
        String prefixOrder = sourceType + "" + yyMMddSecond;
        //通过key，采用redis自增函数，实现单秒自增；不同的key，从0开始自增，同时设置60秒过期
        // Long incrId = redisUtils.saveINCR(prefixComplaint, 60);
        // Long incrId = // redisTemplate.opsForValue().increment(prefixOrder,1);
        int total = Integer.parseInt(totalSeconds);
        String convertTime = convertSecondsToTimeString(total);
        log.info("currentTime: " + currentTime.toString("yyyy-MM-dd HH:mm:ss")+ " totalTime:" + totalSeconds + " convertToTime: " + convertTime);
        redisUtil.expire(prefixOrder,60);
        Long incrId = redisUtil.incr(prefixOrder, 1);
        //生成订单编号
        String orderNo = prefixOrder + StringUtils.leftPad(String.valueOf(incrId), 4, '0');
        return orderNo;
    }

    public String GeneratorCustomerCode(String costomerType){

        return  null;
    }
}
