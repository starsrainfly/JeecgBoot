package org.jeecg.modules.scm.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.scm.entity.ReceiptOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 收款单
 * @Author: jeecg-boot
 * @Date:   2026-04-23
 * @Version: V1.0
 */
public interface ReceiptOrderMapper extends BaseMapper<ReceiptOrder> {

    BigDecimal selectMonthReceiptAmount(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
