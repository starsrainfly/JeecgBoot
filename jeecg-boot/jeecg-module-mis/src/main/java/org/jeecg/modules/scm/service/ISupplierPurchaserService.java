package org.jeecg.modules.scm.service;

import org.jeecg.modules.scm.entity.SupplierPurchaser;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 供应商采购员
 * @Author: jeecg-boot
 * @Date:   2025-05-26
 * @Version: V1.0
 */
public interface ISupplierPurchaserService extends IService<SupplierPurchaser> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<SupplierPurchaser>
	 */
	public List<SupplierPurchaser> selectByMainId(String mainId);
}
