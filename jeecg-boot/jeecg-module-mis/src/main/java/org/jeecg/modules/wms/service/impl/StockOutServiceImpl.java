package org.jeecg.modules.wms.service.impl;

import cn.hutool.core.date.DateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.common.enums.ApproveStatusEnum;
import org.jeecg.modules.common.enums.StockEnum;
import org.jeecg.modules.mes.entity.ProductionMaterial;
import org.jeecg.modules.mes.service.IProductionMaterialService;
import org.jeecg.modules.wms.entity.*;
import org.jeecg.modules.wms.mapper.StockOutDetailMapper;
import org.jeecg.modules.wms.mapper.StockOutMapper;
import org.jeecg.modules.wms.service.*;
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
	private IStockService stockService;
	@Autowired
	private IStockOutDetailService stockOutDetailService;
	@Autowired
	private IStockInDetailService stockInDetailService;
	@Autowired
	private IProductionMaterialService productionMaterialService;
	@Autowired
	private IResidualInventoryService residualInventoryService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(StockOut stockOut, List<StockOutDetail> stockOutDetailList) {
		stockOutMapper.insert(stockOut);
		if(stockOutDetailList!=null && stockOutDetailList.size()>0) {
			for(StockOutDetail entity:stockOutDetailList) {
				//外键设置
				entity.setStockOutId(stockOut.getId());
				stockOutDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(StockOut stockOut,List<StockOutDetail> stockOutDetailList) {
		stockOutMapper.updateById(stockOut);
		
		//1.先删除子表数据
		stockOutDetailMapper.deleteByMainId(stockOut.getId());
		
		//2.子表数据重新插入
		if(stockOutDetailList!=null && stockOutDetailList.size()>0) {
			for(StockOutDetail entity:stockOutDetailList) {
				//外键设置
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
	 * 只增加 locked_qty，不扣减 quantity
	 * @param stockOut 出库主表
	 * @param detailList 出库明细（用户填写的物料和申请数量）
	 * @return 匹配后的出库明细（已关联stock_id，可能拆分多条）
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<StockOutDetail> matchAndLockStock(StockOut stockOut, List<StockOutDetail> detailList) {
		List<StockOutDetail> resultList = new ArrayList<>();

		for (StockOutDetail detail : detailList) {
			// 通过Service查询可用库存
			List<Stock> stocks = stockService.selectAvailableStockByGoods(
					stockOut.getWarehouseId(),
					detail.getGoodsId(),
					detail.getBatchNo()
			);

			if (stocks.isEmpty()) {
				throw new JeecgBootException(
						String.format("物料[%s]在仓库中无可用库存", detail.getGoodsName())
				);
			}

			// 计算总可用量
			BigDecimal totalAvailable = stocks.stream()
					.map(s -> s.getQuantity().subtract(s.getLockedQty()))
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			if (totalAvailable.compareTo(detail.getApplyQty()) < 0) {
				throw new JeecgBootException(String.format(
						"物料[%s]库存不足，申请：%s，可用：%s",
						detail.getGoodsName(),
						detail.getApplyQty(),
						totalAvailable
				));
			}

			//检查是否有需求表id，如果存在更新对应的需求表中的仓库id
			if(StringUtils.isNotBlank(detail.getRequirementId())){
				if(detail.getRequirementId().length() <=36){
					//单个需求id
					ProductionMaterial material = productionMaterialService.getById(detail.getRequirementId());
					if(material != null ){
						material.setWarehouseId(stockOut.getWarehouseId());
						productionMaterialService.updateById(material);
					}
				}
				else{
					//多个需求id，再进行拆分
					String[] reqIds = detail.getRequirementId().split(",");
					for(String reqId:reqIds){
						ProductionMaterial material = productionMaterialService.getById(reqId);
						if(material != null ){
							material.setWarehouseId(stockOut.getWarehouseId());
							productionMaterialService.updateById(material);
						}
					}
				}
			}

			// FIFO分配并锁定
			BigDecimal remaining = detail.getApplyQty();
			int seq = 1;

			for (Stock stock : stocks) {
				if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
					break;
				}

				BigDecimal available = stock.getQuantity().subtract(stock.getLockedQty());
				if (available.compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				BigDecimal allocateQty = available.min(remaining);

				// 通过Service锁定库存
				stockService.increaseLockQty(stock.getId(), allocateQty);

				// 生成出库明细
				// 【关键】通过in_detail_id查询入库明细，获取单价和生产日期
				StockInDetail inDetail = stockInDetailService.getById(stock.getInDetailId());

				// 生成出库明细
				StockOutDetail outDetail = createOutDetail(
						detail,
						stock,
						inDetail,  // 传入入库明细
						allocateQty,
						stockOut
				);
				resultList.add(outDetail);

				remaining = remaining.subtract(allocateQty);
			}
		}

		return resultList;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void confirmStockOut(String stockOutId) {
		List<StockOutDetail> details = stockOutDetailService.selectByMainId(stockOutId);

		for (StockOutDetail detail : details) {
			// 检查必要字段
			if (detail.getStockId() == null || detail.getActualQty() == null
					|| detail.getActualQty().compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			// 1. 扣减库存
			stockService.confirmDeduct(detail.getStockId(), detail.getActualQty());

			// 2. 更新需求表
			if (StringUtils.isBlank(detail.getRequirementId())) {
				continue; // 无需求单关联
			}

			String[] reqIds = detail.getRequirementId().split(",");
			BigDecimal remainingToAllocate = detail.getActualQty();
			StringBuilder allocateLog = new StringBuilder(); // 记录分配明细

			for (String reqId : reqIds) {
				ProductionMaterial material = productionMaterialService.getById(reqId.trim());
				if (material == null) continue;

				// 计算剩余待发
				BigDecimal materialRemaining = material.getRemainingQty() != null
						? material.getRemainingQty()
						: material.getRequiredQty().subtract(material.getIssuedQty());

				// 本次分配
				BigDecimal allocateQty = materialRemaining.min(remainingToAllocate);

				if (allocateQty.compareTo(BigDecimal.ZERO) > 0) {
					productionMaterialService.increaseIssuedQty(reqId.trim(), allocateQty);
					remainingToAllocate = remainingToAllocate.subtract(allocateQty);

					allocateLog.append(String.format("需求%s分配%s;", reqId.substring(0,8), allocateQty));
				}

				if (remainingToAllocate.compareTo(BigDecimal.ZERO) <= 0) {
					break; // 分配完毕
				}
			}

			// 超量处理
			if (remainingToAllocate.compareTo(BigDecimal.ZERO) > 0) {
				String overMsg = String.format("超量%s", remainingToAllocate);
				allocateLog.append(overMsg);
				log.warn("出库超量，物料:{}, {}", detail.getGoodsName(), overMsg);

				// 【可选】更新明细remark记录分配情况
				detail.setRemark((detail.getRemark() != null ? detail.getRemark() + " | " : "")
						+ "分配:" + allocateLog.toString());
				stockOutDetailService.updateById(detail);
			}
		}
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

	/**
	 * 创建出库明细
	 */
	private StockOutDetail createOutDetail(StockOutDetail source, Stock stock, StockInDetail inDetail,
										   BigDecimal qty, StockOut stockOut) {
		StockOutDetail detail = new StockOutDetail();

		// 复制基本信息
		BeanUtils.copyProperties(source, detail);

		// 关联主表
		detail.setStockOutId(stockOut.getId());
		detail.setStockOutNo(stockOut.getStockOutNo());

		// 关联库存
		detail.setStockId(stock.getId());
		detail.setInDetailId(stock.getInDetailId());

		// 设置数量（申请时actualQty等于申请数量）
		detail.setApplyQty(qty);
		detail.setActualQty(qty);

		// 成本信息
		// 单价从入库明细取
		if (inDetail != null) {
			// 入库时的单价（本币）
			detail.setCostPrice(inDetail.getUnitPrice());

			// 计算成本金额
			if (detail.getCostPrice() != null) {
				detail.setCostTotal(
						detail.getCostPrice().multiply(qty)
								.setScale(2, RoundingMode.HALF_UP)
				);
			}
		}
		else {
			// 备用：如果找不到入库明细，从stock取（不应该发生）
			log.warn("未找到入库明细，stockId={}", stock.getId());
			detail.setProductionDate(stock.getProductionDate());
			detail.setExpiryDate(stock.getExpiryDate());
		}
		// 计算销售金额（如果销售价有值）
		if (detail.getSalesPrice() != null) {
			detail.setSalesTotal(
					detail.getSalesPrice().multiply(qty)
							.setScale(2, RoundingMode.HALF_UP)
			);
		}
		// 批次信息
		detail.setBatchNo(stock.getBatchNo());
		detail.setExpiryDate(stock.getExpiryDate());
		detail.setProductionDate(stock.getProductionDate());
		detail.setShelfLife(stock.getShelfLife());

		return detail;
	}

	// ==================== 申请出库 ====================
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void applyStockOut(StockOut stockOut, List<StockOutDetail> details) {
		// 1. 保存主表和明细
		this.saveMain(stockOut, details);

		// 2. 更新物料需求表锁定状态
		for (StockOutDetail detail : details) {
			lockMaterial(detail, stockOut.getRequiredDate());
		}
	}

	private void lockMaterial(StockOutDetail detail, Date requiredDate) {
		String reqId = detail.getRequirementId();
		BigDecimal applyQty = detail.getApplyQty();

		if (StringUtils.isBlank(reqId) || applyQty == null) {
			return;
		}

		// 单个需求ID
		if (reqId.length() <= 36) {
			doLockMaterial(reqId, applyQty, requiredDate, detail);
		} else {
			// 多批次（兼容处理）
			String[] reqIds = reqId.split(",");
			BigDecimal avgQty = applyQty.divide(new BigDecimal(reqIds.length), 6, RoundingMode.HALF_UP);

			for (int i = 0; i < reqIds.length; i++) {
				String id = reqIds[i].trim();
				BigDecimal qty = (i == reqIds.length - 1)
						? applyQty.subtract(avgQty.multiply(new BigDecimal(i)))
						: avgQty;
				doLockMaterial(id, qty, requiredDate, detail);
			}
		}
	}

	private void doLockMaterial(String materialReqId, BigDecimal applyQty, Date requiredDate, StockOutDetail detail) {
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
		// 计算锁定和超量
		BigDecimal realRemaining = required.subtract(issued).subtract(currentLocked);
		BigDecimal lockQty = applyQty.min(realRemaining.max(BigDecimal.ZERO));//本次正常锁定
		BigDecimal overQty = applyQty.subtract(lockQty).max(BigDecimal.ZERO);//本次超量

		BigDecimal newTotalLocked = currentLocked.add(lockQty);//物料需求表中累计锁定量
		//String status = issued.add(newTotalLocked).compareTo(required) >= 0 ? "1" : "0";
		// 0-待发料, 1-部分发料, 2-已完成
		String status;
		if (issued.compareTo(required) >= 0) {
			status = "2";  // 已完成
		} else if (newTotalLocked.compareTo(BigDecimal.ZERO) > 0 || issued.compareTo(BigDecimal.ZERO) > 0) {
			status = "1";  // 部分发料（有锁定或有已出）
		} else {
			status = "0";  // 待发料
		}
		// ========== 出库明细只记录超量信息 ==========
		detail.setOverFlag(overQty.compareTo(BigDecimal.ZERO) > 0 ? "1" : "0");
		detail.setOverQty(overQty);//本次超量

		// 超量备注（可选）
		if (overQty.compareTo(BigDecimal.ZERO) > 0) {
			detail.setRemark(String.format("超量申请：申请%s，需求剩余%s，超量%s",
					applyQty.stripTrailingZeros().toPlainString(),
					realRemaining.stripTrailingZeros().toPlainString(),
					overQty.stripTrailingZeros().toPlainString()));
		}
		// ======================================

		material.setLockedQty(newTotalLocked);
		material.setOverQty(currentOver.add(overQty));
		material.setStatus(status);

		productionMaterialService.updateById(material);
		log.info("物料锁定: reqId={}, lockQty={}, overQty={}, newTotalLocked={}, status={}",
				materialReqId, lockQty, overQty, newTotalLocked, status);
	}

	// ==================== 审核通过 ====================
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void approveStockOut(StockOut stockOut) {
		//StockOut stockOut = this.getById(stockOutId);
		if (stockOut == null) {
			throw new RuntimeException("出库单不存在");
		}
		 if (!"APPLY".equals(stockOut.getStatus())) {
			 throw new RuntimeException("只有申请状态的出库单可以审核");
		 }
		// 1. 获取明细
		List<StockOutDetail> details = stockOutDetailService.selectByMainId(stockOut.getId());

		// 2. 扣减库存、处理需求表、创建余料
		for (StockOutDetail detail : details) {
			confirmDetail(detail, stockOut);
		}

		// 3. 更新出库单状态
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

	private void confirmDetail(StockOutDetail detail, StockOut stockOut) {
		if (detail.getStockId() == null || detail.getActualQty() == null) {
			return;
		}
		// 1. 扣减库存
		stockService.confirmDeduct(detail.getStockId(), detail.getActualQty());
		// 2. 处理需求表
		String reqId = detail.getRequirementId();
		if (StringUtils.isBlank(reqId)) {
			return;
		}

		ProductionMaterial material = productionMaterialService.getById(reqId);
		if (material == null) {
			return;
		}

		BigDecimal actualQty = detail.getActualQty() == null ? BigDecimal.ZERO : detail.getActualQty();
		BigDecimal required = material.getRequiredQty() == null ? BigDecimal.ZERO : material.getRequiredQty();
		BigDecimal issued = material.getIssuedQty() == null ? BigDecimal.ZERO : material.getIssuedQty();
		BigDecimal locked = material.getLockedQty() == null ? BigDecimal.ZERO : material.getLockedQty();
		BigDecimal overApply = material.getOverQty() == null ? BigDecimal.ZERO : material.getOverQty();

		//======从明细获取本次申请数据 ==========
		BigDecimal detailApplyQty = detail.getApplyQty() == null ? BigDecimal.ZERO : detail.getApplyQty();
		BigDecimal detailOverQty = detail.getOverQty() == null ? BigDecimal.ZERO : detail.getOverQty();
		BigDecimal detailNormalLock = detailApplyQty.subtract(detailOverQty);  // 本次正常锁定

		// 本次实际释放的正常锁定 = min(实际出库, 本次正常锁定)
		BigDecimal releaseNormal = actualQty.min(detailNormalLock);
		// 本次实际超量 = 实际出库 - 释放的正常锁定
		BigDecimal actualOverQty = actualQty.subtract(releaseNormal).max(BigDecimal.ZERO);

		// 更新需求表
		BigDecimal newIssued = issued.add(releaseNormal);  // 已出库只加正常部分
		BigDecimal newLocked = locked.subtract(releaseNormal).max(BigDecimal.ZERO);  // 只释放本次正常锁定
		BigDecimal newOverApply = overApply.subtract(actualOverQty).max(BigDecimal.ZERO);  // 超量减少


		String newStatus;
		if (newIssued.compareTo(required) >= 0) {
			newStatus = "2";  // 已完成
		} else if (newIssued.compareTo(BigDecimal.ZERO) > 0 || newLocked.compareTo(BigDecimal.ZERO) > 0) {
			newStatus = "1";  // 部分发料
		} else {
			newStatus = "0";  // 待发料
		}

		material.setIssuedQty(newIssued);           // 已出库数量
		material.setLockedQty(newLocked);           // 锁定数量（释放）
		material.setOverQty(newOverApply);          // 超量申请（减少）
		material.setStatus(newStatus);              // 状态


		productionMaterialService.updateById(material);
		// 3. 超量入余料库
		if (actualOverQty.compareTo(BigDecimal.ZERO) > 0) {
			ResidualInventory residualInventory = new ResidualInventory();
			residualInventory.setMaterialId(detail.getGoodsId());
			residualInventory.setMaterialCode(detail.getGoodsCode());
			residualInventory.setMaterialName(detail.getGoodsName());
			residualInventory.setWarehouseId(stockOut.getWarehouseId());
			residualInventory.setOriginalQty(actualQty);
			residualInventory.setQty(actualOverQty);
			residualInventory.setProductionOrderId(stockOut.getSourceOrderId());
			residualInventory.setProductionOrderNo(stockOut.getSourceOrderCode());
			residualInventory.setProductionBatchId(detail.getProductionBatchId());
			residualInventory.setMaterialBatchNo(detail.getBatchNo());
			residualInventory.setStockOutId(stockOut.getId());
			residualInventory.setStockOutDetailId(detail.getId());

			residualInventoryService.createResidual(residualInventory);
		}

		// 4. 记录日志
		String logMsg = String.format("出库:%s,正常:%s,超量:%s", actualQty, releaseNormal, actualOverQty);
		detail.setRemark((detail.getRemark() != null ? detail.getRemark() + " | " : "") + logMsg);
		stockOutDetailService.updateById(detail);
	}

	// ==================== 审核驳回/取消 ====================
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void rejectStockOut(StockOut stockOut) {
		//StockOut stockOut = this.getById(stockOutId);
		if (stockOut == null) {
			throw new RuntimeException("出库单不存在");
		}

		// 1. 释放库存锁定
		List<StockOutDetail> details = stockOutDetailService.selectByMainId(stockOut.getId());
		for (StockOutDetail detail : details) {
			if (detail.getStockId() != null && detail.getActualQty() != null) {
				stockService.releaseLockQty(detail.getStockId(), detail.getActualQty());
			}
		}

		// 2. 回滚物料需求表
		for (StockOutDetail detail : details) {
			unlockMaterial(detail);
		}

		// 3. 更新出库单状态
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		stockOut.setApproveId(user.getId());
		stockOut.setApproveName(user.getRealname());
		stockOut.setApproveTime(new DateTime());
		stockOut.setApproveStatus(ApproveStatusEnum.REJECT.getCode());
		stockOut.setApproveRemark(stockOut.getApproveRemark());
		stockOut.setStatus(StockEnum.StockOutStatus.CANCEL.getCode());
		this.updateById(stockOut);
	}

	private void unlockMaterial(StockOutDetail detail) {
		String reqId = detail.getRequirementId();
		BigDecimal applyQty = detail.getApplyQty();

		if (StringUtils.isBlank(reqId) || applyQty == null) {
			return;
		}

		ProductionMaterial material = productionMaterialService.getById(reqId);
		if (material == null) {
			return;
		}

		BigDecimal locked = material.getLockedQty() == null ? BigDecimal.ZERO : material.getLockedQty();
		BigDecimal overApply = material.getOverQty() == null ? BigDecimal.ZERO : material.getOverQty();
		BigDecimal issued = material.getIssuedQty() == null ? BigDecimal.ZERO : material.getIssuedQty();
		BigDecimal required = material.getRequiredQty() == null ? BigDecimal.ZERO : material.getRequiredQty();

		// 优先回滚超量
		//BigDecimal rollbackOver = applyQty.min(overApply);
		//BigDecimal rollbackLock = applyQty.subtract(rollbackOver).min(locked);
		BigDecimal detailOverQty = detail.getOverQty() == null ? BigDecimal.ZERO : detail.getOverQty();
		BigDecimal detailNormalLock = applyQty.subtract(detailOverQty);  // 本次正常锁定

		// 优先回滚超量，再回滚正常锁定
		BigDecimal rollbackOver = detailOverQty.min(overApply);
		BigDecimal rollbackLock = detailNormalLock.min(locked);

		BigDecimal newLocked = locked.subtract(rollbackLock);
		BigDecimal newOverApply = overApply.subtract(rollbackOver);

		String newStatus;
		if (issued.compareTo(required) >= 0) {
			newStatus = "2";
		} else if (issued.add(newLocked).compareTo(BigDecimal.ZERO) > 0) { //.add(newOverApply)
			newStatus = "1";
		} else {
			newStatus = "0";
		}
		material.setLockedQty(newLocked);
		material.setOverQty(newOverApply);
		material.setStatus(newStatus);
		productionMaterialService.updateById(material);
//		String updateBy = getCurrentUsername();
//		productionMaterialService.unlockQty(reqId, rollbackLock, rollbackOver, newStatus, updateBy);

		log.info("回滚物料锁定: reqId={}, unlock={}, unOver={}, status={}",
				reqId, rollbackLock, rollbackOver, newStatus);
	}

	// ==================== 删除出库单 ====================
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteStockOut(String stockOutId) {
		StockOut stockOut = this.getById(stockOutId);
		if (stockOut == null) {
			return;
		}

		// 申请状态才需要释放
		if ("APPLY".equals(stockOut.getStatus())) {
			List<StockOutDetail> details = stockOutDetailService.selectByMainId(stockOutId);
			for (StockOutDetail detail : details) {
				if (detail.getStockId() != null && detail.getActualQty() != null) {
					stockService.releaseLockQty(detail.getStockId(), detail.getActualQty());
				}
				unlockMaterial(detail);
			}
		}

		// 删除主表和明细
		this.delMain(stockOutId);
	}

}
