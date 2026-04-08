package org.jeecg.modules.wms.service;

import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.wms.entity.StockInDetail;
import org.jeecg.modules.wms.entity.StockIn;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.wms.vo.StockInPage;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 入库表
 * @Author: jeecg-boot
 * @Date:   2026-04-03
 * @Version: V1.0
 */
public interface IStockInService extends IService<StockIn> {

	/**
	 * 添加一对多
	 *
	 * @param stockIn
	 * @param stockInDetailList
	 */
	public void saveMain(StockIn stockIn,List<StockInDetail> stockInDetailList) ;
	
	/**
	 * 修改一对多
	 *
   * @param stockIn
   * @param stockInDetailList
	 */
	public void updateMain(StockIn stockIn,List<StockInDetail> stockInDetailList);
	
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

	/**
	 * 入库审核
	 * @param stockInPage
	 * @param loginUser
	 */
	public void approveStockIn(StockInPage stockInPage, LoginUser loginUser);
}
