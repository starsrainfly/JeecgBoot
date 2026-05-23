package org.jeecg.modules.wms.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.common.enums.ApproveStatusEnum;
import org.jeecg.modules.common.enums.SerialNoPrefixEnum;
import org.jeecg.modules.common.enums.StockEnum;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.wms.entity.*;
import org.jeecg.modules.wms.mapper.InventoryCheckDetailMapper;
import org.jeecg.modules.wms.mapper.InventoryCheckMapper;
import org.jeecg.modules.wms.mapper.StockMapper;
import org.jeecg.modules.wms.service.*;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @Description: 盘库主表
 * @Author: jeecg-boot
 * @Date:   2026-05-19
 * @Version: V1.0
 */
@Service
public class InventoryCheckServiceImpl extends ServiceImpl<InventoryCheckMapper, InventoryCheck> implements IInventoryCheckService {

	@Autowired
	private InventoryCheckMapper inventoryCheckMapper;
	@Autowired
	private InventoryCheckDetailMapper inventoryCheckDetailMapper;

	@Autowired
	private StockMapper stockMapper;
	@Autowired
	private IStockService stockService;

	@Autowired
	private IInventoryCheckDetailService inventoryCheckDetailService;
	@Autowired
	private IInventoryAdjustService	inventoryAdjustService;
	@Autowired
	private IInventoryAdjustDetailService inventoryAdjustDetailService;
	@Autowired
	private IStockInService stockInService;
	@Autowired
	private IStockOutService stockOutService;
	@Autowired
	private ISerialNoService serialNoService;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		inventoryCheckDetailMapper.deleteByMainId(id);
		inventoryCheckMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			inventoryCheckDetailMapper.deleteByMainId(id.toString());
			inventoryCheckMapper.deleteById(id);
		}
	}

	@Override
	public List<InventoryCheckDetail> previewStock(String checkId) {
		InventoryCheck check = this.getById(checkId);
		return generateDetails(check);
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void startCheck(String id) {
		InventoryCheck check = getById(id);
		if (check == null) {
			throw new JeecgBootException("盘点单不存在");
		}
		if (!"0".equals(check.getCheckStatus())) {
			throw new JeecgBootException("只有待盘点状态才能开始");
		}

		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		// 更新主表
		check.setCheckStatus("1"); // 盘点中
		if (StrUtil.isBlank(check.getCheckUserId())) {
			check.setCheckUserId(user.getId());
			check.setCheckUserName(user.getRealname());
		}
		check.setCheckStartTime(new Date());
		updateById(check);

		// 生成盘点明细（如果还没有）
		List<InventoryCheckDetail> existDetails = inventoryCheckDetailMapper.selectByMainId(id);
		if (CollUtil.isEmpty(existDetails)) {
			List<InventoryCheckDetail> details = generateDetails(check);
			if (CollUtil.isNotEmpty(details)) {

				inventoryCheckDetailService.saveBatch(details);
				// 4. 更新主表总项数
				this.lambdaUpdate()
						.set(InventoryCheck::getTotalItems, details.size())
						.eq(InventoryCheck::getId, id)
						.update();
			}
		}
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void approveCheck(InventoryCheck	inventoryCheck) {
		InventoryCheck check = getById(inventoryCheck.getId());
		if (check == null) {
			throw new JeecgBootException("盘点单不存在");
		}
		if (!"2".equals(check.getCheckStatus())) {
			throw new JeecgBootException("只有已完成状态才能审核");
		}

		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		check.setApproveStatus(inventoryCheck.getApproveStatus());
		check.setApproveId(user.getId());
		check.setApproveUser(user.getRealname());
		check.setApproveTime(new Date());
		check.setApproveRemark(inventoryCheck.getApproveRemark());

		if ("2".equals(inventoryCheck.getApproveStatus())) {
			// 审核不通过，退回盘点中
			check.setCheckStatus("1");
			updateById(check);
			return;
		}
		//审核通过处理 盘盈与盘亏

		List<InventoryCheckDetail> details = inventoryCheckDetailMapper.selectByMainId(inventoryCheck.getId());
		//分离 盘盈与盘亏
		List<InventoryCheckDetail> profitList = new ArrayList<>();
		List<InventoryCheckDetail> lossList = new ArrayList<>();
		for (InventoryCheckDetail detail : details) {
			if (detail.getDiffQty() == null || detail.getDiffQty().compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}
			if (detail.getDiffQty().compareTo(BigDecimal.ZERO) > 0) {
				profitList.add(detail);
			} else {
				lossList.add(detail);
			}
		}
		//预 检验 盘亏库存是否充足
		for (InventoryCheckDetail detail : lossList) {
			Stock stock = stockService.getById(detail.getStockId());
			if (stock == null) {
				throw new JeecgBootException("库存记录不存在：" + detail.getGoodsCode());
			}

			BigDecimal needQty = detail.getDiffQty().abs();
			if (stock.getQuantity().compareTo(needQty) < 0) {
				throw new JeecgBootException(
						String.format("物料[%s]库存不足，可用[%s]，需扣减[%s]",
								detail.getGoodsCode(), stock.getQuantity(), needQty)
				);
			}
		}

		// 4. 生成调整主表
		String adjustNo = serialNoService.generateSerialNo(
				SerialNoPrefixEnum.INVENTORY_ADJUST.getPrefix()
		);

		BigDecimal totalDiffQty = BigDecimal.ZERO;
		BigDecimal totalDiffAmount = BigDecimal.ZERO;

		for (InventoryCheckDetail d : details) {
			if (d.getDiffQty() != null) {
				totalDiffQty = totalDiffQty.add(d.getDiffQty().abs());
			}
			if (d.getDiffAmount() != null) {
				totalDiffAmount = totalDiffAmount.add(d.getDiffAmount().abs());
			}
		}

		// 差异原因汇总
		String reasonSummary = details.stream()
				.filter(d -> StringUtils.isNotBlank(d.getDiffReason()))
				.map(InventoryCheckDetail::getDiffReason)
				.distinct()
				.collect(Collectors.joining(" | "));

		InventoryAdjust adjust = new InventoryAdjust();
		adjust.setAdjustNo(adjustNo);
		adjust.setCheckId(inventoryCheck.getId());
		adjust.setCheckNo(check.getCheckNo());
		adjust.setWarehouseId(check.getWarehouseId());
		adjust.setTotalItems(profitList.size() + lossList.size());
		adjust.setTotalDiffQty(totalDiffQty);
		adjust.setTotalDiffAmount(totalDiffAmount);
		adjust.setReasonSummary(reasonSummary);
		adjust.setApproveUserId(user.getId());
		adjust.setApproveUserName(user.getRealname());
		adjust.setApproveTime(new Date());
		adjust.setApproveRemark(inventoryCheck.getApproveRemark());
		adjust.setApproveStatus("1"); // 通过
		inventoryAdjustService.save(adjust);

		// 5. 处理盘盈：生成入库单
		if (!profitList.isEmpty()) {
			String stockInNo = serialNoService.generateSerialNo(
					SerialNoPrefixEnum.STOCK_IN.getPrefix()
			);
			StockIn stockIn = createProfitStockIn(check, stockInNo, adjustNo, user);
			List<StockInDetail> inDetails = new ArrayList<>();

			for (InventoryCheckDetail detail : profitList) {
				BigDecimal profitQty = detail.getDiffQty().abs();

				// 增加库存
				stockService.increaseQty(detail.getStockId(), profitQty);

				// 入库明细
				StockInDetail inDetail = new StockInDetail();
				inDetail.setStockInId(stockIn.getId());
				inDetail.setStockInNo(stockIn.getStockInNo());
				inDetail.setGoodsType(detail.getGoodsType());
				inDetail.setGoodsId(detail.getGoodsId());
				inDetail.setGoodsCode(detail.getGoodsCode());
				inDetail.setGoodsName(detail.getGoodsName());
				inDetail.setGoodsSpec(detail.getGoodsSpec());
				inDetail.setGoodsColor(detail.getGoodsColor());
				inDetail.setUnit(detail.getUnit());
				inDetail.setActualQty(profitQty);
				inDetail.setApplyQty(profitQty);
				inDetail.setUnitPrice(detail.getCostPrice());
				inDetail.setTotalAmount(detail.getDiffAmount().abs());
				inDetail.setBatchNo(detail.getBatchNo());
				inDetail.setProductionDate(detail.getProductionDate());
				inDetail.setExpiryDate(detail.getExpiryDate());
				inDetail.setShelfLife(detail.getShelfLife());//添加质保期
				inDetails.add(inDetail);

				// 调整明细
				createAdjustDetail(adjust, detail, profitQty,
						stockIn.getId(), stockIn.getStockInNo(), null, null);
			}

			stockInService.saveMain(stockIn, inDetails);
		}

		// 6. 处理盘亏：生成出库单
		if (!lossList.isEmpty()) {
			String stockOutNo = serialNoService.generateSerialNo(
					SerialNoPrefixEnum.STOCK_OUT.getPrefix()
			);
			StockOut stockOut = createLossStockOut(check, stockOutNo, adjustNo, user);
			List<StockOutDetail> outDetails = new ArrayList<>();

			for (InventoryCheckDetail detail : lossList) {
				BigDecimal lossQty = detail.getDiffQty().abs();

				// 扣减库存
				stockService.decreaseQty(detail.getStockId(), lossQty);

				// 出库明细
				StockOutDetail outDetail = new StockOutDetail();
				outDetail.setStockOutId(stockOut.getId());
				outDetail.setStockOutNo(stockOut.getStockOutNo());
				outDetail.setGoodsType(detail.getGoodsType());
				outDetail.setGoodsCode(detail.getGoodsCode());
				outDetail.setGoodsName(detail.getGoodsName());
				outDetail.setGoodsSpec(detail.getGoodsSpec());
				outDetail.setGoodsColor(detail.getGoodsColor());
				outDetail.setUnit(detail.getUnit());
				outDetail.setActualQty(lossQty);
				outDetail.setApplyQty(lossQty);
				outDetail.setCostPrice(detail.getCostPrice());
				outDetail.setCostTotal(detail.getDiffAmount().abs());
				outDetail.setBatchNo(detail.getBatchNo());
				outDetail.setProductionDate(detail.getProductionDate());
				outDetail.setExpiryDate(detail.getExpiryDate());
				outDetails.add(outDetail);

				// 调整明细
				createAdjustDetail(adjust, detail, lossQty,
						null, null, stockOut.getId(), stockOut.getStockOutNo());
			}

			stockOutService.saveMain(stockOut, outDetails);
		}

		// 7. 更新盘点主表

		check.setApproveUser(user.getRealname());
		check.setApproveTime(new Date());

		//更新盘点单审核状态
		updateById(check);

	}

	/**
	 * 根据盘点范围生成明细
	 */
	private List<InventoryCheckDetail> generateDetails(InventoryCheck check) {
		List<InventoryCheckDetail> details = new ArrayList<>();

		// 查询库存
		LambdaQueryWrapper<Stock> query = new LambdaQueryWrapper<>();

		// 按范围过滤
		if ("1".equals(check.getCheckScope())) {
			// 按库位
			if (StrUtil.isNotBlank(check.getWarehouseId())) {
				query.eq(Stock::getWarehouseId, check.getWarehouseId());
			}
			if (StrUtil.isNotBlank(check.getAreaId())) {
				query.eq(Stock::getAreaId, check.getAreaId());
			}
			if (StrUtil.isNotBlank(check.getShelfId())) {
				query.eq(Stock::getShelfId, check.getShelfId());
			}
			if (StrUtil.isNotBlank(check.getLocationId())) {
				query.eq(Stock::getLocationId, check.getLocationId());
			}
		} else if ("2".equals(check.getCheckScope())) {
			// 按产品
			query.eq(Stock::getGoodsId, check.getGoodsId());
		} else if ("3".equals(check.getCheckScope())) {
			// 按物料
			if (StrUtil.isNotBlank(check.getGoodsId())) {
				query.eq(Stock::getGoodsId, check.getGoodsId());
			}
//			if (StrUtil.isNotBlank(check.getGoodsType())) {
//				query.eq(Stock::getGoodsType, check.getGoodsType());
//			}
		} else if ("4".equals(check.getCheckScope())) {
			// 全仓
			if (StrUtil.isNotBlank(check.getWarehouseId())) {
				query.eq(Stock::getWarehouseId, check.getWarehouseId());
			}
		}

		query.eq(Stock::getDelFlag, "0");
		query.gt(Stock::getQuantity, 0); // 只盘有库存的

		List<Stock> stocks = stockService.list(query);

		for (Stock stock : stocks) {
			InventoryCheckDetail detail = new InventoryCheckDetail();

			detail.setCheckId(check.getId());
			detail.setStockId(stock.getId());
			detail.setGoodsId(stock.getGoodsId());
			detail.setGoodsCode(stock.getGoodsCode());
			detail.setGoodsName(stock.getGoodsName());
			detail.setGoodsSpec(stock.getGoodsSpec());
			detail.setGoodsColor(stock.getGoodsColor());
			detail.setGoodsType(stock.getGoodsType());
			detail.setBatchNo(stock.getBatchNo());
			detail.setProductionDate(stock.getProductionDate());
			detail.setExpiryDate(stock.getExpiryDate());
			detail.setShelfLife(stock.getShelfLife());
			detail.setSupplierId(stock.getSupplierId());
			detail.setSupplierName(stock.getSupplierName());
			detail.setWarehouseId(stock.getWarehouseId());
			detail.setAreaId(stock.getAreaId());
			detail.setShelfId(stock.getShelfId());
			detail.setLocationId(stock.getLocationId());
			detail.setSystemQty(stock.getQuantity());

//			detail.setActualQty(BigDecimal.ZERO);
//			detail.setDiffQty(BigDecimal.ZERO);
//			detail.setDiffAmount(BigDecimal.ZERO);
			detail.setActualQty(null);   // 未盘点
			detail.setDiffQty(null);     // 未计算
			detail.setDiffAmount(null);  // 未计算
			detail.setUnit(stock.getUnit());
			detail.setCostPrice(stock.getCostPrice());

			detail.setCheckStatus("0"); // 待盘点
			detail.setDelFlag("0");

			details.add(detail);
		}

		return details;
	}
	// ========== 辅助方法 ==========

	private StockIn createProfitStockIn(InventoryCheck check, String stockInNo,
										String adjustNo, LoginUser user) {
		StockIn stockIn = new StockIn();
		stockIn.setStockInNo(stockInNo);
		stockIn.setStockInType("ADJUST_PROFIT");
		stockIn.setWarehouseId(check.getWarehouseId());
		stockIn.setStatus(StockEnum.StockInStatus.FINISHED.getCode());
		stockIn.setApproveStatus(ApproveStatusEnum.PASS.getCode());
		stockIn.setOperatorUserId(user.getId());
		stockIn.setOperatorName(user.getRealname());
		stockIn.setStockInTime(new Date());
		stockIn.setRemark("盘点盘盈生成，关联调整单：" + adjustNo);
		return stockIn;
	}

	private StockOut createLossStockOut(InventoryCheck check, String stockOutNo,
										String adjustNo, LoginUser user) {
		StockOut stockOut = new StockOut();
		stockOut.setStockOutNo(stockOutNo);
		stockOut.setStockOutType("ADJUST_LOSS");
		stockOut.setWarehouseId(check.getWarehouseId());
		stockOut.setStatus(StockEnum.StockOutStatus.FINISHED.getCode());
		stockOut.setApproveStatus(ApproveStatusEnum.PASS.getCode());
		stockOut.setOperatorUserId(user.getId());
		stockOut.setOperatorName(user.getRealname());
		stockOut.setStockOutTime(new Date());
		stockOut.setRemark("盘点盘亏生成，关联调整单：" + adjustNo);
		return stockOut;
	}

	private void createAdjustDetail(InventoryAdjust adjust, InventoryCheckDetail detail,
									BigDecimal adjustQty,
									String stockInId, String stockInNo,
									String stockOutId, String stockOutNo) {
		InventoryAdjustDetail adjustDetail = new InventoryAdjustDetail();
		adjustDetail.setAdjustId(adjust.getId());
		adjustDetail.setCheckDetailId(detail.getId());
		adjustDetail.setStockId(detail.getStockId());
		adjustDetail.setGoodsId(detail.getGoodsId());
		adjustDetail.setGoodsCode(detail.getGoodsCode());
		adjustDetail.setGoodsName(detail.getGoodsName());
		adjustDetail.setGoodsSpec(detail.getGoodsSpec());
		adjustDetail.setGoodsColor(detail.getGoodsColor());
		adjustDetail.setBatchNo(detail.getBatchNo());
		adjustDetail.setWarehouseId(detail.getWarehouseId());
		adjustDetail.setAreaId(detail.getAreaId());
		adjustDetail.setShelfId(detail.getShelfId());
		adjustDetail.setLocationId(detail.getLocationId());
		// path_code 从库存记录或盘点明细获取

		adjustDetail.setAdjustType(detail.getDiffQty().compareTo(BigDecimal.ZERO) > 0 ? "1" : "2");
		adjustDetail.setAdjustQty(adjustQty);
		adjustDetail.setUnit(detail.getUnit());
		adjustDetail.setCostPrice(detail.getCostPrice());
		adjustDetail.setAdjustAmount(detail.getDiffAmount().abs());

		// 调整前库存 = 系统库存，调整后 = 实盘数量
		adjustDetail.setBeforeQty(detail.getSystemQty());
		//adjustDetail.setAfterQty(detail.getActualQty());
		adjustDetail.setAfterQty(
				detail.getActualQty() != null ? detail.getActualQty() : detail.getSystemQty()
		);
		adjustDetail.setDiffReason(detail.getDiffReason());
		adjustDetail.setStockInId(stockInId);
		adjustDetail.setStockInNo(stockInNo);
		adjustDetail.setStockOutId(stockOutId);
		adjustDetail.setStockOutNo(stockOutNo);
		inventoryAdjustDetailService.save(adjustDetail);
	}

}
