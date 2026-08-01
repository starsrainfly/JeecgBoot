package org.jeecg.modules.scm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.scm.vo.CostCalcReportVo;

import java.util.List;
import java.util.Map;

public interface ICostCalcReportService {
    IPage<CostCalcReportVo> queryPageList(Map<String, String> params, Integer pageNo, Integer pageSize);
    List<CostCalcReportVo> queryList(Map<String, String> params);
}
