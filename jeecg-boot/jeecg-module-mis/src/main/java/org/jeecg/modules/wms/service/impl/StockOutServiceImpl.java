package org.jeecg.modules.wms.service.impl;

import cn.hutool.core.date.DateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.common.enums.ApproveStatusEnum;
import org.jeecg.modules.common.enums.SerialNoPrefixEnum;
import org.jeecg.modules.common.enums.StockEnum;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.mdm.service.IMisConfigService;
import org.jeecg.modules.mes.entity.ProductionMaterial;
import org.jeecg.modules.mes.service.IProductionMaterialService;
import org.jeecg.modules.wms.entity.*;
import org.jeecg.modules.wms.mapper.StockInDetailMapper;
import org.jeecg.modules.wms.mapper.StockInMapper;
import org.jeecg.modules.wms.mapper.StockOutDetailMapper;
import org.jeecg.modules.wms.mapper.StockOutMapper;
import org.jeecg.modules.wms.service.*;
import org.jeecg.modules.wms.vo.StockOutPage;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @Description: 出库表
 * @Author: jeecg-boot
 * @Date:   2026-04-09
 * @Version: V1.0
 */
@Slf4j
@Service
public class StockOutServiceImpl extends ServiceImpl<StockOutMapper, StockOut> implements IStockOutService {

