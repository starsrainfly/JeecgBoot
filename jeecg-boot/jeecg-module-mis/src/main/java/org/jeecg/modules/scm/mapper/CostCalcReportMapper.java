package org.jeecg.modules.scm.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.scm.vo.CostCalcReportVo;

import java.util.List;
import java.util.Map;

public interface CostCalcReportMapper {
    List<CostCalcReportVo> queryPageList(@Param("page") Page<CostCalcReportVo> page, @Param("params") Map<String, String> params);
    List<CostCalcReportVo> queryList(@Param("params") Map<String, String> params);
}
