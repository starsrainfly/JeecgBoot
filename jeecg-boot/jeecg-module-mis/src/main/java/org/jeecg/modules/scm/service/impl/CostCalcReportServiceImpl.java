package org.jeecg.modules.scm.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.scm.mapper.CostCalcReportMapper;
import org.jeecg.modules.scm.service.ICostCalcReportService;
import org.jeecg.modules.scm.vo.CostCalcReportVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
