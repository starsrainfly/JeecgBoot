package org.jeecg.modules.wms.service.impl;

import cn.hutool.core.date.DateTime;
import com.jeecg.weibo.exception.BusinessException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.util.CollectionUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.common.enums.ApproveStatusEnum;
import org.jeecg.modules.common.enums.StockEnum;
import org.jeecg.modules.mes.entity.ProductionBatch;
import org.jeecg.modules.mes.mapper.ProductionBatchMapper;
import org.jeecg.modules.wms.entity.Stock;
import org.jeecg.modules.wms.entity.StockIn;
import org.jeecg.modules.wms.entity.StockInDetail;
import org.jeecg.modules.wms.entity.WarehouseArea;
import org.jeecg.modules.wms.mapper.StockInDetailMapper;
import org.jeecg.modules.wms.mapper.StockInMapper;
import org.jeecg.modules.wms.mapper.StockOutDetailMapper;
import org.jeecg.modules.wms.mapper.StockOutMapper;
import org.jeecg.modules.wms.service.*;
import org.jeecg.modules.wms.vo.StockInPage;
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
 * @Description: 入库表
 * @Author: jeecg-boot
 * @Date:   2026-04-03
 * @Version: V1.0
 */
@Log4j2
@Service
public class StockInServiceImpl extends ServiceImpl<StockInMapper, StockIn> implements IStockInService {

