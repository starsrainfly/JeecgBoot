package org.jeecg.modules.mis.supplier.service.impl;

import org.jeecg.modules.mis.supplier.entity.Supplier;
import org.jeecg.modules.mis.supplier.entity.SupplierQualification;
import org.jeecg.modules.mis.supplier.entity.SupplierContact;
import org.jeecg.modules.mis.supplier.entity.SupplierPurchaser;
import org.jeecg.modules.mis.supplier.mapper.SupplierQualificationMapper;
import org.jeecg.modules.mis.supplier.mapper.SupplierContactMapper;
import org.jeecg.modules.mis.supplier.mapper.SupplierPurchaserMapper;
import org.jeecg.modules.mis.supplier.mapper.SupplierMapper;
import org.jeecg.modules.mis.supplier.service.ISupplierService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 供应商表
 * @Author: jeecg-boot
 * @Date:   2025-05-26
 * @Version: V1.0
 */
@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements ISupplierService {

	@Autowired
	private SupplierMapper supplierMapper;
	@Autowired
	private SupplierQualificationMapper supplierQualificationMapper;
	@Autowired
	private SupplierContactMapper supplierContactMapper;
	@Autowired
	private SupplierPurchaserMapper supplierPurchaserMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(Supplier supplier, List<SupplierQualification> supplierQualificationList,List<SupplierContact> supplierContactList,List<SupplierPurchaser> supplierPurchaserList) {
		supplierMapper.insert(supplier);
		if(supplierQualificationList!=null && supplierQualificationList.size()>0) {
			for(SupplierQualification entity:supplierQualificationList) {
				//外键设置
				entity.setSupplierId(supplier.getId());
				supplierQualificationMapper.insert(entity);
			}
		}
		if(supplierContactList!=null && supplierContactList.size()>0) {
			for(SupplierContact entity:supplierContactList) {
				//外键设置
				entity.setSupplierId(supplier.getId());
				supplierContactMapper.insert(entity);
			}
		}
		if(supplierPurchaserList!=null && supplierPurchaserList.size()>0) {
			for(SupplierPurchaser entity:supplierPurchaserList) {
				//外键设置
				entity.setSupplierId(supplier.getId());
				supplierPurchaserMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(Supplier supplier,List<SupplierQualification> supplierQualificationList,List<SupplierContact> supplierContactList,List<SupplierPurchaser> supplierPurchaserList) {
		supplierMapper.updateById(supplier);
		
		//1.先删除子表数据
		supplierQualificationMapper.deleteByMainId(supplier.getId());
		supplierContactMapper.deleteByMainId(supplier.getId());
		supplierPurchaserMapper.deleteByMainId(supplier.getId());
		
		//2.子表数据重新插入
		if(supplierQualificationList!=null && supplierQualificationList.size()>0) {
			for(SupplierQualification entity:supplierQualificationList) {
				//外键设置
				entity.setSupplierId(supplier.getId());
				supplierQualificationMapper.insert(entity);
			}
		}
		if(supplierContactList!=null && supplierContactList.size()>0) {
			for(SupplierContact entity:supplierContactList) {
				//外键设置
				entity.setSupplierId(supplier.getId());
				supplierContactMapper.insert(entity);
			}
		}
		if(supplierPurchaserList!=null && supplierPurchaserList.size()>0) {
			for(SupplierPurchaser entity:supplierPurchaserList) {
				//外键设置
				entity.setSupplierId(supplier.getId());
				supplierPurchaserMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		supplierQualificationMapper.deleteByMainId(id);
		supplierContactMapper.deleteByMainId(id);
		supplierPurchaserMapper.deleteByMainId(id);
		supplierMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			supplierQualificationMapper.deleteByMainId(id.toString());
			supplierContactMapper.deleteByMainId(id.toString());
			supplierPurchaserMapper.deleteByMainId(id.toString());
			supplierMapper.deleteById(id);
		}
	}
	
}
