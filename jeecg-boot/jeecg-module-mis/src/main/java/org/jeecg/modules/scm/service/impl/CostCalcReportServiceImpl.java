package org.jeecg.modules.scm.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.scm.mapper.CostCalcReportMapper;
import org.jeecg.modules.scm.service.ICostCalcReportService;
import org.jeecg.modules.scm.vo.CostCalcDashboardVo;
import org.jeecg.modules.scm.vo.CostCalcReportVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CostCalcReportServiceImpl implements ICostCalcReportService {

    @Autowired
    private CostCalcReportMapper costCalcReportMapper;

    @Override
    public IPage<CostCalcReportVo> queryPageList(Map<String, String> params, Integer pageNo, Integer pageSize) {
        Page<CostCalcReportVo> page = new Page<>(pageNo, pageSize);
        List<CostCalcReportVo> list = costCalcReportMapper.queryPageList(page, params);
        page.setRecords(list);
        return page;
    }

    @Override
    public List<CostCalcReportVo> queryList(Map<String, String> params) {
        return costCalcReportMapper.queryList(params);
    }

    @Override
    public CostCalcDashboardVo getDashboard(Map<String, String> params) {
        CostCalcDashboardVo vo = new CostCalcDashboardVo();

        // 1. 本期核算产品数 & 总产品数
        vo.setCalcProductCount(costCalcReportMapper.selectCalcProductCount(params));
        vo.setTotalProductCount(costCalcReportMapper.selectTotalProductCount());

        // 2. 本期总成本金额（万元）—— 当前为各单位成本之和，如需精确值需关联产量
        BigDecimal totalCost = costCalcReportMapper.selectTotalCostAmount(params);
        vo.setTotalCostAmount(totalCost != null
                ? totalCost.divide(new BigDecimal("10000"), 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);

        // 3. 成本上涨 & 异常产品数
        vo.setRiseCount(costCalcReportMapper.selectRiseCount(params));
        vo.setAbnormalCount(costCalcReportMapper.selectAbnormalCount(params));

        // 4. 近6个月趋势
        vo.setTrendData(costCalcReportMapper.selectTrendData());

        // 5. TOP10
        vo.setTop10HighCost(costCalcReportMapper.selectTop10HighCost(params));
        vo.setTop10Rise(costCalcReportMapper.selectTop10Rise(params));

        return vo;
    }
}
