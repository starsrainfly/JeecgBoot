package org.jeecg.modules.mis.currency.service;

import org.jeecg.modules.mis.currency.entity.ExchangeRate;
import org.jeecg.modules.mis.currency.entity.Currency;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

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
