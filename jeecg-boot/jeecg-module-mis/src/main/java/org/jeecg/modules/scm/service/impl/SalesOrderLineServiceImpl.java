package org.jeecg.modules.scm.service.impl;

import org.jeecg.modules.scm.entity.SalesOrderLine;
import org.jeecg.modules.scm.mapper.SalesOrderLineMapper;
import org.jeecg.modules.scm.service.ISalesOrderLineService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 销售订单明细表
 * @Author: jeecg-boot
 * @Date:   2026-02-07
 * @Version: V1.0
 */
@Service
public class SalesOrderLineServiceImpl extends ServiceImpl<SalesOrderLineMapper, SalesOrderLine> implements ISalesOrderLineService {
	
	@Autowired
	private SalesOrderLineMapper salesOrderLineMapper;
	
	@Override
	public List<SalesOrderLine> selectByMainId(String mainId) {
		return salesOrderLineMapper.selectByMainId(mainId);
	}
}
