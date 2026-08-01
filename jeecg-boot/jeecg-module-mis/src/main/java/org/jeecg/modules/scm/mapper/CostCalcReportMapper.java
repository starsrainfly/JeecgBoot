package org.jeecg.modules.scm.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.scm.vo.CostCalcReportVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CostCalcReportMapper {
    List<CostCalcReportVo> queryPageList(@Param("page") Page<CostCalcReportVo> page, @Param("params") Map<String, String> params);
    List<CostCalcReportVo> queryList(@Param("params") Map<String, String> params);

    // ===== 看板统计 =====
    Integer selectCalcProductCount(@Param("params") Map<String, String> params);
    Integer selectTotalProductCount();
    BigDecimal selectTotalCostAmount(@Param("params") Map<String, String> params);
    Integer selectRiseCount(@Param("params") Map<String, String> params);
    Integer selectAbnormalCount(@Param("params") Map<String, String> params);
    List<Map<String, Object>> selectTrendData();
    List<CostCalcReportVo> selectTop10HighCost(@Param("params") Map<String, String> params);
    List<CostCalcReportVo> selectTop10Rise(@Param("params") Map<String, String> params);
}
