package org.jeecg.modules.common.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.SpringContextUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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

        // 1. 时间处理
        DateTime now = DateUtil.date(); // Hutool
        String datePart = new SimpleDateFormat("yyyyMMdd").format(now);
        String redisKey = prefix + datePart;

        RedisUtil redisUtil = SpringContextUtils.getBean(RedisUtil.class);

        // 2. 【核心操作】先自增
        Long sequence = redisUtil.incr(redisKey, 1);

        // 3. 设置过期时间
        long ttlSeconds = getSecondsUntilMidnight();

        // 只要 ttl > 0 就设置，防止极端情况下设为 0 或负数导致立即过期
        if (ttlSeconds > 0) {
            redisUtil.expire(redisKey, ttlSeconds);
        } else {
            // 兜底：万一计算出错，至少给 24 小时，保证今天的数据不丢
            redisUtil.expire(redisKey, 86400);
        }

        // 4. 格式化
        // 确认：5位最大 99999。
        String seqPart = StringUtils.leftPad(sequence.toString(), 5, '0');
        String orderNo = prefix + datePart + seqPart;

        log.debug("生成业务单号成功 | 单号: {} | Key: {} | 流水: {}", orderNo, redisKey, sequence);
        return orderNo;
    }

    /**
     * 计算从 now 到 24:00 的剩余秒数
     * @return
     */
    public static long getSecondsUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        // 获取明天的 00:00:00
        LocalDateTime tomorrowMidnight = now.toLocalDate().plusDays(1).atStartOfDay();

        long seconds = now.until(tomorrowMidnight, ChronoUnit.SECONDS);

        // 防御性编程：确保至少大于 0
        return Math.max(seconds, 1);
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
