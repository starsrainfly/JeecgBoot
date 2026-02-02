package org.jeecg.modules.scm.service;

import org.jeecg.modules.scm.entity.SupplierQualification;
import org.jeecg.modules.scm.entity.SupplierContact;
import org.jeecg.modules.scm.entity.SupplierPurchaser;
import org.jeecg.modules.scm.entity.Supplier;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 供应商表
 * @Author: jeecg-boot
 * @Date:   2025-05-26
 * @Version: V1.0
 */
public interface ISupplierService extends IService<Supplier> {

	/**
	 * 添加一对多
	 *
	 * @param supplier
	 * @param supplierQualificationList
	 * @param supplierContactList
	 * @param supplierPurchaserList
	 */
	public void saveMain(Supplier supplier,List<SupplierQualification> supplierQualificationList,List<SupplierContact> supplierContactList,List<SupplierPurchaser> supplierPurchaserList) ;
	
	/**
	 * 修改一对多
	 *
   * @param supplier
   * @param supplierQualificationList
   * @param supplierContactList
   * @param supplierPurchaserList
	 */
	public void updateMain(Supplier supplier,List<SupplierQualification> supplierQualificationList,List<SupplierContact> supplierContactList,List<SupplierPurchaser> supplierPurchaserList);
	
	/**
	 * 删除一对多
	 *
	 * @param id
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 *
	 * @param idList
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);
	
}
