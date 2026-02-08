package org.jeecg.modules.scm.service.impl;

import org.jeecg.modules.scm.entity.SalesOrder;
import org.jeecg.modules.scm.entity.SalesOrderLine;
import org.jeecg.modules.scm.mapper.SalesOrderLineMapper;
import org.jeecg.modules.scm.mapper.SalesOrderMapper;
import org.jeecg.modules.scm.service.ISalesOrderService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 销售订单主表
 * @Author: jeecg-boot
 * @Date:   2026-02-07
 * @Version: V1.0
 */
@Service
public class SalesOrderServiceImpl extends ServiceImpl<SalesOrderMapper, SalesOrder> implements ISalesOrderService {

	@Autowired
	private SalesOrderMapper salesOrderMapper;
	@Autowired
	private SalesOrderLineMapper salesOrderLineMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(SalesOrder salesOrder, List<SalesOrderLine> salesOrderLineList) {
		salesOrderMapper.insert(salesOrder);
		if(salesOrderLineList!=null && salesOrderLineList.size()>0) {
			for(SalesOrderLine entity:salesOrderLineList) {
				//外键设置
				entity.setOrderId(salesOrder.getId());
				salesOrderLineMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(SalesOrder salesOrder,List<SalesOrderLine> salesOrderLineList) {
		salesOrderMapper.updateById(salesOrder);
		
		//1.先删除子表数据
		salesOrderLineMapper.deleteByMainId(salesOrder.getId());
		
		//2.子表数据重新插入
		if(salesOrderLineList!=null && salesOrderLineList.size()>0) {
			for(SalesOrderLine entity:salesOrderLineList) {
				//外键设置
				entity.setOrderId(salesOrder.getId());
				salesOrderLineMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		salesOrderLineMapper.deleteByMainId(id);
		salesOrderMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			salesOrderLineMapper.deleteByMainId(id.toString());
			salesOrderMapper.deleteById(id);
		}
	}
	
}
