package org.jeecg.modules.scm.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.scm.entity.Supplier;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 供应商表
 * @Author: jeecg-boot
 * @Date:   2025-05-26
 * @Version: V1.0
 */
public interface SupplierMapper extends BaseMapper<Supplier> {
    public Integer selectMaxSeq(@Param("codePrefix") String codePrefix);
}
