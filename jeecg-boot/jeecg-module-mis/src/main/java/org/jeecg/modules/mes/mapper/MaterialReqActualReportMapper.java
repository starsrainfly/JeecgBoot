package org.jeecg.modules.mes.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mes.vo.MaterialReqActualReportVo;

import java.util.List;
import java.util.Map;

public interface MaterialReqActualReportMapper {
    List<MaterialReqActualReportVo> queryPageList(@Param("page") Page<MaterialReqActualReportVo> page, @Param("params") Map<String, String> params);
    List<Map<String, Object>> getBatchDetail(@Param("orderId") String orderId, @Param("materialId") String materialId);
}
