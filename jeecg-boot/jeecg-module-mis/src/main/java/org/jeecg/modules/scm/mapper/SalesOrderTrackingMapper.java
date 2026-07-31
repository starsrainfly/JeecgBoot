package org.jeecg.modules.scm.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import org.jeecg.modules.scm.vo.SalesOrderTrackingVo;

import java.util.List;
import java.util.Map;
public interface SalesOrderTrackingMapper {
    List<SalesOrderTrackingVo> queryPageList(Page<SalesOrderTrackingVo> page, @Param("params") Map<String, String> params);

    /** 不分页查询（导出用）— 复用同一段 SQL */
    List<SalesOrderTrackingVo> queryList(@Param("params") Map<String, String> params);
}
