package org.jeecg.modules.scm.mapper;

import java.util.List;
import java.util.Map;

import org.jeecg.modules.scm.entity.PurchaseOrderDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 采购明细
 * @Author: jeecg-boot
 * @Date:   2026-07-27
 * @Version: V1.0
 */
public interface PurchaseOrderDetailMapper extends BaseMapper<PurchaseOrderDetail> {

	/**
	 * 通过主表id删除子表数据
	 *
	 * @param mainId 主表id
	 * @return boolean
	 */
	public boolean deleteByMainId(@Param("mainId") String mainId);

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId 主表id
   * @return List<PurchaseOrderDetail>
   */
	public List<PurchaseOrderDetail> selectByMainId(@Param("mainId") String mainId);

	/**
	 * 根据订单ID查询各明细的在途申请数量
	 */
	List<Map<String, Object>> selectAppliedQtyByOrderId(@Param("orderId") String orderId);

	/**
	 * 批量查询订单的子表汇总（采购数量、已入库数量）
	 */
	List<Map<String, Object>> selectSummaryByOrderIds(@Param("orderIds") List<String> orderIds);

	/**
	 * 批量查询订单的在途申请数量合计
	 */
	List<Map<String, Object>> selectAppliedQtyByOrderIds(@Param("orderIds") List<String> orderIds);
}
