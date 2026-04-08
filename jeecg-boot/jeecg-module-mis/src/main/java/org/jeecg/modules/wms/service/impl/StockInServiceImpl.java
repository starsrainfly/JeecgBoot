package org.jeecg.modules.wms.service.impl;

import cn.hutool.core.date.DateTime;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.util.CollectionUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.common.enums.ApproveStatusEnum;
import org.jeecg.modules.common.enums.StockEnum;
import org.jeecg.modules.wms.entity.Stock;
import org.jeecg.modules.wms.entity.StockIn;
import org.jeecg.modules.wms.entity.StockInDetail;
import org.jeecg.modules.wms.entity.WarehouseArea;
import org.jeecg.modules.wms.mapper.StockInDetailMapper;
import org.jeecg.modules.wms.mapper.StockInMapper;
import org.jeecg.modules.wms.service.*;
import org.jeecg.modules.wms.vo.StockInPage;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
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
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(StockIn stockIn, List<StockInDetail> stockInDetailList) {
		stockInMapper.insert(stockIn);
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
	public void updateMain(StockIn stockIn,List<StockInDetail> stockInDetailList) {
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
		StockIn stockIn = new StockIn();
		BeanUtils.copyProperties(stockInPage, stockIn);

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
			stock.setGoodsType(stockInDetail.getGoodsType());
			stock.setProductionBatchId(stockInDetail.getProductionBatchId());
			stock.setProductionDate(stockInDetail.getProductionDate());
			stock.setShelfLife(stockInDetail.getShelfLife());
			stock.setExpiryDate(stockInDetail.getExpiryDate());
			stock.setSysOrgCode(loginUser.getOrgCode());
			stock.setCreateBy(loginUser.getRealname());
			stock.setCreateTime(new DateTime());
			//stock.setAreaId("STAGING"); //默认的暂存区域
			WarehouseArea area = warehouseAreaService.getAreaByCode("STAGING");
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



			stockList.add(stock);
		}
		return stockList;
	}

}