	@Autowired
	private StockOutMapper stockOutMapper;
	@Autowired
	private StockOutDetailMapper stockOutDetailMapper;
	@Autowired
	private StockInMapper stockInMapper;
	@Autowired
	private StockInDetailMapper stockInDetailMapper;
	@Autowired
	private IStockService stockService;
	@Autowired
	private IStockOutDetailService stockOutDetailService;
	@Autowired
	private IStockInDetailService stockInDetailService;
	@Autowired
	private IProductionMaterialService productionMaterialService;
	@Autowired
	private IWarehouseAreaService warehouseAreaService;
	@Autowired
	private IWarehouseService warehouseService;
	@Autowired
	private IMisConfigService misConfigService;
	@Autowired
	private ISerialNoService serialNoService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(StockOut stockOut, List<StockOutDetail> stockOutDetailList) {
		if(stockOutDetailList !=null && stockOutDetailList.size()>0){
			BigDecimal totalCost = stockOutDetailList.stream()
					.map(d -> d.getCostTotal() != null ? d.getCostTotal() : BigDecimal.ZERO)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			BigDecimal totalSales = stockOutDetailList.stream()
					.map(d -> d.getSalesTotal() != null ? d.getSalesTotal() : BigDecimal.ZERO)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			stockOut.setTotalCost(totalCost);
			stockOut.setTotalSales(totalSales);
		}
		stockOutMapper.insert(stockOut);
		if(stockOutDetailList!=null && stockOutDetailList.size()>0) {
			for(StockOutDetail entity:stockOutDetailList) {
				entity.setStockOutId(stockOut.getId());
				stockOutDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(StockOut stockOut,List<StockOutDetail> stockOutDetailList) {
		if(stockOutDetailList !=null && stockOutDetailList.size()>0){
			BigDecimal totalCost = stockOutDetailList.stream()
					.map(d -> d.getCostTotal() != null ? d.getCostTotal() : BigDecimal.ZERO)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			BigDecimal totalSales = stockOutDetailList.stream()
					.map(d -> d.getSalesTotal() != null ? d.getSalesTotal() : BigDecimal.ZERO)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			stockOut.setTotalCost(totalCost);
			stockOut.setTotalSales(totalSales);
		}
		stockOutMapper.updateById(stockOut);
		stockOutDetailMapper.deleteByMainId(stockOut.getId());
		if(stockOutDetailList!=null && stockOutDetailList.size()>0) {
			for(StockOutDetail entity:stockOutDetailList) {
				entity.setStockOutNo(stockOut.getStockOutNo());
				entity.setStockOutId(stockOut.getId());
				stockOutDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		stockOutDetailMapper.deleteByMainId(id);
		stockOutMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			stockOutDetailMapper.deleteByMainId(id.toString());
			stockOutMapper.deleteById(id);
		}
	}

	/***
	 * 阶段一：申请出库 - FIFO匹配并锁定库存
	 * 拆分成独立明细（正常+超量）
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<StockOutDetail> matchAndLockStock(StockOut stockOut, List<StockOutDetail> detailList) {
		List<StockOutDetail> resultList = new ArrayList<>();

		// 判断是否需要优先余料库
		boolean priorityResidual = isProductionResidualPriority(stockOut);

		for (StockOutDetail detail : detailList) {
			BigDecimal applyQty = detail.getApplyQty();           // 150
			BigDecimal overQty = detail.getOverQty() != null ? detail.getOverQty() : BigDecimal.ZERO;  // 50
			String overFlag = detail.getOverFlag();                 // "1"

			// 拆分：正常部分 + 超量部分
			BigDecimal normalApplyQty = applyQty.subtract(overQty);  // 100
			BigDecimal overApplyQty = overQty;                      // 50

			// ===== 步骤1：处理正常部分（100）=====
			if (normalApplyQty.compareTo(BigDecimal.ZERO) > 0) {
				List<StockOutDetail> normalDetails;
				if (priorityResidual) {
					// 生产领料：先余料库，再目标仓库
					normalDetails = processProductionPart(detail, normalApplyQty, stockOut);
				} else {
					// 其他出库或用户不启用优先：只查目标仓库
					normalDetails = processSingleWarehousePart(detail, normalApplyQty, stockOut);
				}
				resultList.addAll(normalDetails);
//				List<StockOutDetail> normalDetails = processNormalPart(
//						detail, normalApplyQty, stockOut
//				);
//				resultList.addAll(normalDetails);
			}

			// ===== 步骤2：处理超量部分（50）=====
			if (overApplyQty.compareTo(BigDecimal.ZERO) > 0 && "1".equals(overFlag)) {
				List<StockOutDetail> overDetails = processOverPart(
						detail, overApplyQty, overQty, stockOut  // overQty=总超量50，用于标记
				);
				resultList.addAll(overDetails);
			}
		}

		return resultList;
	}

	/**
	 * 判断是否优先余料库
	 */
	private boolean isProductionResidualPriority(StockOut stockOut) {
		// 只有生产领料类型才考虑优先余料
		if (!"PRODUCTION".equals(stockOut.getStockOutType())) {
			return false;
		}
		// 读取配置开关
		return misConfigService.getBoolean("WMS", "PRODUCTION_RESIDUAL_PRIORITY", true);
	}

	/**
	 * 获取余料库ID列表
	 */
	private List<String> getResidualWarehouseIds() {
		return warehouseService.lambdaQuery()
				.eq(Warehouse::getWarehouseType, "RESIDUAL")
				.eq(Warehouse::getStatus, "1")
				.eq(Warehouse::getDelFlag, "0")
				.list()
				.stream()
				.map(Warehouse::getId)
				.collect(Collectors.toList());
	}

	/**
	 * 生产领料：分层FIFO —— 先余料库，再目标仓库
	 */
	private List<StockOutDetail> processProductionPart(StockOutDetail source,
														BigDecimal normalApplyQty,
														StockOut stockOut) {
		List<StockOutDetail> result = new ArrayList<>();
		String originalRemark = source.getRemark();
		BigDecimal normalRemaining = normalApplyQty;

		// ===== 第1层：余料库 =====
		List<String> residualIds = getResidualWarehouseIds();
		if (!residualIds.isEmpty()) {
			List<Stock> residualStocks = stockService.selectAvailableStockByGoodsMultiWarehouse(
					residualIds, source.getGoodsId(), source.getBatchNo()
			);

			for (Stock stock : residualStocks) {
				if (normalRemaining.compareTo(BigDecimal.ZERO) <= 0) break;

				BigDecimal available = stock.getQuantity().subtract(stock.getLockedQty());
				if (available.compareTo(BigDecimal.ZERO) <= 0) continue;

				BigDecimal allocate = available.min(normalRemaining);
				stockService.increaseLockQty(stock.getId(), allocate);

				StockInDetail inDetail = stockInDetailService.getById(stock.getInDetailId());
				StockOutDetail outDetail = createOutDetail(source, stock, inDetail, allocate, stockOut, "0");

				outDetail.setApplyQty(normalApplyQty);
				outDetail.setActualQty(allocate);
				outDetail.setOverFlag("0");
				outDetail.setOverQty(BigDecimal.ZERO);

				// 标记余料库来源
				String residualRemark = "余料库[" + stock.getWarehouseId() + "]出库：" + stock.getBatchNo() +
						"(库存" + stock.getQuantity() + ",本次出" + allocate + ")";
				outDetail.setRemark(StringUtils.isNotBlank(originalRemark)
						? originalRemark + " | " + residualRemark
						: residualRemark);

				result.add(outDetail);
				normalRemaining = normalRemaining.subtract(allocate);
			}
		}

		// ===== 第2层：正常仓库 =====
		if (normalRemaining.compareTo(BigDecimal.ZERO) > 0) {
			List<Stock> stocks = stockService.selectAvailableStockByGoods(
					stockOut.getWarehouseId(), source.getGoodsId(), source.getBatchNo()
			);

			if (stocks.isEmpty() && !result.isEmpty()) {
				// 有余料出库但正常仓库没库存，抛异常
				throw new JeecgBootException("物料[" + source.getGoodsName() + "]余料库已耗尽，正常仓库库存不足，剩余需锁定：" + normalRemaining);
			} else if (stocks.isEmpty()) {
				throw new JeecgBootException("物料[" + source.getGoodsName() + "]正常仓库库存不足");
			}

			for (Stock stock : stocks) {
				if (normalRemaining.compareTo(BigDecimal.ZERO) <= 0) break;

				BigDecimal available = stock.getQuantity().subtract(stock.getLockedQty());
				if (available.compareTo(BigDecimal.ZERO) <= 0) continue;

				BigDecimal allocate = available.min(normalRemaining);
				stockService.increaseLockQty(stock.getId(), allocate);

				StockInDetail inDetail = stockInDetailService.getById(stock.getInDetailId());
				StockOutDetail outDetail = createOutDetail(source, stock, inDetail, allocate, stockOut, "0");

				outDetail.setApplyQty(normalApplyQty);
				outDetail.setActualQty(allocate);
				outDetail.setOverFlag("0");
				outDetail.setOverQty(BigDecimal.ZERO);
				outDetail.setRemark(originalRemark);

				result.add(outDetail);
				normalRemaining = normalRemaining.subtract(allocate);
			}

			if (normalRemaining.compareTo(BigDecimal.ZERO) > 0) {
				throw new JeecgBootException("库存不足（含余料库），剩余需锁定：" + normalRemaining);
			}
		}

		return result;
	}

	/**
	 * 非生产出库：只查目标仓库
	 */
	private List<StockOutDetail> processSingleWarehousePart(StockOutDetail source,
															 BigDecimal normalApplyQty,
															 StockOut stockOut) {
		List<StockOutDetail> result = new ArrayList<>();
		String originalRemark = source.getRemark();
		BigDecimal normalRemaining = normalApplyQty;

		List<Stock> stocks = stockService.selectAvailableStockByGoods(
				stockOut.getWarehouseId(), source.getGoodsId(), source.getBatchNo()
		);

		if (stocks.isEmpty()) {
			throw new JeecgBootException("物料[" + source.getGoodsName() + "]在仓库["
					+ stockOut.getWarehouseId() + "]库存不足");
		}

		for (Stock stock : stocks) {
			if (normalRemaining.compareTo(BigDecimal.ZERO) <= 0) break;

			BigDecimal available = stock.getQuantity().subtract(stock.getLockedQty());
			if (available.compareTo(BigDecimal.ZERO) <= 0) continue;

			BigDecimal allocate = available.min(normalRemaining);
			stockService.increaseLockQty(stock.getId(), allocate);

			StockInDetail inDetail = stockInDetailService.getById(stock.getInDetailId());
			StockOutDetail outDetail = createOutDetail(source, stock, inDetail, allocate, stockOut, "0");

			outDetail.setApplyQty(normalApplyQty);
			outDetail.setActualQty(allocate);
			outDetail.setOverFlag("0");
			outDetail.setOverQty(BigDecimal.ZERO);
			outDetail.setRemark(originalRemark);

			result.add(outDetail);
			normalRemaining = normalRemaining.subtract(allocate);
		}

		if (normalRemaining.compareTo(BigDecimal.ZERO) > 0) {
			throw new JeecgBootException("仓库[" + stockOut.getWarehouseId() + "]库存不足，剩余需锁定：" + normalRemaining);
		}

		return result;
	}

	/**
	 * 处理正常部分 不使用了 用processProductionPart + processSingleWarehousePart替代
	 * 申请数量固定，实际出库根据库存分配
	 */
	/*
	private List<StockOutDetail> processNormalPart(StockOutDetail source,
												   BigDecimal normalApplyQty,
												   StockOut stockOut) {
		List<StockOutDetail> result = new ArrayList<>();
		String originalRemark = source.getRemark();
		// 1. 优先使用余料库
		BigDecimal residualAvailable = residualInventoryService.getAvailableQty(
				source.getGoodsId(), null
		);//stockOut.getWarehouseId()

		BigDecimal normalRemaining = normalApplyQty;

		if (residualAvailable.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal useResidual = normalApplyQty.min(residualAvailable);
			List<ResidualInventory> lockedResiduals = residualInventoryService.lockQtyAndReturnList(
					source.getGoodsId(), useResidual
			);

			for (ResidualInventory residual : lockedResiduals) {
				StockOutDetail residualDetail = createResidualDetail(source, residual,
						residual.getLockedQty(), stockOut);
				// 标记为正常部分
				residualDetail.setApplyQty(normalApplyQty);   // 申请量=100（固定）
				residualDetail.setActualQty(residual.getLockedQty());  // 实际=本次分配
				residualDetail.setOverFlag("0");
				residualDetail.setOverQty(BigDecimal.ZERO);
				// **设置 remark（合并原始 + 余料说明）**
				String residualRemark = "余料库出库：" + residual.getProductionOrderNo() +
						"(原数量" + residual.getQty() + ",本次出" + residual.getLockedQty() + ")";
				residualDetail.setRemark(StringUtils.isNotBlank(originalRemark)
						? originalRemark + " | " + residualRemark
						: residualRemark);
				result.add(residualDetail);
			}

			normalRemaining = normalApplyQty.subtract(useResidual);
		}

		// 2. 正常库存分配
		if (normalRemaining.compareTo(BigDecimal.ZERO) > 0) {
			List<Stock> stocks = stockService.selectAvailableStockByGoods(
					stockOut.getWarehouseId(), source.getGoodsId(), source.getBatchNo()
			);

			if (stocks.isEmpty()) {
				throw new JeecgBootException("物料[" + source.getGoodsName() + "]正常库存不足");
			}

			for (Stock stock : stocks) {
				if (normalRemaining.compareTo(BigDecimal.ZERO) <= 0) break;

				BigDecimal available = stock.getQuantity().subtract(stock.getLockedQty());
				if (available.compareTo(BigDecimal.ZERO) <= 0) continue;

				BigDecimal allocate = available.min(normalRemaining);
				stockService.increaseLockQty(stock.getId(), allocate);

				StockInDetail inDetail = stockInDetailService.getById(stock.getInDetailId());
				StockOutDetail outDetail = createOutDetail(source, stock, inDetail, allocate, stockOut, "0");

				// 关键：申请数量固定为100，实际=本次分配
				outDetail.setApplyQty(normalApplyQty);   // 固定100
				outDetail.setActualQty(allocate);        // 实际分配量
				outDetail.setOverFlag("0");
				outDetail.setOverQty(BigDecimal.ZERO);
				// 复制原始remark
				outDetail.setRemark(originalRemark);

				result.add(outDetail);
				normalRemaining = normalRemaining.subtract(allocate);
			}

			if (normalRemaining.compareTo(BigDecimal.ZERO) > 0) {
				throw new JeecgBootException("正常库存不足，剩余需锁定：" + normalRemaining);
			}
		}

		return result;
	}
*/
	/**
	 * 处理超量部分
	 * 申请数量=超量本身（50），实际出库根据库存分配
	 * totalOverQty: 总超量（用于overQty字段标记）
	 */
	private List<StockOutDetail> processOverPart(StockOutDetail source,
												 BigDecimal overApplyQty,    // 50
												 BigDecimal totalOverQty,    // 50（总超量，标记用）
												 StockOut stockOut) {
		List<StockOutDetail> result = new ArrayList<>();
		String originalRemark = source.getRemark();
		// 超量部分直接从正常库存分配（不入余料库）
		List<Stock> stocks = stockService.selectAvailableStockByGoods(
				stockOut.getWarehouseId(), source.getGoodsId(), source.getBatchNo()
		);

		if (stocks.isEmpty()) {
			throw new JeecgBootException("物料[" + source.getGoodsName() + "]无可用库存供超量分配");
		}

		BigDecimal overRemaining = overApplyQty;

		for (Stock stock : stocks) {
			if (overRemaining.compareTo(BigDecimal.ZERO) <= 0) break;

			BigDecimal available = stock.getQuantity().subtract(stock.getLockedQty());
			if (available.compareTo(BigDecimal.ZERO) <= 0) continue;

			BigDecimal allocate = available.min(overRemaining);
			stockService.increaseLockQty(stock.getId(), allocate);

			StockInDetail inDetail = stockInDetailService.getById(stock.getInDetailId());
			StockOutDetail outDetail = createOutDetail(source, stock, inDetail, allocate, stockOut, "1");

			// 关键：申请数量=超量本身（50），实际=本次分配，overQty=总超量（标记）
			outDetail.setApplyQty(overApplyQty);     // 固定50（超量本身）
			outDetail.setActualQty(allocate);         // 实际分配量
			outDetail.setOverFlag("1");
			outDetail.setOverQty(totalOverQty);      // **总超量50，仅标记**
			// **设置 remark（复制原始，可选追加超量标记）**
			outDetail.setRemark(originalRemark);

			result.add(outDetail);
			overRemaining = overRemaining.subtract(allocate);
		}

		if (overRemaining.compareTo(BigDecimal.ZERO) > 0) {
			throw new JeecgBootException("库存不足支持超量申请，剩余需锁定：" + overRemaining);
		}

		return result;
	}

	/**
	 * 取消/驳回 - 释放锁定
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void releaseStockLock(String stockOutId) {
		List<StockOutDetail> details = stockOutDetailService.selectByMainId(stockOutId);
		for (StockOutDetail detail : details) {
			if (detail.getStockId() == null || detail.getActualQty() == null) {
				continue;
			}
			stockService.releaseLockQty(detail.getStockId(), detail.getActualQty());
		}
	}

	// ==================== 申请出库 ====================
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void applyStockOut(StockOut stockOut, List<StockOutDetail> details) {
		this.saveMain(stockOut, details);
		for (StockOutDetail detail : details) {
			lockMaterial(detail, stockOut.getRequiredDate());
		}
	}

	private void lockMaterial(StockOutDetail detail, Date requiredDate) {
		String reqId = detail.getRequirementId();
		BigDecimal applyQty = detail.getApplyQty();
		BigDecimal overQty = detail.getOverQty() != null ? detail.getOverQty() : BigDecimal.ZERO;

		if (StringUtils.isBlank(reqId) || applyQty == null) {
			return;
		}

		// 拆分正常和超量
		BigDecimal normalQty = applyQty.subtract(overQty);  // 100
		BigDecimal actualOverQty = overQty;                  // 50

		String[] ids = reqId.split(",");
		if (ids.length == 1) {
			doLockMaterial(reqId, normalQty, actualOverQty, requiredDate, detail);
		} else {
			// 多批次（兼容处理）

			int count = ids.length;

			// 正常部分平分
			BigDecimal avgNormal = normalQty.divide(new BigDecimal(count), 6, RoundingMode.HALF_UP);
			// 超量部分平分
			BigDecimal avgOver = actualOverQty.divide(new BigDecimal(count), 6, RoundingMode.HALF_UP);

			for (int i = 0; i < count; i++) {
				String id = ids[i].trim();

				BigDecimal normalPart = (i == count - 1)
						? normalQty.subtract(avgNormal.multiply(new BigDecimal(i)))
						: avgNormal;

				BigDecimal overPart = (i == count - 1)
						? actualOverQty.subtract(avgOver.multiply(new BigDecimal(i)))
						: avgOver;

				doLockMaterial(id, normalPart, overPart, requiredDate, detail);
			}
		}
	}

	/**
	 * 锁定材料（分别处理正常和超量）
	 */
	private void doLockMaterial(String materialReqId, BigDecimal normalQty, BigDecimal overQty,
								Date requiredDate, StockOutDetail detail) {

		// 【新增】防御性判断
		if (StringUtils.isBlank(materialReqId)) {
			log.warn("物料需求ID为空，跳过锁定");
			return;
		}
		ProductionMaterial material = productionMaterialService.getById(materialReqId);
		if (material == null) {
			log.warn("物料需求不存在: {}", materialReqId);
			return;
		}

		if (requiredDate != null) {
			material.setRequiredDate(requiredDate);
		}

		BigDecimal required = material.getRequiredQty() == null ? BigDecimal.ZERO : material.getRequiredQty();
		BigDecimal issued = material.getIssuedQty() == null ? BigDecimal.ZERO : material.getIssuedQty();
		BigDecimal currentLocked = material.getLockedQty() == null ? BigDecimal.ZERO : material.getLockedQty();
		BigDecimal currentOver = material.getOverQty() == null ? BigDecimal.ZERO : material.getOverQty();

		// 更新锁定量（正常部分）
		BigDecimal newLocked = currentLocked.add(normalQty);
		// 更新超量（超量部分）
		BigDecimal newOver = currentOver.add(overQty);

		// 计算状态
		String status;
		if (issued.compareTo(required) >= 0) {
			status = "2";  // 已完成
		} else if (issued.add(newLocked).compareTo(BigDecimal.ZERO) > 0 || newOver.compareTo(BigDecimal.ZERO) > 0) {
			status = "1";  // 部分发料（有锁定或有超量）
		} else {
			status = "0";  // 待发料
		}

		material.setLockedQty(newLocked);
		material.setOverQty(newOver);
		material.setStatus(status);

		productionMaterialService.updateById(material);

		log.info("物料锁定: reqId={}, normalLock={}, overQty={}, newLocked={}, newOver={}, status={}",
				materialReqId, normalQty, overQty, newLocked, newOver, status);
	}

	// ==================== 审核通过 ====================
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void approveStockOut(StockOut stockOut, List<StockOutDetail> details) {

		for (StockOutDetail detail : details) {
			confirmDetail(detail, stockOut);
		}
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		stockOut.setApproveId(user.getId());
		stockOut.setApproveName(user.getRealname());
		stockOut.setApproveTime(new DateTime());
		stockOut.setApproveStatus(ApproveStatusEnum.PASS.getCode());
		stockOut.setApproveRemark(stockOut.getApproveRemark());
		stockOut.setStatus(StockEnum.StockOutStatus.FINISHED.getCode());
		stockOut.setStockOutTime(new DateTime());
		this.updateById(stockOut);
	}

	/**
	 * 确认单条明细（审核时）
	 */
	private void confirmDetail(StockOutDetail detail, StockOut stockOut) {
		if (detail.getStockId() == null || detail.getActualQty() == null) {
			log.warn("明细缺少库存ID或数量: detailId={}", detail.getId());
			return;
		}

		// 1. 扣减库存（统一走正常库存扣减，含余料库）
		stockService.confirmDeduct(detail.getStockId(), detail.getActualQty());

		String reqId = detail.getRequirementId();
		if(StringUtils.isNotBlank(reqId)){
			// 生产领料 通过需求表出库
			handleProductionRequirementOut(detail, stockOut, reqId);
		}
	}

	/**
	 * 生产领料通过需求表出库
	 * @param detail
	 * @param reqId
	 */
	private void handleProductionRequirementOut(StockOutDetail detail,StockOut stockOut, String reqId){
		BigDecimal actualQty = detail.getActualQty();
		// 区分正常和超量处理
		if ("1".equals(detail.getOverFlag())) {
			// ===== 超量明细 =====
			log.info("超量明细审核: reqId={}, actualQty={}, 减少锁定和剩余", reqId, detail.getActualQty());
			// 超量部分：释放锁定，入issued 增加已发，释放锁定，剩余保持为0
			productionMaterialService.decreaseLockAndRemainingQty(reqId, actualQty);
			updateMaterialStatus(reqId);
			// 超量入余料库（用实际出库量）
			createResidualStock(detail, stockOut, actualQty);

		} else {
			log.info("正常明细审核: reqId={}, actualQty={}, 增加已发减少锁定", reqId, detail.getActualQty());
			// 使用SQL直接计算：增加已发，减少锁定和剩余
			updateMaterialIssued(reqId, detail.getActualQty());
			// 更新状态
			//updateMaterialStatus(reqId);

			log.info("正常明细审核: reqId={}, actualQty={}, 增加已发减少锁定",
					reqId, detail.getActualQty());
		}
		// 统一更新状态
		updateMaterialStatus(reqId);
	}

	private void handleSalesOut(StockOutDetail detail, String sourceDetailId, String orderId) {
//		// 更新销售订单明细发货数量
//		salesOrderDetailService.increaseDeliveryQty(sourceDetailId, detail.getActualQty());
//		// 更新订单发货状态
//		updateSalesOrderDeliveryStatus(orderId);
	}


	/**
	 * 【改造】超量入余料库
	 * 1. 从原库存记录复制完整信息
	 * 2. 生成余料入库单+明细
	 * 3. 创建库存记录（关联入库明细）
	 * 数据源是从出库单反查
	 */
	private void createResidualStock_sourceOut(StockOutDetail detail, StockOut stockOut, BigDecimal qty) {
		// 获取原库存记录（超量部分从目标仓库扣减的库存）
		Stock sourceStock = stockService.getById(detail.getStockId());
		if (sourceStock == null) {
			throw new JeecgBootException("原库存记录不存在，无法创建余料库存");
		}

		// 追溯单价（从原库存或入库明细）
		BigDecimal unitPrice = sourceStock.getCostPrice();
		if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) == 0) {
			unitPrice = getUnitPriceFromStock(detail.getStockId());
		}

		List<String> residualIds = getResidualWarehouseIds();
		if (residualIds.isEmpty()) {
			throw new JeecgBootException("未配置余料库");
		}
		String residualWhId = residualIds.get(0);

		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		// ===== 第1步：创建余料入库单 =====
		StockIn stockIn = new StockIn();
		stockIn.setStockInNo(serialNoService.generateSerialNo(SerialNoPrefixEnum.STOCK_IN.getPrefix()));
		stockIn.setStockInType("RESIDUAL");
		stockIn.setSourceOrderId(stockOut.getId());
		stockIn.setSourceOrderNo(stockOut.getStockOutNo());
		stockIn.setWarehouseId(residualWhId);
		stockIn.setTotalAmount(qty.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP));
		stockIn.setStatus(StockEnum.StockInStatus.FINISHED.getCode());
		stockIn.setApproveStatus(ApproveStatusEnum.PASS.getCode());
		stockIn.setApproveId(user.getId());
		stockIn.setApproveName(user.getRealname());
		stockIn.setApproveTime(new DateTime());
		stockIn.setStockInTime(new DateTime());
		stockIn.setCreateBy(user.getRealname());
		stockIn.setCreateTime(new DateTime());
		stockIn.setSysOrgCode(user.getOrgCode());

		stockInMapper.insert(stockIn);

		// ===== 第2步：创建入库明细 =====
		StockInDetail inDetail = new StockInDetail();
		inDetail.setStockInId(stockIn.getId());
		inDetail.setStockInNo(stockIn.getStockInNo());
		inDetail.setGoodsType(detail.getGoodsType());
		inDetail.setGoodsId(detail.getGoodsId());
		inDetail.setGoodsCode(detail.getGoodsCode());
		inDetail.setGoodsName(detail.getGoodsName());
		inDetail.setGoodsSpec(detail.getGoodsSpec());
		inDetail.setUnit(detail.getUnit());
		inDetail.setApplyQty(qty);
		inDetail.setActualQty(qty);
		inDetail.setUnitPrice(unitPrice);
		inDetail.setTotalAmount(qty.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP));
		inDetail.setBatchNo(detail.getBatchNo());
		// ❌ 不设置 productionBatchId（只有产品入库才有）
		//inDetail.setProductionBatchId(detail.getProductionBatchId());

		inDetail.setQcStatus(sourceStock.getQcStatus());
		inDetail.setCreateBy(user.getRealname());
		inDetail.setCreateTime(new DateTime());

		stockInDetailMapper.insert(inDetail);

		// ===== 第3步：创建库存记录（从原库存复制完整信息）=====
		Stock stock = new Stock();
		stock.setStockInTime(new DateTime());

		// 物料信息（从出库明细）
		stock.setBatchNo(detail.getBatchNo());
		stock.setGoodsCode(detail.getGoodsCode());
		stock.setGoodsName(detail.getGoodsName());
		stock.setGoodsId(detail.getGoodsId());
		stock.setGoodsSpec(detail.getGoodsSpec());
		stock.setGoodsType(detail.getGoodsType());
		stock.setUnit(detail.getUnit());

		// 【关键】从原库存记录复制（不复制 production_batch_id）
		stock.setSupplierId(sourceStock.getSupplierId());
		stock.setSupplierName(sourceStock.getSupplierName());
		stock.setShelfLife(sourceStock.getShelfLife());
		stock.setExpiryDate(sourceStock.getExpiryDate());
		stock.setProductionDate(sourceStock.getProductionDate());
		stock.setCostPrice(sourceStock.getCostPrice());
		stock.setCostTotal(qty.multiply(sourceStock.getCostPrice()).setScale(2, RoundingMode.HALF_UP));
		stock.setIsProduct(sourceStock.getIsProduct());
		stock.setQcStatus(sourceStock.getQcStatus());

		// 仓库位置（余料库）
		stock.setWarehouseId(residualWhId);
		WarehouseArea area = warehouseAreaService.lambdaQuery()
				.eq(WarehouseArea::getAreaCode, "STAGING")
				.eq(WarehouseArea::getWarehouseId, residualWhId)
				.eq(WarehouseArea::getDelFlag, "0")
				.one();
		if (area != null) {
			stock.setAreaId(area.getId());
		}

		// 【关键】关联新生成的入库明细
		stock.setInDetailId(inDetail.getId());

		// 数量
		stock.setOriginalQty(qty);
		stock.setQuantity(qty);
		stock.setLockedQty(BigDecimal.ZERO);

		// 安全库存/最大库存（从原库存复制，或重置为0）
		stock.setSafetyStockQty(sourceStock.getSafetyStockQty());
		stock.setMaxStockQty(sourceStock.getMaxStockQty());

		// 备注
		stock.setRemark("超量入余料库，来源出库单：" + stockOut.getStockOutNo() + "，原库存ID：" + sourceStock.getId());

		stockService.save(stock);

		// 更新出库明细备注
		String logMsg = String.format("超量%s入余料库[%s],单价%s,入库单:%s",
				qty.stripTrailingZeros().toPlainString(),
				residualWhId,
				unitPrice != null ? unitPrice.stripTrailingZeros().toPlainString() : "0",
				stockIn.getStockInNo());
		detail.setRemark((detail.getRemark() != null ? detail.getRemark() + " | " : "") + logMsg);
		stockOutDetailService.updateById(detail);

		log.info("超量入余料库: detailId={}, qty={}, unitPrice={}, stockId={}, stockInNo={}",
				detail.getId(), qty, unitPrice, stock.getId(), stockIn.getStockInNo());
	}

