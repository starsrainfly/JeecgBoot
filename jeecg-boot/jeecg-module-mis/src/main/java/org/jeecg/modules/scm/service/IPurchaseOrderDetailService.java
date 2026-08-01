package org.jeecg.modules.scm.service;

import org.jeecg.modules.scm.entity.PurchaseOrderDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Map;

/**
 * @Description: 采购明细
 * @Author: jeecg-boot
 * @Date:   2026-07-27
 * @Version: V1.0
 */
public interface IPurchaseOrderDetailService extends IService<PurchaseOrderDetail> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<PurchaseOrderDetail>
	 */
	public List<PurchaseOrderDetail> selectByMainId(String mainId);

	/**
	 * 查询在途申请数量（待审核的入库申请）
	 */
	List<Map<String, Object>> selectAppliedQtyByOrderId(String orderId);

	/**
	 * 批量查询订单的子表汇总（采购数量、已入库数量）
	 */
	List<Map<String, Object>> selectSummaryByOrderIds(List<String> orderIds);

	/**
	 * 批量查询订单的在途申请数量合计
	 */
	List<Map<String, Object>> selectAppliedQtyByOrderIds(List<String> orderIds);
}
