package org.jeecg.modules.scm.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.scm.vo.CustomerStatementVo;

import java.util.List;
import java.util.Map;

public interface CustomerStatementMapper {
    List<CustomerStatementVo> queryPageList(Page<CustomerStatementVo> page, @Param("params") Map<String, String> params);
    List<CustomerStatementVo> queryList(@Param("params") Map<String, String> params);
}