	@Autowired
	private StockInMapper stockInMapper;
	@Autowired
	private StockInDetailMapper stockInDetailMapper;
	@Autowired
	private IStockInDetailService stockInDetailService;
	@Autowired
	private IWarehouseAreaService warehouseAreaService;
	@Autowired
	private IStockService stockService;
	@Autowired
	private ProductionBatchMapper productionBatchMapper;
	@Autowired
	private StockOutDetailMapper stockOutDetailMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(StockIn stockIn, List<StockInDetail> stockInDetailList) {
		if(stockInDetailList != null && stockInDetailList.size() > 0){
			// 汇总主表金额
			BigDecimal totalAmount = stockInDetailList.stream()
					.map(d -> d.getTotalAmount() != null ? d.getTotalAmount() : BigDecimal.ZERO)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			stockIn.setTotalAmount(totalAmount);
		}

		stockInMapper.insert(stockIn);
		if(stockInDetailList!=null && stockInDetailList.size()>0) {
			for(StockInDetail entity:stockInDetailList) {
				//外键设置 及入库单号
				entity.setStockInNo(stockIn.getStockInNo());
				entity.setStockInId(stockIn.getId());
				stockInDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(StockIn stockIn,List<StockInDetail> stockInDetailList) {
		if(stockInDetailList != null && stockInDetailList.size() > 0){
			BigDecimal totalAmount = stockInDetailList.stream()
					.map(d -> d.getTotalAmount() != null ? d.getTotalAmount() : BigDecimal.ZERO)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			stockIn.setTotalAmount(totalAmount);
		}
		stockInMapper.updateById(stockIn);
		
		//1.先删除子表数据
		stockInDetailMapper.deleteByMainId(stockIn.getId());
		
		//2.子表数据重新插入
		if(stockInDetailList!=null && stockInDetailList.size()>0) {
			for(StockInDetail entity:stockInDetailList) {
				//外键设置
				entity.setStockInId(stockIn.getId());
				stockInDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		stockInDetailMapper.deleteByMainId(id);
		stockInMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			stockInDetailMapper.deleteByMainId(id.toString());
			stockInMapper.deleteById(id);
		}
	}

	/**
	 * 审核入库单
	 * @param stockInPage
	 * @param loginUser
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void approveStockIn(StockInPage stockInPage, LoginUser loginUser) {
		// 如果审核时重新加载明细，可再算一次金额
//		List<StockInDetail> details = stockInDetailMapper.selectByMainId(stockInPage.getId());
//		BigDecimal totalAmount = details.stream()
//				.map(d -> d.getTotalAmount() != null ? d.getTotalAmount() : BigDecimal.ZERO)
//				.reduce(BigDecimal.ZERO, BigDecimal::add);


		StockIn stockIn = new StockIn();
		BeanUtils.copyProperties(stockInPage, stockIn);
		//stockIn.setTotalAmount(totalAmount); //重新计算总金额
		stockIn.setApproveId(loginUser.getId());  // 记录实际执行人
		stockIn.setApproveName(loginUser.getRealname());
		stockIn.setApproveTime(new DateTime());
		stockIn.setStockInTime(new DateTime());
		if(stockIn.getApproveStatus().equals(ApproveStatusEnum.PASS.getCode())) {
			stockIn.setStatus(StockEnum.StockInStatus.FINISHED.getCode());
			//更新主表及明细表（主要明细表是要删除再重建）
			updateMain(stockIn, stockInPage.getStockInDetailList());
			//重新得到入库明细
			List<StockInDetail> stockInDetailList = stockInDetailService.selectByMainId(stockIn.getId());

			//生产入库且是产品 --> 自动计算成本  + 更新批次状态（合并在一个方法中）
			if ("PRODUCTION".equals(stockIn.getStockInType()) && "1".equals(stockIn.getIsProduct())) {
				calcProductionCost(stockIn, stockInDetailList);
			}

			//添加库存记录功能
			List<Stock> stockList = generateStockRecords(stockIn,stockInDetailList, loginUser);
			for(Stock stock:stockList) {
				stockService.save(stock);
			}

		}
		else if(stockIn.getApproveStatus().equals(ApproveStatusEnum.REJECT.getCode())) {
			//stockIn.setStatus(StockEnum.StockInStatus.APPLY.getCode());
			updateMain(stockIn, stockInPage.getStockInDetailList());
		}
	}

	/**
	 * 明细转入库记录插入
	 * @param stockIn
	 * @param stockInDetailList
	 */
	public List<Stock> generateStockRecords(StockIn stockIn, List<StockInDetail> stockInDetailList, LoginUser loginUser) {
		List<Stock> stockList = new ArrayList<Stock>();
		if(CollectionUtils.isEmpty(stockInDetailList)) {
			return null;
		}

		for(StockInDetail stockInDetail:stockInDetailList) {
			Stock stock = new Stock();
			stock.setStockInTime(new DateTime());
			stock.setBatchNo(stockInDetail.getBatchNo());
			stock.setGoodsCode(stockInDetail.getGoodsCode());
			stock.setGoodsName(stockInDetail.getGoodsName());
			stock.setGoodsId(stockInDetail.getGoodsId());
			stock.setGoodsSpec(stockInDetail.getGoodsSpec());
			stock.setGoodsColor(stockInDetail.getGoodsColor());
			stock.setGoodsType(stockInDetail.getGoodsType());
			stock.setProductionBatchId(stockInDetail.getProductionBatchId());
			stock.setProductionDate(stockInDetail.getProductionDate());
			stock.setShelfLife(stockInDetail.getShelfLife());
			stock.setExpiryDate(stockInDetail.getExpiryDate());
			stock.setIsProduct(stockIn.getIsProduct());
			stock.setSysOrgCode(loginUser.getOrgCode());
			stock.setCreateBy(loginUser.getRealname());
			stock.setCreateTime(new DateTime());
			stock.setCoaFile(stockInDetail.getCoaFile());
			//stock.setAreaId("STAGING"); //默认的暂存区域
			WarehouseArea area = warehouseAreaService.getAreaByCode(stockIn.getWarehouseId(),"STAGING");
			if(area != null) {
				stock.setAreaId(area.getId());
			}
			stock.setWarehouseId(stockIn.getWarehouseId());
			stock.setInDetailId(stockInDetail.getId());
			stock.setOriginalQty(stockInDetail.getActualQty());
			stock.setQuantity(stockInDetail.getActualQty());
			stock.setUnit(stockInDetail.getUnit());
			stock.setSupplierId(stockIn.getSupplierId());
			stock.setSupplierName(stockIn.getSupplierName());
			stock.setQcStatus(stockInDetail.getQcStatus());
			stock.setCostPrice(stockInDetail.getUnitPrice());
			stock.setCostTotal(stockInDetail.getTotalAmount());

			stockList.add(stock);
		}
		return stockList;
	}

	/**
	 * 计算生产入库成本
	 */
	/**
	 * 计算生产入库成本 + 更新批次入库状态
	 */
	private void calcProductionCost(StockIn stockIn, List<StockInDetail> detailList) {
		BigDecimal totalAmount = BigDecimal.ZERO;

		for (StockInDetail detail : detailList) {
			String batchId = detail.getProductionBatchId();
			if (StringUtils.isBlank(batchId)) {
				log.warn("生产入库明细缺少批次ID: detailId={}", detail.getId());
				continue;
			}

			// 1. 获取批次信息
			ProductionBatch batch = productionBatchMapper.selectById(batchId);
			if (batch == null) {
				throw new JeecgBootException("生产批次不存在: " + batchId);
			}

			// 2. 校验：必须有实际产量
			BigDecimal actualQty = batch.getActualQty();
			if (actualQty == null || actualQty.compareTo(BigDecimal.ZERO) <= 0) {
				throw new JeecgBootException(
						"批次【" + batch.getBatchNo() + "】尚未报工或实际产量为0，请先完成生产报工"
				);
			}

			// 3. 校验：入库数量不能超过实际产量
			BigDecimal detailActualQty = detail.getActualQty();
			if (detailActualQty == null) {
				detailActualQty = detail.getApplyQty(); // 如果实收为空，用申请数量
			}
			if (detailActualQty.compareTo(actualQty) > 0) {
				throw new JeecgBootException(
						"入库数量【" + detailActualQty + "】不能超过实际产量【" + actualQty + "】"
				);
			}

			// 4. 汇总该批次已审核的领料成本
			BigDecimal materialCost = stockOutDetailMapper.sumMaterialCostByBatchId(batchId);
			if (materialCost == null || materialCost.compareTo(BigDecimal.ZERO) == 0) {
				log.warn("批次【{}】没有已审核的领料记录，成本计为0", batch.getBatchNo());
				materialCost = BigDecimal.ZERO;
			}

			// 5. 计算成本单价 = 领料总成本 ÷ 实际产量
			BigDecimal costPrice = materialCost.divide(actualQty, 4, RoundingMode.HALF_UP);

			// 6. 本次入库成本金额
			BigDecimal costTotal = costPrice.multiply(detailActualQty).setScale(2, RoundingMode.HALF_UP);

			detail.setUnitPrice(costPrice);
			detail.setTotalAmount(costTotal);
			stockInDetailMapper.updateById(detail);

			totalAmount = totalAmount.add(costTotal);

			// ========== 7. 更新批次入库状态（合并到这里）==========
			updateBatchInStockStatus(batch, detailActualQty, actualQty);
		}

		// 回写主表成本总额
		stockIn.setTotalAmount(totalAmount);
		stockInMapper.updateById(stockIn);
	}

	/**
	 * 更新批次入库状态
	 */
	/**
	 * 更新批次入库状态
	 * @param batch 生产批次
	 * @param inQty 本次入库数量
	 * @param actualQty 批次实际产量（用于校验和计算剩余量）
	 */
	private void updateBatchInStockStatus(ProductionBatch batch, BigDecimal inQty, BigDecimal actualQty) {
		// 已入库数量增加
		BigDecimal newInStockQty = (batch.getInStockQty() != null ? batch.getInStockQty() : BigDecimal.ZERO)
				.add(inQty);

		// 剩余可入库量 = 实际产量 - 已入库量（不是 plannedQty！）
		BigDecimal remainQty = actualQty.subtract(newInStockQty);

		// 校验：剩余量不能为负
		if (remainQty.compareTo(BigDecimal.ZERO) < 0) {
			throw new JeecgBootException(
					String.format("批次【%s】入库后剩余量不能为负，已入库[%s]，本次[%s]，实际产量[%s]",
							batch.getBatchNo(), batch.getInStockQty(), inQty, actualQty)
			);
		}

		batch.setInStockQty(newInStockQty);
		batch.setRemainQty(remainQty);

		// 入库状态：0未入库 1部分入库 2已入库
		String inStockStatus;
		if (newInStockQty.compareTo(actualQty) >= 0 || remainQty.compareTo(BigDecimal.ZERO) == 0) {
			inStockStatus = "2"; // 已入库完毕
		} else if (newInStockQty.compareTo(BigDecimal.ZERO) > 0) {
			inStockStatus = "1"; // 部分入库
		} else {
			inStockStatus = "0"; // 未入库
		}
		batch.setInStockStatus(inStockStatus);

		// 可选：如果入库完毕，同时更新批次状态为已完成
		if ("2".equals(inStockStatus)) {
			// batch.setStatus("COMPLETED"); // 如果批次有独立的状态字段
		}

		productionBatchMapper.updateById(batch);

		log.info("批次入库状态更新成功，batchNo={}, inStockQty={}, remainQty={}, status={}",
				batch.getBatchNo(), newInStockQty, remainQty, inStockStatus);
	}

	/**
	 * 更新生产批次的已入库数量和剩余可入库数量
	 */
	private void updateBatchInStockQty(List<StockInDetail> stockInDetailList) {
		if (stockInDetailList == null || stockInDetailList.isEmpty()) {
			return;
		}

		for (StockInDetail detail : stockInDetailList) {
			// 只处理有生产批次ID的明细
			if (detail.getProductionBatchId() == null) {
				continue;
			}

			ProductionBatch batch = productionBatchMapper.selectById(detail.getProductionBatchId());
			if (batch == null) {
				log.warn("生产批次不存在，batchId={}", detail.getProductionBatchId());
				continue;
			}

			BigDecimal actualQty = detail.getActualQty();
			if (actualQty == null || actualQty.compareTo(BigDecimal.ZERO) <= 0) {
				log.warn("入库实收数量无效，detailId={}, actualQty={}", detail.getId(), actualQty);
				continue;
			}

			// 已入库数量增加
			BigDecimal newInstockQty = (batch.getInStockQty() != null ? batch.getInStockQty() : BigDecimal.ZERO)
					.add(actualQty);
			batch.setInStockQty(newInstockQty);

			// 剩余可入库数量减少
			BigDecimal newRemainQty = (batch.getRemainQty() != null ? batch.getRemainQty() : BigDecimal.ZERO)
					.subtract(actualQty);
			batch.setRemainQty(newRemainQty);

			// 校验：剩余量不能为负
			if (newRemainQty.compareTo(BigDecimal.ZERO) < 0) {
				throw new BusinessException(
						String.format("物料[%s]入库数量[%s]超过批次剩余可入库量，批次号：%s",
								detail.getGoodsName(), actualQty, batch.getBatchNo()));
			}

			productionBatchMapper.updateById(batch);

			log.info("批次入库数量更新成功，batchNo={}, instockQty={}, remainQty={}",
					batch.getBatchNo(), newInstockQty, newRemainQty);
		}
	}

}