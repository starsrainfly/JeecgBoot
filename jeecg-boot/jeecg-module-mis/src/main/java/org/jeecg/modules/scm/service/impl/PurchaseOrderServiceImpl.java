package org.jeecg.modules.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.scm.entity.PurchaseOrder;
import org.jeecg.modules.scm.entity.PurchaseOrderDetail;
import org.jeecg.modules.scm.mapper.PurchaseOrderDetailMapper;
import org.jeecg.modules.scm.mapper.PurchaseOrderMapper;
import org.jeecg.modules.scm.service.IPurchaseOrderDetailService;
import org.jeecg.modules.scm.service.IPurchaseOrderService;
import org.jeecg.modules.scm.vo.PurchaseOrderPage;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 采购订单
 * @Author: jeecg-boot
 * @Date:   2026-07-27
 * @Version: V1.0
 */
@Service
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements IPurchaseOrderService {

	@Autowired
	private PurchaseOrderMapper purchaseOrderMapper;
	@Autowired
	private PurchaseOrderDetailMapper purchaseOrderDetailMapper;

	@Autowired
	private IPurchaseOrderDetailService detailService;

	private static final String STATUS_APPLY = "APPLY";
	private static final String STATUS_PURCHASING = "PURCHASING";
	private static final String STATUS_PARTIAL = "PARTIAL";
	private static final String STATUS_COMPLETED = "COMPLETED";
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(PurchaseOrder purchaseOrder, List<PurchaseOrderDetail> purchaseOrderDetailList) {
		if (purchaseOrderDetailList == null || purchaseOrderDetailList.isEmpty()) {
			throw new JeecgBootException("采购明细不能为空");
		}
		purchaseOrder.setStatus(STATUS_APPLY);
		purchaseOrder.setApproveStatus("0");
		calcAmounts(purchaseOrder, purchaseOrderDetailList);
		purchaseOrderMapper.insert(purchaseOrder);
		//if(purchaseOrderDetailList!=null && purchaseOrderDetailList.size()>0) {
			for(PurchaseOrderDetail entity:purchaseOrderDetailList) {
				//外键设置
				entity.setOrderId(purchaseOrder.getId());
				entity.setOrderNo(purchaseOrder.getOrderNo());
				if (entity.getReceivedQty() == null) entity.setReceivedQty(BigDecimal.ZERO);
				if (entity.getSourceType() == null) entity.setSourceType("MANUAL");
				entity.setDetailStatus(STATUS_APPLY);
				//purchaseOrderDetailMapper.insert(entity);
			}
		detailService.saveBatch(purchaseOrderDetailList);
		//}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(PurchaseOrder purchaseOrder,List<PurchaseOrderDetail> purchaseOrderDetailList) {

		if (purchaseOrderDetailList == null || purchaseOrderDetailList.isEmpty()) {
			throw new JeecgBootException("采购明细不能为空");
		}
		PurchaseOrder db = getById(purchaseOrder.getId());
		if (db == null) throw new JeecgBootException("采购单不存在");
		if (!STATUS_APPLY.equals(db.getStatus())) {
			throw new JeecgBootException("该采购单已审核，不允许修改");
		}

		// 拒绝后修改 → 重置回待审核
		purchaseOrder.setStatus(STATUS_APPLY);
		purchaseOrder.setApproveStatus("0");
		purchaseOrder.setApproveId(null);
		purchaseOrder.setApproveName(null);
		purchaseOrder.setApproveTime(null);
		purchaseOrder.setOrderNo(db.getOrderNo()); // 单号不允许改
		calcAmounts(purchaseOrder, purchaseOrderDetailList);

		purchaseOrderMapper.updateById(purchaseOrder);
		
		//1.先删除子表数据
		purchaseOrderDetailMapper.deleteByMainId(purchaseOrder.getId());
		
		//2.子表数据重新插入
		if(purchaseOrderDetailList!=null && purchaseOrderDetailList.size()>0) {
			for(PurchaseOrderDetail entity:purchaseOrderDetailList) {
				//外键设置
				entity.setOrderId(purchaseOrder.getId());
				entity.setOrderNo(db.getOrderNo());
				entity.setReceivedQty(BigDecimal.ZERO);
				if (entity.getSourceType() == null) entity.setSourceType("MANUAL");
				entity.setDetailStatus(STATUS_APPLY);
				entity.setDelFlag("0");
				purchaseOrderDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		PurchaseOrder db = getById(id);
		if (db == null) throw new JeecgBootException("采购单不存在");
		if (!STATUS_APPLY.equals(db.getStatus())) {
			throw new JeecgBootException("该采购单已审核，不允许删除");
		}
		purchaseOrderDetailMapper.deleteByMainId(id);
		purchaseOrderMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			purchaseOrderDetailMapper.deleteByMainId(id.toString());
			purchaseOrderMapper.deleteById(id);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void approve(PurchaseOrder purchaseOrder) {
		PurchaseOrder db = getById(purchaseOrder.getId());
		if (db == null) throw new JeecgBootException("采购单不存在");
		if (!"0".equals(db.getApproveStatus())) {
			throw new JeecgBootException("该采购单已审核，请勿重复操作");
		}
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		db.setApproveStatus(purchaseOrder.getApproveStatus());
		db.setApproveRemark(purchaseOrder.getApproveRemark());
		db.setApproveId(user.getId());
		db.setApproveName(user.getRealname());
		db.setApproveTime(new Date());
		if ("1".equals(purchaseOrder.getApproveStatus())) {
			db.setStatus(STATUS_PURCHASING);
			List<PurchaseOrderDetail> details = detailService.selectByMainId(db.getId());
			for (PurchaseOrderDetail d : details) {
				d.setDetailStatus(STATUS_PURCHASING);
			}
			detailService.updateBatchById(details);
		}
		// 拒绝：approveStatus=2，status保持APPLY，允许修改后重新提交
		updateById(db);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addReceivedQty(String orderDetailId, BigDecimal actualQty) {
		PurchaseOrderDetail detail = detailService.getById(orderDetailId);
		if (detail == null) return;
		BigDecimal received = detail.getReceivedQty() == null ? BigDecimal.ZERO : detail.getReceivedQty();
		received = received.add(actualQty);
		detail.setReceivedQty(received);
		// 超量到货也算完成（>=）
		detail.setDetailStatus(received.compareTo(detail.getOrderQty()) >= 0 ? STATUS_COMPLETED : STATUS_PARTIAL);
		detailService.updateById(detail);

		// 主表状态联动
		List<PurchaseOrderDetail> details = detailService.selectByMainId(detail.getOrderId());
		boolean allDone = details.stream().allMatch(d ->
				d.getReceivedQty() != null && d.getOrderQty() != null
						&& d.getReceivedQty().compareTo(d.getOrderQty()) >= 0);
		boolean anyReceived = details.stream().anyMatch(d ->
				d.getReceivedQty() != null && d.getReceivedQty().compareTo(BigDecimal.ZERO) > 0);
		PurchaseOrder po = getById(detail.getOrderId());
		if (allDone) {
			po.setStatus(STATUS_COMPLETED);
		} else if (anyReceived) {
			po.setStatus(STATUS_PARTIAL);
		}
		updateById(po);
	}

	/** 金额计算：含税金额=数量×含税单价，不含税=含税/(1+税率%)，税额=差值，主表汇总 */
	private void calcAmounts(PurchaseOrder po, List<PurchaseOrderDetail> detailList) {
		BigDecimal total = BigDecimal.ZERO, net = BigDecimal.ZERO, tax = BigDecimal.ZERO;
		for (PurchaseOrderDetail d : detailList) {
			if (d.getOrderQty() == null || d.getUnitPrice() == null) continue;
			BigDecimal rate = d.getTaxRate() == null ? new BigDecimal("13") : d.getTaxRate();
			BigDecimal detailAmount = d.getOrderQty().multiply(d.getUnitPrice()).setScale(4, RoundingMode.HALF_UP);
			BigDecimal netAmount = detailAmount.divide(
					BigDecimal.ONE.add(rate.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP)), 4, RoundingMode.HALF_UP);
			d.setDetailAmount(detailAmount);
			d.setNetAmount(netAmount);
			d.setTaxAmount(detailAmount.subtract(netAmount));
			total = total.add(detailAmount);
			net = net.add(netAmount);
			tax = tax.add(d.getTaxAmount());
		}
		po.setOrderTotal(total);
		po.setOrderNet(net);
		po.setOrderTax(tax);
	}


	/* ==================== 采购执行跟踪 ==================== */

	@Override
	public IPage<PurchaseOrder> trackingPage(Page<PurchaseOrder> page, HttpServletRequest req) {
		// 1. 先按主表条件分页查询
		PurchaseOrder query = new PurchaseOrder();
		QueryWrapper<PurchaseOrder> wrapper = QueryGenerator.initQueryWrapper(query, req.getParameterMap());
		IPage<PurchaseOrder> resultPage = this.page(page, wrapper);

		if (resultPage.getRecords() == null || resultPage.getRecords().isEmpty()) {
			return resultPage;
		}

		List<String> orderIds = resultPage.getRecords().stream()
				.map(PurchaseOrder::getId)
				.collect(Collectors.toList());

		// 2. 批量查询子表汇总（采购数量、已入库数量）
		List<Map<String, Object>> detailSummary = purchaseOrderDetailMapper.selectSummaryByOrderIds(orderIds);
		Map<String, Map<String, Object>> summaryMap = detailSummary.stream()
				.collect(Collectors.toMap(
						m -> (String) m.get("order_id"),
						m -> m,
						(existing, replacement) -> existing
				));

		// 3. 批量查询在途申请数量（按订单汇总）
		List<Map<String, Object>> appliedList = purchaseOrderDetailMapper.selectAppliedQtyByOrderIds(orderIds);
		Map<String, BigDecimal> appliedMap = new HashMap<>();
		for (Map<String, Object> row : appliedList) {
			Object qtyObj = row.get("applied_qty");
			if (qtyObj != null) {
				appliedMap.put((String) row.get("order_id"), new BigDecimal(qtyObj.toString()));
			}
		}

		// 4. 组装跟踪数据
		Date now = new Date();
		for (PurchaseOrder order : resultPage.getRecords()) {
			Map<String, Object> summary = summaryMap.get(order.getId());
			BigDecimal totalOrderQty = BigDecimal.ZERO;
			BigDecimal totalReceivedQty = BigDecimal.ZERO;

			if (summary != null) {
				Object orderQtyObj = summary.get("total_order_qty");
				Object receivedQtyObj = summary.get("total_received_qty");
				if (orderQtyObj != null) totalOrderQty = new BigDecimal(orderQtyObj.toString());
				if (receivedQtyObj != null) totalReceivedQty = new BigDecimal(receivedQtyObj.toString());
			}

			BigDecimal totalAppliedQty = appliedMap.getOrDefault(order.getId(), BigDecimal.ZERO);

			order.setTotalOrderQty(totalOrderQty);
			order.setTotalReceivedQty(totalReceivedQty);
			order.setTotalAppliedQty(totalAppliedQty);

			// 到货率 = 已入库 / 采购数量 * 100
			if (totalOrderQty.compareTo(BigDecimal.ZERO) > 0) {
				order.setArrivalRate(totalReceivedQty.multiply(new BigDecimal("100"))
						.divide(totalOrderQty, 2, RoundingMode.HALF_UP));
			} else {
				order.setArrivalRate(BigDecimal.ZERO);
			}

			// 超期判断：要求到货日期已过，且未全部到货
			if (order.getExpectedDate() != null && order.getExpectedDate().before(now)) {
				if (totalReceivedQty.compareTo(totalOrderQty) < 0) {
					order.setIsOverdue("1");
					long diff = now.getTime() - order.getExpectedDate().getTime();
					order.setOverdueDays((int) (diff / (1000 * 60 * 60 * 24)));
				} else {
					order.setIsOverdue("0");
					order.setOverdueDays(0);
				}
			} else {
				order.setIsOverdue("0");
				order.setOverdueDays(0);
			}
		}

		return resultPage;
	}

	@Override
	public PurchaseOrderPage getTrackingDetail(String id) {
		PurchaseOrder purchaseOrder = this.getById(id);
		if (purchaseOrder == null) {
			return null;
		}
		PurchaseOrderPage page = new PurchaseOrderPage();
		BeanUtils.copyProperties(purchaseOrder, page);

		// 查询明细并计算跟踪字段
		List<PurchaseOrderDetail> detailList = purchaseOrderDetailMapper.selectByMainId(id);

		// 查询在途申请数量（按明细）
		Map<String, BigDecimal> appliedMap = new HashMap<>();
		for (Map<String, Object> row : purchaseOrderDetailMapper.selectAppliedQtyByOrderId(id)) {
			Object qtyObj = row.get("appliedQty");
			if (qtyObj != null) {
				appliedMap.put((String) row.get("sourceDetailId"), new BigDecimal(qtyObj.toString()));
			}
		}

		BigDecimal totalOrderQty = BigDecimal.ZERO;
		BigDecimal totalReceivedQty = BigDecimal.ZERO;
		BigDecimal totalAppliedQty = BigDecimal.ZERO;
		Date now = new Date();

		for (PurchaseOrderDetail d : detailList) {
			BigDecimal orderQty = d.getOrderQty() == null ? BigDecimal.ZERO : d.getOrderQty();
			BigDecimal receivedQty = d.getReceivedQty() == null ? BigDecimal.ZERO : d.getReceivedQty();
			BigDecimal appliedQty = appliedMap.getOrDefault(d.getId(), BigDecimal.ZERO);

			d.setAppliedQty(appliedQty);
			d.setRemainingQty(orderQty.subtract(receivedQty).subtract(appliedQty));

			totalOrderQty = totalOrderQty.add(orderQty);
			totalReceivedQty = totalReceivedQty.add(receivedQty);
			totalAppliedQty = totalAppliedQty.add(appliedQty);
		}

		page.setPurchaseOrderDetailList(detailList);
		page.setTotalOrderQty(totalOrderQty);
		page.setTotalReceivedQty(totalReceivedQty);
		page.setTotalAppliedQty(totalAppliedQty);

		if (totalOrderQty.compareTo(BigDecimal.ZERO) > 0) {
			page.setArrivalRate(totalReceivedQty.multiply(new BigDecimal("100"))
					.divide(totalOrderQty, 2, RoundingMode.HALF_UP));
		} else {
			page.setArrivalRate(BigDecimal.ZERO);
		}

		if (purchaseOrder.getExpectedDate() != null && purchaseOrder.getExpectedDate().before(now)) {
			if (totalReceivedQty.compareTo(totalOrderQty) < 0) {
				page.setIsOverdue("1");
				long diff = now.getTime() - purchaseOrder.getExpectedDate().getTime();
				page.setOverdueDays((int) (diff / (1000 * 60 * 60 * 24)));
			} else {
				page.setIsOverdue("0");
				page.setOverdueDays(0);
			}
		} else {
			page.setIsOverdue("0");
			page.setOverdueDays(0);
		}

		return page;
	}
	
}
