package org.jeecg.modules.mes.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.mes.vo.MaterialReqActualReportVo;

import java.util.List;
import java.util.Map;

public interface IMaterialReqActualReportService {
    IPage<MaterialReqActualReportVo> queryPageList(Map<String, String> params, Integer pageNo, Integer pageSize);
    List<Map<String, Object>> getBatchDetail(String orderId, String materialId);
}
