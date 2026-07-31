package org.jeecg.modules.scm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.scm.vo.CustomerStatementVo;

import java.util.List;
import java.util.Map;

public interface ICustomerStatementService  {
    IPage<CustomerStatementVo> queryPageList(Map<String, String> params, Integer pageNo, Integer pageSize);
    List<CustomerStatementVo> queryList(Map<String, String> params);
}
