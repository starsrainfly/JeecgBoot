package org.jeecg.modules.mes.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mes.mapper.MaterialReqActualReportMapper;
import org.jeecg.modules.mes.service.IMaterialReqActualReportService;
import org.jeecg.modules.mes.vo.MaterialReqActualReportVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MaterialReqActualReportServiceImpl implements IMaterialReqActualReportService {

    @Autowired
    private MaterialReqActualReportMapper materialReqActualReportMapper;

    @Override
    public IPage<MaterialReqActualReportVo> queryPageList(Map<String, String> params, Integer pageNo, Integer pageSize) {
        Page<MaterialReqActualReportVo> page = new Page<>(pageNo, pageSize);
        List<MaterialReqActualReportVo> list = materialReqActualReportMapper.queryPageList(page, params);
        page.setRecords(list);
        return page;
    }

    @Override
    public List<Map<String, Object>> getBatchDetail(String orderId, String materialId) {
        return materialReqActualReportMapper.getBatchDetail(orderId, materialId);
    }
}
