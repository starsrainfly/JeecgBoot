package org.jeecg.modules.scm.service.impl;

import org.jeecg.modules.scm.entity.SalesOrder;
import org.jeecg.modules.scm.entity.SalesOrderDetail;
import org.jeecg.modules.scm.mapper.SalesOrderDetailMapper;
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
 * @Date:   2026-04-20
 * @Version: V1.0
 */
@Service
public class SalesOrderServiceImpl extends ServiceImpl<SalesOrderMapper, SalesOrder> implements ISalesOrderService {

	@Autowired
	private SalesOrderMapper salesOrderMapper;
	@Autowired
	private SalesOrderDetailMapper salesOrderDetailMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(SalesOrder salesOrder, List<SalesOrderDetail> salesOrderDetailList) {
		salesOrderMapper.insert(salesOrder);
		if(salesOrderDetailList!=null && salesOrderDetailList.size()>0) {
			for(SalesOrderDetail entity:salesOrderDetailList) {
				//外键设置
				entity.setOrderId(salesOrder.getId());
				salesOrderDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(SalesOrder salesOrder,List<SalesOrderDetail> salesOrderDetailList) {
		salesOrderMapper.updateById(salesOrder);
		
		//1.先删除子表数据
		salesOrderDetailMapper.deleteByMainId(salesOrder.getId());
		
		//2.子表数据重新插入
		if(salesOrderDetailList!=null && salesOrderDetailList.size()>0) {
			for(SalesOrderDetail entity:salesOrderDetailList) {
				//外键设置
				entity.setOrderId(salesOrder.getId());
				salesOrderDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		salesOrderDetailMapper.deleteByMainId(id);
		salesOrderMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			salesOrderDetailMapper.deleteByMainId(id.toString());
			salesOrderMapper.deleteById(id);
		}
	}
	
}
