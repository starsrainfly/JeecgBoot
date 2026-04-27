package org.jeecg.modules.common.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.mdm.service.IRegionMappingService;
import org.jeecg.modules.scm.mapper.CustomerMapper;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class SerialNoServiceImpl implements ISerialNoService {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    CustomerMapper customerMapper;
    @Autowired
    private IRegionMappingService regionService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public String generateSerialNo(String prefix) {
        if (StringUtils.isBlank(prefix)) {
            throw new IllegalArgumentException("单号前缀不能为空");
        }

        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DATE_FORMATTER);
        String redisKey = prefix + datePart;

        try {
            // 1. 原子自增
            Long sequence = redisUtil.incr(redisKey, 1);

            // 2. 设置过期时间 (修正后的逻辑：先 incr 后 expire)
            long ttlSeconds = getSecondsUntilMidnight(now);
            if (ttlSeconds > 0) {
                redisUtil.expire(redisKey, ttlSeconds);
            } else {
                // 兜底策略
                redisUtil.expire(redisKey, 86400);
                log.warn("单号生成器计算 TTL 异常，Key: {}, 强制设置为 24h", redisKey);
            }

            // 3. 格式化流水号 ( 4 位)
            // JeecgBoot 通常引入了 Apache Commons Lang，可直接用 StringUtils
            String seqPart = StringUtils.leftPad(sequence.toString(), 4, '0');

            String orderNo = prefix + datePart + seqPart;

            // 注意：JeecgBoot 日志通常使用 log.info 或 log.debug
            log.debug("生成单号成功：{}, Key: {}, Seq: {}", orderNo, redisKey, sequence);

            return orderNo;

        } catch (Exception e) {
            log.error("生成单号失败，prefix: {}", prefix, e);
            throw new RuntimeException("单号生成服务异常", e);
        }
    }

    @Override
    public String generateSerialNo(String prefix, int sequenceLength) {
        if (StringUtils.isBlank(prefix)) {
            throw new IllegalArgumentException("单号前缀不能为空");
        }

        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DATE_FORMATTER);
        String redisKey = prefix + datePart;

        try {
            // 1. 原子自增
            Long sequence = redisUtil.incr(redisKey, 1);

            // 2. 设置过期时间 (修正后的逻辑：先 incr 后 expire)
            long ttlSeconds = getSecondsUntilMidnight(now);
            if (ttlSeconds > 0) {
                redisUtil.expire(redisKey, ttlSeconds);
            } else {
                // 兜底策略
                redisUtil.expire(redisKey, 86400);
                log.warn("单号生成器计算 TTL 异常，Key: {}, 强制设置为 24h", redisKey);
            }

            // 3. 格式化流水号 ( 5 位)
            // JeecgBoot 通常引入了 Apache Commons Lang，可直接用 StringUtils
            String seqPart = StringUtils.leftPad(sequence.toString(), sequenceLength, '0');

            String orderNo = prefix + datePart + seqPart;

            // 注意：JeecgBoot 日志通常使用 log.info 或 log.debug
            log.debug("生成单号成功：{}, Key: {}, Seq: {}", orderNo, redisKey, sequence);

            return orderNo;

        } catch (Exception e) {
            log.error("生成单号失败，prefix: {}", prefix, e);
            throw new RuntimeException("单号生成服务异常", e);
        }
    }

    @Override
    public String generateCustomerCode(String tradeType, String districtCode, String countryCode) {
        return generatePartnerCode("K", tradeType, districtCode, countryCode);
    }

    @Override
    public String generateSupplierCode(String tradeType, String districtCode, String countryCode) {
        return generatePartnerCode("S", tradeType, districtCode, countryCode);
    }

    /**
     * 统一生成往来编码
     */
    private String generatePartnerCode(String partnerType, String tradeType,
                                       String districtCode, String countryCode) {
        // 1. 获取地区简写
        String regionCode = getRegionCode(tradeType, districtCode, countryCode);

        // 2. 查对应表最大序号
        int nextSeq;
        String codePrefix = String.format("%s-%s-%s-", partnerType, tradeType, regionCode);

        Integer maxSeq = customerMapper.selectMaxSeq(codePrefix + "%");
        nextSeq = (maxSeq == null ? 1 : maxSeq + 1);
        // 3. 组装编码
        return String.format("%s%05d", codePrefix, nextSeq);
    }

    private String getRegionCode(String tradeType, String districtCode, String countryCode) {
        if ("N".equals(tradeType)) {
            // 内贸：6位区编码前2位匹配省简写
            String prefix = districtCode.substring(0, 2);
            return regionService.getShortCodeByPrefix(prefix);
        }
        // 外贸：直接用国家码
        return countryCode;
    }
    /**
     * 计算距离明天 00:00:00 的秒数
     */
    private long getSecondsUntilMidnight(LocalDateTime now) {
        LocalDateTime tomorrowMidnight = now.toLocalDate().plusDays(1).atStartOfDay();
        long seconds = now.until(tomorrowMidnight, ChronoUnit.SECONDS);
        return Math.max(seconds, 1);
    }
}
