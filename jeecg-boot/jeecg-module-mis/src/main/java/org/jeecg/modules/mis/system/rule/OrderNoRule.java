package org.jeecg.modules.mis.system.rule;

import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.handler.IFillRuleHandler;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.SpringContextUtils;

import java.text.SimpleDateFormat;

import static org.jeecg.modules.mis.utils.DateTimeUtil.*;

@Slf4j
public class OrderNoRule implements IFillRuleHandler {
    //@Autowired
    //private RedisUtil redisUtil;
//JeecgBaseConfig jeecgBaseConfig = SpringContextUtils.getBean(JeecgBaseConfig.class);
    RedisUtil redisUtil = SpringContextUtils.getBean(RedisUtil.class);
    @Override
    public Object execute(JSONObject params, JSONObject formData) {
        String prefix = "S";//前缀默认为空 销售S，退R，
        //订单前缀默认为CN 如果规则参数不为空，则取自定义前缀
        if (params != null) {
            Object obj = params.get("prefix");
            if (obj != null) prefix = obj.toString();
        }
        //获取当前时间
        DateTime currentTime  = new DateTime();
        //格式化当前时间为【年的后2位+月+日】
        String originDateStr = new SimpleDateFormat("yyyyMMdd").format(currentTime );
        //计算当前时间走过的秒
        String totalSeconds = convertTimeToSeconds(currentTime);
        //获取【年的后2位+月+日+秒】，秒的长度不足补充0
        String yyyyMMddSecond = originDateStr + totalSeconds;
        //获取【业务编码】 + 【年的后2位+月+日+秒】，作为自增key；
        String prefixOrder = prefix + "" + yyyyMMddSecond;
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
}
