package org.jeecg.modules.scm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.scm.entity.Customer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 客户信息
 * @Author: jeecg-boot
 * @Date:   2026-04-16
 * @Version: V1.0
 */
public interface CustomerMapper extends BaseMapper<Customer> {
     public Integer selectMaxSeq(@Param("codePrefix") String codePrefix);
}
