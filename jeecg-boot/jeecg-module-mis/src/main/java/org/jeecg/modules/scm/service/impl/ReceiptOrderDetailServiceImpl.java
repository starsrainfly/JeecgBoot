package org.jeecg.modules.scm.service.impl;

import org.jeecg.modules.scm.entity.ReceiptOrderDetail;
import org.jeecg.modules.scm.mapper.ReceiptOrderDetailMapper;
import org.jeecg.modules.scm.service.IReceiptOrderDetailService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 收款明细
 * @Author: jeecg-boot
 * @Date:   2026-04-23
 * @Version: V1.0
 */
@Service
public class ReceiptOrderDetailServiceImpl extends ServiceImpl<ReceiptOrderDetailMapper, ReceiptOrderDetail> implements IReceiptOrderDetailService {
	
	@Autowired
	private ReceiptOrderDetailMapper receiptOrderDetailMapper;
	
	@Override
	public List<ReceiptOrderDetail> selectByMainId(String mainId) {
		return receiptOrderDetailMapper.selectByMainId(mainId);
	}
}
