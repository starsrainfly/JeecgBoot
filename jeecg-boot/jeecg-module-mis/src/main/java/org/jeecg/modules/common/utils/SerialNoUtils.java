package org.jeecg.modules.common.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.SpringContextUtils;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;


/**
 * 业务单号生成工具类（高并发安全）
 * <p>
 * 生成规则：{prefix}{yyyyMMdd}{seconds(5位)}{sequence(4位)}
 * 示例：
 *   - RK20250405123450001 → 入库单
 *   - SO20250405863990001 → 销售订单
 * <p>
 * 特点：
 *   - 秒级隔离：同一秒内流水号自增，不同秒独立计数
 *   - Redis 保证高并发唯一性
 *   - 每日自动重置（因包含 yyyyMMdd）
 *   - 支持任意前缀（由调用方传入）
 *
 * @author YourName
 */
@Slf4j
public class SerialNoUtils {

    /**
     * 生成唯一业务单号
     *
     * @param prefix 单号前缀，如 "IN"（入库）、"SO"（销售）、"WO"（工单）等
     * @return 完整单号字符串，如 "IN20250405123450001"
     * @throws IllegalArgumentException 当 prefix 为空时
     */
    public static String generate(String prefix) {
        if (StringUtils.isBlank(prefix)) {
            throw new IllegalArgumentException("单号前缀（prefix）不能为空");
        }
        // 获取当前时间
        DateTime now = DateUtil.date();
        String datePart = new SimpleDateFormat("yyyyMMdd").format(now);
        String secondsPart = convertTimeToSeconds(now); // 5位秒偏移，00000 ～ 86399

        // 构造 Redis key：前缀 + 日期 + 秒偏移
        String redisKey = prefix + datePart + secondsPart;

        // 从 Spring 容器获取 RedisUtil（兼容非 Spring 管理的类，如 IFillRuleHandler）
        RedisUtil redisUtil = SpringContextUtils.getBean(RedisUtil.class);

        // 设置 key 60 秒过期（覆盖当日剩余时间足够）
        redisUtil.expire(redisKey, 60);
        Long sequence = redisUtil.incr(redisKey, 1);

        // 流水号固定4位，不足补0（最大支持 9999 单/秒）
        String seqPart = StringUtils.leftPad(sequence.toString(), 4, '0');

        String orderNo = redisKey + seqPart;

        log.debug("生成业务单号 | 单号: {} | 时间: {} | 秒偏移: {} | 流水: {}",
                orderNo,
                now.toString("yyyy-MM-dd HH:mm:ss"),
                secondsPart,
                seqPart);

        return orderNo;
    }

    /**
     *生成唯一业务单号
     * @param prefix 单号前缀，
     * @return 整单号字符串，如 "PO202603060001"
     */
    public static String generateSerialNo(String prefix) {
        if (StringUtils.isBlank(prefix)) {
            throw new IllegalArgumentException("单号前缀（prefix）不能为空");
        }
        // 获取当前时间
        DateTime now = DateUtil.date();
        String datePart = new SimpleDateFormat("yyyyMMdd").format(now);
        String secondsPart = convertTimeToSeconds(now); // 5位秒偏移，00000 ～ 86399

        // 构造 Redis key：前缀 + 日期
        String redisKey = prefix + datePart ;//+ secondsPart

        // 从 Spring 容器获取 RedisUtil（兼容非 Spring 管理的类，如 IFillRuleHandler）
        RedisUtil redisUtil = SpringContextUtils.getBean(RedisUtil.class);

        long ttlSeconds = getSecondsUntilMidnight();
        // 至少保留 1 秒，防止边界问题
        ttlSeconds = Math.max(ttlSeconds, 1);
        // 设置 key 到午夜过期（覆盖当日剩余时间足够）
        redisUtil.expire(redisKey, ttlSeconds);
        Long sequence = redisUtil.incr(redisKey, 1);

        // 流水号固定4位，不足补0（最大支持 9999 单/秒）
        String seqPart = StringUtils.leftPad(sequence.toString(), 5, '0');

        String orderNo = redisKey + seqPart;

        log.debug("生成业务单号 | 单号: {} | 时间: {} |  流水: {}",
                orderNo,
                now.toString("yyyy-MM-dd HH:mm:ss"),
                seqPart);

        return orderNo;
    }

    /**
     * 计算从 now 到 24:00 的剩余秒数
     * @return
     */
    public static long getSecondsUntilMidnight() {
        LocalTime now = LocalTime.now();
        LocalTime midnight = LocalTime.MIDNIGHT; // 00:00:00 of next day
        return now.until(midnight, ChronoUnit.SECONDS);
    }

    /**
     * 将 DateTime 转换为当日已过秒数（5位数字，不足补0）
     * 范围：00000（00:00:00） ～ 86399（23:59:59）
     *
     * @param dt 当前时间
     * @return 5位字符串表示的秒偏移
     */
    public static String convertTimeToSeconds(DateTime dt) {
        DateTime startOfDay = DateUtil.beginOfDay(dt);
        long seconds = DateUtil.between(startOfDay, dt, cn.hutool.core.date.DateUnit.SECOND);
        // 安全边界处理
        if (seconds < 0) seconds = 0;
        if (seconds > 86399) seconds = 86399;
        return String.format("%05d", seconds);
    }

    /**
     * （可选）将秒数转换为 HH:mm:ss 格式，用于日志或调试
     *
     * @param totalSeconds 从 0 到 86399 的整数
     * @return 如 "12:34:56"
     */
    public static String convertSecondsToTimeString(int totalSeconds) {
        int safeSeconds = Math.max(0, Math.min(totalSeconds, 86399));
        int hours = safeSeconds / 3600;
        int minutes = (safeSeconds % 3600) / 60;
        int seconds = safeSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
