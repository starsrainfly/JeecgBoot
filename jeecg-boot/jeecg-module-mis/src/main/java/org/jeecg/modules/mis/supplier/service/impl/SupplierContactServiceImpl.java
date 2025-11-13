package org.jeecg.modules.mis.supplier.service.impl;

import org.jeecg.modules.mis.supplier.entity.SupplierContact;
import org.jeecg.modules.mis.supplier.mapper.SupplierContactMapper;
import org.jeecg.modules.mis.supplier.service.ISupplierContactService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 供应商联系人
 * @Author: jeecg-boot
 * @Date:   2025-05-26
 * @Version: V1.0
 */
@Service
public class SupplierContactServiceImpl extends ServiceImpl<SupplierContactMapper, SupplierContact> implements ISupplierContactService {
	
	@Autowired
	private SupplierContactMapper supplierContactMapper;
	
	@Override
	public List<SupplierContact> selectByMainId(String mainId) {
		return supplierContactMapper.selectByMainId(mainId);
	}
}
