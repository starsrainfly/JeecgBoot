package org.jeecg.modules.base.service;

import org.jeecg.modules.base.entity.Currency;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.Collection;

/**
 * @Description: 币种表
 * @Author: jeecg-boot
 * @Date:   2024-11-16
 * @Version: V1.0
 */
public interface ICurrencyService extends IService<Currency> {

	/**
	 * 删除一对多
	 *
	 * @param id
	 */
	public void delMain(String id);
	
	/**
	 * 批量删除一对多
	 *
	 * @param idList
	 */
	public void delBatchMain(Collection<? extends Serializable> idList);


}
