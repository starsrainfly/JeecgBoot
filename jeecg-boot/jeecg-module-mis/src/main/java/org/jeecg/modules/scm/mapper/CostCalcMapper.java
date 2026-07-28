package org.jeecg.modules.scm.mapper;

import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.scm.entity.CostCalc;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.scm.vo.CostCalcProductVo;

/**
 * @Description: 成本核算快照
 * @Author: jeecg-boot
 * @Date:   2026-07-28
 * @Version: V1.0
 */
public interface CostCalcMapper extends BaseMapper<CostCalc> {

    IPage<CostCalcProductVo> queryProductList(Page<CostCalcProductVo> page,
                                              @Param("productCode") String productCode,
                                              @Param("productName") String productName);

    @Select("SELECT SUM(cost_total) / NULLIF(SUM(quantity), 0) " +
            "FROM mis_stock WHERE goods_id = #{materialId} AND del_flag = '0' AND quantity > 0")
    BigDecimal selectStockAvgPrice(@Param("materialId") String materialId);

    @Select("SELECT d.unit_price FROM mis_stock_in_detail d " +
            "INNER JOIN mis_stock_in h ON h.id = d.stock_in_id AND h.del_flag = '0' AND h.approve_status = '1' " +
            "WHERE d.goods_id = #{materialId} AND d.del_flag = '0' AND d.unit_price IS NOT NULL " +
            "ORDER BY d.create_time DESC LIMIT 1")
    BigDecimal selectLatestInPrice(@Param("materialId") String materialId);

}
