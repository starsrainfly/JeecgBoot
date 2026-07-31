package org.jeecg.modules.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.scm.mapper.CustomerStatementMapper;
import org.jeecg.modules.scm.service.ICustomerStatementService;
import org.jeecg.modules.scm.vo.CustomerStatementVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class CustomerStatementServiceImpl implements ICustomerStatementService {

    @Autowired
    private CustomerStatementMapper customerStatementMapper;

    @Override
    public IPage<CustomerStatementVo> queryPageList(Map<String, String> params, Integer pageNo, Integer pageSize) {
        Page<CustomerStatementVo> page = new Page<>(pageNo, pageSize);
        return page.setRecords(customerStatementMapper.queryPageList(page, params));
    }

    @Override
    public List<CustomerStatementVo> queryList(Map<String, String> params) {
        return customerStatementMapper.queryList(params);
    }

}
