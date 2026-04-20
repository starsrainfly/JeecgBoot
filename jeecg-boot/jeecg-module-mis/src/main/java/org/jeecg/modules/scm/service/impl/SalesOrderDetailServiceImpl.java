package org.jeecg.modules.scm.service.impl;

import org.jeecg.modules.scm.entity.SalesOrderDetail;
import org.jeecg.modules.scm.mapper.SalesOrderDetailMapper;
import org.jeecg.modules.scm.service.ISalesOrderDetailService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 销售订单明细表
 * @Author: jeecg-boot
 * @Date:   2026-04-20
 * @Version: V1.0
 */
@Service
public class SalesOrderDetailServiceImpl extends ServiceImpl<SalesOrderDetailMapper, SalesOrderDetail> implements ISalesOrderDetailService {
	
	@Autowired
	private SalesOrderDetailMapper salesOrderDetailMapper;
	
	@Override
	public List<SalesOrderDetail> selectByMainId(String mainId) {
		return salesOrderDetailMapper.selectByMainId(mainId);
	}
}