	/**
	 * 【优化】超量入余料库 - 从原库存记录复制完整信息
	 * 核心改进：以原库存记录为主数据源，出库明细仅补充关联关系
	 */
	private void createResidualStock(StockOutDetail detail, StockOut stockOut, BigDecimal qty) {
		// ===== 第0步：获取原库存记录（核心数据源）=====
		Stock sourceStock = stockService.getById(detail.getStockId());
		if (sourceStock == null) {
			throw new JeecgBootException("原库存记录不存在，无法创建余料库存");
		}

		// 追溯单价（优先原库存成本价，其次追溯入库明细）
		BigDecimal unitPrice = sourceStock.getCostPrice();
		if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) == 0) {
			unitPrice = getUnitPriceFromStock(detail.getStockId());
		}

		// 获取余料库
		List<String> residualIds = getResidualWarehouseIds();
		if (residualIds.isEmpty()) {
			throw new JeecgBootException("未配置余料库");
		}
		String residualWhId = residualIds.get(0);

		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		// ===== 第1步：创建余料入库单（简化版，只保留必要字段）=====
		StockIn stockIn = buildResidualStockIn(stockOut, sourceStock, residualWhId, qty, unitPrice, user);
		stockInMapper.insert(stockIn);

		// ===== 第2步：创建入库明细（从原库存+出库明细组合）=====
		StockInDetail inDetail = buildResidualStockInDetail(detail, sourceStock, stockIn, qty, unitPrice, user);
		stockInDetailMapper.insert(inDetail);

		// ===== 第3步：创建库存记录（从原库存复制完整信息）=====
		Stock stock = buildResidualStock(detail, sourceStock, stockOut, inDetail, residualWhId, qty, user);
		stockService.save(stock);

		// ===== 第4步：更新出库明细备注=====
		updateDetailRemark(detail, stockOut, qty, unitPrice, residualWhId, stockIn.getStockInNo());

		log.info("超量入余料库完成: detailId={}, qty={}, stockId={}, stockInNo={}",
				detail.getId(), qty, stock.getId(), stockIn.getStockInNo());
	}

