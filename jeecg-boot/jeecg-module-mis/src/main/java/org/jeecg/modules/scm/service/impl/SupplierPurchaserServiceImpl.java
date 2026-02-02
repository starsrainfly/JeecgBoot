package org.jeecg.modules.scm.service.impl;

import org.jeecg.modules.scm.entity.SupplierPurchaser;
import org.jeecg.modules.scm.mapper.SupplierPurchaserMapper;
import org.jeecg.modules.scm.service.ISupplierPurchaserService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 供应商采购员
 * @Author: jeecg-boot
 * @Date:   2025-05-26
 * @Version: V1.0
 */
@Service
public class SupplierPurchaserServiceImpl extends ServiceImpl<SupplierPurchaserMapper, SupplierPurchaser> implements ISupplierPurchaserService {
	
	@Autowired
	private SupplierPurchaserMapper supplierPurchaserMapper;
	
	@Override
	public List<SupplierPurchaser> selectByMainId(String mainId) {
		return supplierPurchaserMapper.selectByMainId(mainId);
	}
}
