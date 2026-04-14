package org.jeecg.modules.wms.service;

import org.jeecg.modules.wms.entity.Stock;
import org.jeecg.modules.wms.entity.StockInDetail;
import org.jeecg.modules.wms.entity.StockOutDetail;
import org.jeecg.modules.wms.entity.StockOut;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.wms.vo.StockOutPage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 出库表
 * @Author: jeecg-boot
 * @Date:   2026-04-09
 * @Version: V1.0
 */
public interface IStockOutService extends IService<StockOut> {

	/**
	 * 添加一对多
	 *
	 * @param stockOut
	 * @param stockOutDetailList
	 */
	public void saveMain(StockOut stockOut,List<StockOutDetail> stockOutDetailList) ;
	
	/**
	 * 修改一对多
	 *
   * @param stockOut
   * @param stockOutDetailList
	 */
	public void updateMain(StockOut stockOut,List<StockOutDetail> stockOutDetailList);
	
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
	 * 阶段一：申请出库 - FIFO匹配并锁定库存
	 */
	public List<StockOutDetail> matchAndLockStock(StockOut stockOut, List<StockOutDetail> detailList);

	/**
	 * 取消/驳回 - 释放锁定
	 */
	public void releaseStockLock(String stockOutId);

	/**
	 * 申请出库 - 更新物料需求锁定
	 */
	void applyStockOut(StockOut stockOut, List<StockOutDetail> details);

	/**
	 * 审核通过 - 确认出库并处理余料
	 */
	void approveStockOut(StockOut stockOut, List<StockOutDetail> details);

	/**
	 * 审核驳回/取消 - 释放锁定
	 */
	void rejectStockOut(StockOut stockOut);

	/**
	 * 删除出库单 - 释放锁定
	 */
	void deleteStockOut(String stockOutId);

	
}