// ==================== 私有构建方法（职责分离）====================

	/**
	 * 构建余料入库单主表
	 */
	private StockIn buildResidualStockIn(StockOut stockOut, Stock sourceStock, String residualWhId,
										 BigDecimal qty, BigDecimal unitPrice, LoginUser user) {
		StockIn stockIn = new StockIn();
		stockIn.setStockInNo(serialNoService.generateSerialNo(SerialNoPrefixEnum.STOCK_IN.getPrefix()));
		stockIn.setStockInType("RESIDUAL");           // 余料入库类型
		stockIn.setSourceOrderId(stockOut.getId());   // 关联出库单
		stockIn.setSourceOrderNo(stockOut.getStockOutNo());
		stockIn.setSourceOrderType("STOCK_OUT");      // 来源类型：出库单
		stockIn.setWarehouseId(residualWhId);
		Warehouse warehouse = warehouseService.getById(residualWhId);
		stockIn.setWarehouseName(warehouse != null ? warehouse.getName() : residualWhId);

		// ===== 供应商信息（从原库存复制）=====
		stockIn.setSupplierId(sourceStock.getSupplierId());
		stockIn.setSupplierName(sourceStock.getSupplierName());

		stockIn.setTotalAmount(qty.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP));

		// 状态：直接完成（余料入库无需再次审核）
		stockIn.setStatus(StockEnum.StockInStatus.FINISHED.getCode());
		stockIn.setApproveStatus(ApproveStatusEnum.PASS.getCode());
		stockIn.setApproveId(user.getId());
		stockIn.setApproveName(user.getRealname());
		stockIn.setApproveTime(new DateTime());
		stockIn.setStockInTime(new DateTime());

		stockIn.setApplyTime(new DateTime());
		// 操作人信息
		stockIn.setOperatorUserId(user.getId());
		stockIn.setOperatorName(user.getRealname());

		// ===== 备注 =====
		stockIn.setRemark(String.format(
				"超量入余料库，来源出库单：%s，物料：%s，数量：%s，原库存ID：%s",
				stockOut.getStockOutNo(),
				sourceStock.getGoodsName(),
				qty.stripTrailingZeros().toPlainString(),
				sourceStock.getId()
		));

		stockIn.setCreateBy(user.getRealname());
		stockIn.setCreateTime(new DateTime());
		stockIn.setSysOrgCode(user.getOrgCode());

		return stockIn;
	}

	/**
	 * 构建余料入库明细
	 * 数据源：出库明细（物料标识）+ 原库存（批次、日期、质检）
	 */
	private StockInDetail buildResidualStockInDetail(StockOutDetail detail, Stock sourceStock,
													 StockIn stockIn, BigDecimal qty,
													 BigDecimal unitPrice, LoginUser user) {
		StockInDetail inDetail = new StockInDetail();
		inDetail.setStockInId(stockIn.getId());
		inDetail.setStockInNo(stockIn.getStockInNo());

		// 物料信息：从出库明细（最准确，因为出库时已经确定）
		inDetail.setGoodsType(detail.getGoodsType());
		inDetail.setGoodsId(detail.getGoodsId());
		inDetail.setGoodsCode(detail.getGoodsCode());
		inDetail.setGoodsName(detail.getGoodsName());
		inDetail.setGoodsSpec(detail.getGoodsSpec());
		inDetail.setUnit(detail.getUnit());

		// 数量与金额
		inDetail.setApplyQty(qty);
		inDetail.setActualQty(qty);
		inDetail.setUnitPrice(unitPrice);
		inDetail.setTotalAmount(qty.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP));

		// 批次与日期：从原库存记录复制（保留原始追溯信息）
		inDetail.setBatchNo(sourceStock.getBatchNo());
		inDetail.setProductionDate(sourceStock.getProductionDate());
		inDetail.setShelfLife(sourceStock.getShelfLife());
		inDetail.setExpiryDate(sourceStock.getExpiryDate());

		// 质检状态：继承原库存状态
		inDetail.setQcStatus(sourceStock.getQcStatus());

		// 不设置 productionBatchId（余料不是产品入库，没有生产批次）最后还是设置一下吧，防止使用了中间体自己生产的
		inDetail.setProductionBatchId(sourceStock.getProductionBatchId());

		// 添加备注：记录超量来源
		inDetail.setRemark(String.format(
				"超量入余料库，来源出库明细：%s，原库存：%s，数量：%s",
				detail.getId(),
				sourceStock.getId(),
				qty.stripTrailingZeros().toPlainString()
		));

		inDetail.setCreateBy(user.getRealname());
		inDetail.setCreateTime(new DateTime());

		return inDetail;
	}

	/**
	 * 构建余料库存记录
	 * 【核心】从原库存记录复制完整信息，确保数据完整性
	 */
	private Stock buildResidualStock(StockOutDetail detail, Stock sourceStock,
									 StockOut stockOut, StockInDetail inDetail,
									 String residualWhId, BigDecimal qty, LoginUser user) {
		Stock stock = new Stock();

		// ===== A. 物料基础信息（从出库明细，最准确）=====
		stock.setGoodsId(detail.getGoodsId());
		stock.setGoodsCode(detail.getGoodsCode());
		stock.setGoodsName(detail.getGoodsName());
		stock.setGoodsSpec(detail.getGoodsSpec());
		stock.setGoodsType(detail.getGoodsType());
		stock.setUnit(detail.getUnit());
		stock.setBatchNo(detail.getBatchNo());

		// ===== B. 供应商与追溯信息（从原库存记录复制）【关键改进】=====
		stock.setSupplierId(sourceStock.getSupplierId());
		stock.setSupplierName(sourceStock.getSupplierName());
		stock.setProductionDate(sourceStock.getProductionDate());
		stock.setShelfLife(sourceStock.getShelfLife());
		stock.setExpiryDate(sourceStock.getExpiryDate());

		// ===== C. 成本信息（从原库存，保持成本链完整）=====
		stock.setCostPrice(sourceStock.getCostPrice());
		stock.setCostTotal(qty.multiply(
				sourceStock.getCostPrice() != null ? sourceStock.getCostPrice() : BigDecimal.ZERO
		).setScale(2, RoundingMode.HALF_UP));

		// ===== D. 产品标识与质检（从原库存）=====
		stock.setIsProduct(sourceStock.getIsProduct());
		stock.setQcStatus(sourceStock.getQcStatus());

		// ===== E. 安全库存配置（从原库存复制，或按余料库策略重置）=====
		stock.setSafetyStockQty(sourceStock.getSafetyStockQty());
		stock.setMaxStockQty(sourceStock.getMaxStockQty());

		// ===== F. 仓库位置（余料库）=====
		stock.setWarehouseId(residualWhId);
		// 区域：余料库的暂存区（与正常入库逻辑一致）
		WarehouseArea area = warehouseAreaService.getAreaByCode(residualWhId, "STAGING");
		if (area != null) {
			stock.setAreaId(area.getId());
		}
		// 货架、货位：余料库通常只管理到区域，货架货位置空
		stock.setShelfId(null);
		stock.setLocationId(null);

		// ===== G. 关联关系（关键）=====
		stock.setInDetailId(inDetail.getId());  // 关联新生成的入库明细

		// ===== H. 数量信息 =====
		stock.setOriginalQty(qty);
		stock.setQuantity(qty);
		stock.setLockedQty(BigDecimal.ZERO);  // 新库存无锁定

		// ===== I. 系统字段 =====
		stock.setStockInTime(new DateTime());
		stock.setCreateBy(user.getRealname());
		stock.setCreateTime(new DateTime());
		stock.setSysOrgCode(user.getOrgCode());

		// ===== J. 备注（追溯来源）=====
		stock.setRemark(String.format(
				"超量入余料库，来源出库单：%s，原库存ID：%s，原入库明细：%s",
				stockOut.getStockOutNo(),
				sourceStock.getId(),
				sourceStock.getInDetailId()
		));

		return stock;
	}

	/**
	 * 更新出库明细备注
	 */
	private void updateDetailRemark(StockOutDetail detail, StockOut stockOut,
									BigDecimal qty, BigDecimal unitPrice,
									String residualWhId, String stockInNo) {
		String logMsg = String.format("超量%s入余料库[%s],单价%s,入库单:%s",
				qty.stripTrailingZeros().toPlainString(),
				residualWhId,
				unitPrice != null ? unitPrice.stripTrailingZeros().toPlainString() : "0",
				stockInNo);

		String existingRemark = detail.getRemark();
		detail.setRemark(StringUtils.isNotBlank(existingRemark)
				? existingRemark + " | " + logMsg
				: logMsg);
		stockOutDetailService.updateById(detail);
	}
	/**
	 * 从库存记录追溯单价
	 */
	private BigDecimal getUnitPriceFromStock(String stockId) {
		if (StringUtils.isBlank(stockId)) {
			return BigDecimal.ZERO;
		}
		try {
			Stock stock = stockService.getById(stockId);
			if (stock == null || StringUtils.isBlank(stock.getInDetailId())) {
				return BigDecimal.ZERO;
			}
			StockInDetail inDetail = stockInDetailService.getById(stock.getInDetailId());
			return inDetail != null && inDetail.getUnitPrice() != null
					? inDetail.getUnitPrice() : BigDecimal.ZERO;
		} catch (Exception e) {
			log.error("追溯单价失败: {}", stockId, e);
			return BigDecimal.ZERO;
		}
	}

	/**
	 * 更新需求表已出库量（支持多ID）
	 */
	private void updateMaterialIssued(String requirementId, BigDecimal qty) {
		if (StringUtils.isBlank(requirementId) || qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}
		String[] ids = requirementId.split(",");
		if (ids.length == 1) {
			productionMaterialService.increaseIssuedQty(ids[0].trim(), qty);
		} else {
			BigDecimal avg = qty.divide(new BigDecimal(ids.length), 6, RoundingMode.HALF_UP);
			for (int i = 0; i < ids.length; i++) {
				BigDecimal allocate = (i == ids.length - 1)
						? qty.subtract(avg.multiply(new BigDecimal(i)))
						: avg;
				productionMaterialService.increaseIssuedQty(ids[i].trim(), allocate);
			}
		}
	}

	// ==================== 审核驳回/取消 ====================
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void rejectStockOut(StockOut stockOut) {
		if (stockOut == null) {
			throw new RuntimeException("出库单不存在");
		}

		List<StockOutDetail> details = stockOutDetailService.selectByMainId(stockOut.getId());
		for (StockOutDetail detail : details) {
			unlockMaterial(detail);
		}

		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		stockOut.setApproveId(user.getId());
		stockOut.setApproveName(user.getRealname());
		stockOut.setApproveTime(new DateTime());
		stockOut.setApproveStatus(ApproveStatusEnum.REJECT.getCode());
		stockOut.setApproveRemark(stockOut.getApproveRemark());
		stockOut.setStatus(StockEnum.StockOutStatus.CANCEL.getCode());
		this.updateById(stockOut);
	}

	/**
	 * 回滚单条明细（驳回时）
	 * 统一用 actualQty 回滚
	 */
	private void unlockMaterial(StockOutDetail detail) {
		String reqId = detail.getRequirementId();
		BigDecimal actualQty = detail.getActualQty();

		if (StringUtils.isBlank(reqId) || actualQty == null || actualQty.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		// 1. 释放库存锁定（正常和超量都一样）
		if (detail.getStockId() != null) {
			stockService.releaseLockQty(detail.getStockId(), actualQty);
		}

//		// 2. 回滚余料库锁定（如果是余料来源）
//		if ("RESIDUAL".equals(detail.getSourceType()) && detail.getResidualId() != null) {
//			residualInventoryService.unlockQty(detail.getResidualId(), actualQty);
//		}

		// 3. 回滚物料需求表（统一用 actualQty）
		if ("1".equals(detail.getOverFlag())) {
			// 超量明细：减 over_qty，删除余料记录
			rollbackMaterialOver(reqId, actualQty);

		} else {
			// 正常明细：减 locked_qty
			rollbackMaterialLock(reqId, actualQty);
		}

		// 4. 更新状态
		updateMaterialStatus(reqId);
	}

	// ==================== 删除出库单 ====================
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteStockOut(String stockOutId) {
		StockOut stockOut = this.getById(stockOutId);
		if (stockOut == null) {
			return;
		}

		if ("APPLY".equals(stockOut.getStatus())) {
			List<StockOutDetail> details = stockOutDetailService.selectByMainId(stockOutId);
			for (StockOutDetail detail : details) {
				unlockMaterial(detail);
			}
		}

		this.delMain(stockOutId);
	}



	/**
	 * 创建正常库存出库明细
	 */
	private StockOutDetail createOutDetail(StockOutDetail source,
										   Stock stock,
										   StockInDetail inDetail,
										   BigDecimal qty,
										   StockOut stockOut,
										   String overFlag) {
		StockOutDetail detail = new StockOutDetail();
		detail.setStockOutId(stockOut.getId());
		detail.setStockOutNo(stockOut.getStockOutNo());
		detail.setGoodsId(source.getGoodsId());
		detail.setGoodsCode(source.getGoodsCode());
		detail.setGoodsName(source.getGoodsName());
		detail.setGoodsSpec(source.getGoodsSpec());
		detail.setGoodsType(source.getGoodsType());
		detail.setUnit(source.getUnit());
		detail.setSourceType("NORMAL");
		detail.setResidualId(null);
		detail.setStockId(stock.getId());
		detail.setInDetailId(stock.getInDetailId());
		// applyQty和actualQty由调用方设置
		detail.setOverFlag(overFlag);
		if (inDetail != null) {
			detail.setCostPrice(inDetail.getUnitPrice());
			if (detail.getCostPrice() != null) {
				detail.setCostTotal(detail.getCostPrice().multiply(qty).setScale(2, RoundingMode.HALF_UP));
			}
		}
		//复制销售单价和金额
		detail.setSalesPrice(source.getSalesPrice() != null ? source.getSalesPrice() : BigDecimal.ZERO);
		detail.setSalesTotal(source.getSalesTotal() != null ? source.getSalesTotal() : BigDecimal.ZERO);


		detail.setBatchNo(stock.getBatchNo());
		detail.setExpiryDate(stock.getExpiryDate());
		detail.setProductionDate(stock.getProductionDate());
		detail.setShelfLife(stock.getShelfLife());
		detail.setRequirementId(source.getRequirementId());
        detail.setProductionBatchId(source.getProductionBatchId());
	    detail.setProductionBatchNo(source.getProductionBatchNo());
		detail.setSourceDetailId(source.getSourceDetailId());
		detail.setSerialNo(source.getSerialNo());
		return detail;
	}

	// ==================== 回滚方法（支持多ID） ====================

	/**
	 * 回滚超量（逐条减 actualQty）
	 */
	private void rollbackMaterialOver(String requirementId, BigDecimal qty) {
		if (StringUtils.isBlank(requirementId) || qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		String[] ids = requirementId.split(",");
		if (ids.length == 1) {
			doRollbackMaterialOver(ids[0].trim(), qty);
		} else {
			BigDecimal avg = qty.divide(new BigDecimal(ids.length), 6, RoundingMode.HALF_UP);
			for (int i = 0; i < ids.length; i++) {
				BigDecimal allocate = (i == ids.length - 1)
						? qty.subtract(avg.multiply(new BigDecimal(i)))
						: avg;
				doRollbackMaterialOver(ids[i].trim(), allocate);
			}
		}
	}

	private void doRollbackMaterialOver(String reqId, BigDecimal qty) {
		ProductionMaterial material = productionMaterialService.getById(reqId);
		if (material == null) {
			log.warn("回滚超量失败，物料需求不存在: {}", reqId);
			return;
		}

		BigDecimal currentOver = material.getOverQty() == null ? BigDecimal.ZERO : material.getOverQty();
		BigDecimal newOver = currentOver.subtract(qty).max(BigDecimal.ZERO);

		material.setOverQty(newOver);
		productionMaterialService.updateById(material);

		log.info("回滚超量: reqId={}, rollbackQty={}, newOver={}", reqId, qty, newOver);
	}

	/**
	 * 回滚锁定量（逐条减 actualQty）
	 */
	private void rollbackMaterialLock(String requirementId, BigDecimal qty) {
		if (StringUtils.isBlank(requirementId) || qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		String[] ids = requirementId.split(",");
		if (ids.length == 1) {
			doRollbackMaterialLock(ids[0].trim(), qty);
		} else {
			BigDecimal avg = qty.divide(new BigDecimal(ids.length), 6, RoundingMode.HALF_UP);
			for (int i = 0; i < ids.length; i++) {
				BigDecimal allocate = (i == ids.length - 1)
						? qty.subtract(avg.multiply(new BigDecimal(i)))
						: avg;
				doRollbackMaterialLock(ids[i].trim(), allocate);
			}
		}
	}

	private void doRollbackMaterialLock(String reqId, BigDecimal qty) {
		ProductionMaterial material = productionMaterialService.getById(reqId);
		if (material == null) {
			log.warn("回滚锁定量失败，物料需求不存在: {}", reqId);
			return;
		}

		BigDecimal currentLocked = material.getLockedQty() == null ? BigDecimal.ZERO : material.getLockedQty();
		BigDecimal newLocked = currentLocked.subtract(qty).max(BigDecimal.ZERO);

		material.setLockedQty(newLocked);
		productionMaterialService.updateById(material);

		log.info("回滚锁定量: reqId={}, rollbackQty={}, newLocked={}", reqId, qty, newLocked);
	}

	/**
	 * 回滚已出库量（预留）
	 */
	private void rollbackMaterialIssued(String requirementId, BigDecimal qty) {
		if (StringUtils.isBlank(requirementId) || qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		String[] ids = requirementId.split(",");
		if (ids.length == 1) {
			doRollbackMaterialIssued(ids[0].trim(), qty);
		} else {
			BigDecimal avg = qty.divide(new BigDecimal(ids.length), 6, RoundingMode.HALF_UP);
			for (int i = 0; i < ids.length; i++) {
				BigDecimal allocate = (i == ids.length - 1)
						? qty.subtract(avg.multiply(new BigDecimal(i)))
						: avg;
				doRollbackMaterialIssued(ids[i].trim(), allocate);
			}
		}
	}

	private void doRollbackMaterialIssued(String reqId, BigDecimal qty) {
		ProductionMaterial material = productionMaterialService.getById(reqId);
		if (material == null) {
			log.warn("回滚已出库量失败，物料需求不存在: {}", reqId);
			return;
		}

		BigDecimal currentIssued = material.getIssuedQty() == null ? BigDecimal.ZERO : material.getIssuedQty();
		BigDecimal newIssued = currentIssued.subtract(qty).max(BigDecimal.ZERO);

		material.setIssuedQty(newIssued);
		productionMaterialService.updateById(material);

		log.info("回滚已出库量: reqId={}, rollbackQty={}, newIssued={}", reqId, qty, newIssued);
	}



	/**
	 * 更新物料需求状态
	 */
	private void updateMaterialStatus(String requirementId) {
		if (StringUtils.isBlank(requirementId)) {
			return;
		}
		String[] ids = requirementId.split(",");
		for (String id : ids) {
			doUpdateMaterialStatus(id.trim());
		}
	}

	private void doUpdateMaterialStatus(String reqId) {
		ProductionMaterial material = productionMaterialService.getById(reqId);
		if (material == null) {
			log.warn("更新状态失败，物料需求不存在: {}", reqId);
			return;
		}

		BigDecimal required = material.getRequiredQty() == null ? BigDecimal.ZERO : material.getRequiredQty();
		BigDecimal issued = material.getIssuedQty() == null ? BigDecimal.ZERO : material.getIssuedQty();
		BigDecimal locked = material.getLockedQty() == null ? BigDecimal.ZERO : material.getLockedQty();
		BigDecimal over = material.getOverQty() == null ? BigDecimal.ZERO : material.getOverQty();

		String newStatus;
		if (issued.compareTo(required) >= 0) {
			newStatus = "2";  // 已完成
		} else if (issued.compareTo(BigDecimal.ZERO) > 0 || locked.compareTo(BigDecimal.ZERO) > 0 || over.compareTo(BigDecimal.ZERO) > 0) {
			newStatus = "1";  // 部分发料（有已出库、锁定或超量）
		} else {
			newStatus = "0";  // 待发料
		}

		if (!newStatus.equals(material.getStatus())) {
			material.setStatus(newStatus);
			productionMaterialService.updateById(material);
			log.info("更新物料状态: reqId={}, oldStatus={}, newStatus={}",
					reqId, material.getStatus(), newStatus);
		}
	}



}