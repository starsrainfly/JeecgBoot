package org.jeecg.modules.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.common.enums.SerialNoPrefixEnum;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.mdm.entity.Product;
import org.jeecg.modules.mdm.entity.Recipe;
import org.jeecg.modules.mdm.entity.RecipeDetail;
import org.jeecg.modules.mdm.mapper.ProductMapper;
import org.jeecg.modules.mdm.mapper.RecipeDetailMapper;
import org.jeecg.modules.mdm.mapper.RecipeMapper;
import org.jeecg.modules.scm.dto.CostCalcSnapshotDto;
import org.jeecg.modules.scm.entity.CostCalc;
import org.jeecg.modules.scm.entity.CostCalcDetail;
import org.jeecg.modules.scm.mapper.CostCalcDetailMapper;
import org.jeecg.modules.scm.mapper.CostCalcMapper;
import org.jeecg.modules.scm.service.ICostCalcService;
import org.jeecg.modules.scm.vo.CostCalcDetailVo;
import org.jeecg.modules.scm.vo.CostCalcMaterialVo;
import org.jeecg.modules.scm.vo.CostCalcProductVo;
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
 * @Description: 成本核算快照
 * @Author: jeecg-boot
 * @Date:   2026-07-28
 * @Version: V1.0
 */
@Service
public class CostCalcServiceImpl extends ServiceImpl<CostCalcMapper, CostCalc> implements ICostCalcService {

	@Autowired
	private CostCalcMapper costCalcMapper;
	@Autowired
	private CostCalcDetailMapper costCalcDetailMapper;

	@Autowired
	private ProductMapper productMapper;
	@Autowired
	private RecipeMapper recipeMapper;
	@Autowired
	private RecipeDetailMapper recipeDetailMapper;
	@Autowired
	private ISerialNoService serialNoService;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(CostCalc costCalc, List<CostCalcDetail> costCalcDetailList) {
		costCalcMapper.insert(costCalc);
		if(costCalcDetailList!=null && costCalcDetailList.size()>0) {
			for(CostCalcDetail entity:costCalcDetailList) {
				//外键设置
				entity.setCalcId(costCalc.getId());
				costCalcDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(CostCalc costCalc,List<CostCalcDetail> costCalcDetailList) {
		costCalcMapper.updateById(costCalc);
		
		//1.先删除子表数据
		costCalcDetailMapper.deleteByMainId(costCalc.getId());
		
		//2.子表数据重新插入
		if(costCalcDetailList!=null && costCalcDetailList.size()>0) {
			for(CostCalcDetail entity:costCalcDetailList) {
				//外键设置
				entity.setCalcId(costCalc.getId());
				costCalcDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		costCalcDetailMapper.deleteByMainId(id);
		costCalcMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			costCalcDetailMapper.deleteByMainId(id.toString());
			costCalcMapper.deleteById(id);
		}
	}

	@Override
	public IPage<CostCalcProductVo> queryProductList(Page<CostCalcProductVo> page, String productCode, String productName) {
		return baseMapper.queryProductList(page, productCode, productName);
	}

	@Override
	public CostCalcDetailVo calculateCost(String productId) {
		Product product = productMapper.selectById(productId);
		if (product == null || "1".equals(product.getDelFlag())) {
			throw new JeecgBootException("产品不存在");
		}
		Recipe recipe = recipeMapper.selectById(product.getRecipeId());
		if (recipe == null || !"1".equals(recipe.getPublishStatus())) {
			throw new JeecgBootException("产品未关联已发布配方，无法核算");
		}
		List<RecipeDetail> details = recipeDetailMapper.selectList(
				new LambdaQueryWrapper<RecipeDetail>()
						.eq(RecipeDetail::getRecipeId, recipe.getId())
						.eq(RecipeDetail::getDelFlag, "0")
						.orderByAsc(RecipeDetail::getSerialNo)
		);

		CostCalcDetailVo vo = new CostCalcDetailVo();
		vo.setProductId(productId);
		vo.setProductCode(product.getProductCode());
		vo.setProductName(product.getProductName());
		vo.setProductSpec(product.getProductSpec());
		vo.setProductColor(product.getProductColor());
		vo.setRecipeId(recipe.getId());
		vo.setRecipeCode(recipe.getRecipeCode());
		vo.setRecipeName(recipe.getRecipeName());
		vo.setRecipeVersion(recipe.getVersion());
		vo.setProportionTotal(recipe.getProportionTotal());
		vo.setProportionType(recipe.getProportionType());

		BigDecimal totalLatest = BigDecimal.ZERO;
		BigDecimal totalAvg = BigDecimal.ZERO;
		boolean hasUnpriced = false;
		List<CostCalcMaterialVo> list = new ArrayList<>();
		BigDecimal pTotal = recipe.getProportionTotal() != null ? recipe.getProportionTotal() : new BigDecimal("100");

		for (RecipeDetail d : details) {
			CostCalcMaterialVo m = new CostCalcMaterialVo();
			m.setMaterialId(d.getMaterialId());
			m.setMaterialCode(d.getMaterialCode());
			m.setMaterialName(d.getMaterialName());
			m.setMaterialSpec(d.getMaterialSpec());
			m.setProportion(d.getProportion());
			m.setUnit(d.getUnit());

			BigDecimal avgPrice = baseMapper.selectStockAvgPrice(d.getMaterialId());
			BigDecimal latestPrice = baseMapper.selectLatestInPrice(d.getMaterialId());
			m.setAvgPrice(avgPrice);
			m.setLatestPrice(latestPrice);

			BigDecimal calcPrice = null;
			String source = "NONE";
			if (avgPrice != null && avgPrice.compareTo(BigDecimal.ZERO) > 0) {
				calcPrice = avgPrice;
				source = "AVG";
			} else if (latestPrice != null && latestPrice.compareTo(BigDecimal.ZERO) > 0) {
				calcPrice = latestPrice;
				source = "LATEST";
			} else {
				hasUnpriced = true;
			}

			m.setPriceSource(source);
			m.setCalcPrice(calcPrice);
			BigDecimal prop = d.getProportion() != null ? d.getProportion() : BigDecimal.ZERO;
			BigDecimal ratio = prop.divide(pTotal, 10, RoundingMode.HALF_UP);

			if (calcPrice != null) {
				BigDecimal amt = ratio.multiply(calcPrice).setScale(6, RoundingMode.HALF_UP);
				m.setAmount(amt);
				if (latestPrice != null) totalLatest = totalLatest.add(ratio.multiply(latestPrice));
				if (avgPrice != null) totalAvg = totalAvg.add(ratio.multiply(avgPrice));
			} else {
				m.setAmount(null);
			}
			list.add(m);
		}

		vo.setMaterialList(list);
		vo.setTotalCostLatest(totalLatest.setScale(6, RoundingMode.HALF_UP));
		vo.setTotalCostAvg(totalAvg.setScale(6, RoundingMode.HALF_UP));
		vo.setHasUnpriced(hasUnpriced);
		return vo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public CostCalc saveSnapshot(CostCalcSnapshotDto dto) {
		CostCalcDetailVo calcVo = calculateCost(dto.getProductId());
		String calcNo = serialNoService.generateSerialNo(SerialNoPrefixEnum.COST_CALC.getPrefix());

		CostCalc calc = new CostCalc();
		calc.setCalcNo(calcNo);
		calc.setCalcType(dto.getCalcType() != null ? dto.getCalcType() : "MANUAL");
		calc.setCalcDate(new Date());
		calc.setProductId(calcVo.getProductId());
		calc.setProductCode(calcVo.getProductCode());
		calc.setProductName(calcVo.getProductName());
		calc.setProductSpec(calcVo.getProductSpec());
		calc.setProductColor(calcVo.getProductColor());
		calc.setRecipeId(calcVo.getRecipeId());
		calc.setRecipeCode(calcVo.getRecipeCode());
		calc.setRecipeName(calcVo.getRecipeName());
		calc.setRecipeVersion(calcVo.getRecipeVersion());
		calc.setProportionTotal(calcVo.getProportionTotal());
		calc.setProportionType(calcVo.getProportionType());
		calc.setTotalCostLatest(calcVo.getTotalCostLatest());
		calc.setTotalCostAvg(calcVo.getTotalCostAvg());
		calc.setRemark(dto.getRemark());
		calc.setDelFlag("0");
		save(calc);

		int seq = 1;
		for (CostCalcMaterialVo m : calcVo.getMaterialList()) {
			CostCalcDetail detail = new CostCalcDetail();
			detail.setCalcId(calc.getId());
			detail.setCalcNo(calcNo);
			detail.setSerialNo(seq++);
			detail.setMaterialId(m.getMaterialId());
			detail.setMaterialCode(m.getMaterialCode());
			detail.setMaterialName(m.getMaterialName());
			detail.setMaterialSpec(m.getMaterialSpec());
			detail.setProportion(m.getProportion());
			detail.setUnit(m.getUnit());
			detail.setPriceSource(m.getPriceSource());
			detail.setLatestPrice(m.getLatestPrice());
			detail.setAvgPrice(m.getAvgPrice());
			detail.setCalcPrice(m.getCalcPrice());
			detail.setAmount(m.getAmount());
			detail.setDelFlag("0");
			costCalcDetailMapper.insert(detail);
		}
		return calc;
	}

	@Override
	public CostCalcDetailVo getSnapshotDetail(String calcId) {
		CostCalc calc = getById(calcId);
		if (calc == null || "1".equals(calc.getDelFlag())) {
			throw new JeecgBootException("快照不存在");
		}
		CostCalcDetailVo vo = new CostCalcDetailVo();
		vo.setProductId(calc.getProductId());
		vo.setProductCode(calc.getProductCode());
		vo.setProductName(calc.getProductName());
		vo.setProductSpec(calc.getProductSpec());
		vo.setProductColor(calc.getProductColor());
		vo.setRecipeId(calc.getRecipeId());
		vo.setRecipeCode(calc.getRecipeCode());
		vo.setRecipeName(calc.getRecipeName());
		vo.setRecipeVersion(calc.getRecipeVersion());
		vo.setProportionTotal(calc.getProportionTotal());
		vo.setProportionType(calc.getProportionType());
		vo.setTotalCostLatest(calc.getTotalCostLatest());
		vo.setTotalCostAvg(calc.getTotalCostAvg());

		List<CostCalcDetail> details = costCalcDetailMapper.selectList(
				new LambdaQueryWrapper<CostCalcDetail>()
						.eq(CostCalcDetail::getCalcId, calcId)
						.eq(CostCalcDetail::getDelFlag, "0")
						.orderByAsc(CostCalcDetail::getSerialNo)
		);
		boolean hasUnpriced = false;
		List<CostCalcMaterialVo> list = new ArrayList<>();
		for (CostCalcDetail d : details) {
			CostCalcMaterialVo m = new CostCalcMaterialVo();
			m.setMaterialId(d.getMaterialId());
			m.setMaterialCode(d.getMaterialCode());
			m.setMaterialName(d.getMaterialName());
			m.setMaterialSpec(d.getMaterialSpec());
			m.setProportion(d.getProportion());
			m.setUnit(d.getUnit());
			m.setPriceSource(d.getPriceSource());
			m.setLatestPrice(d.getLatestPrice());
			m.setAvgPrice(d.getAvgPrice());
			m.setCalcPrice(d.getCalcPrice());
			m.setAmount(d.getAmount());
			if ("NONE".equals(d.getPriceSource())) hasUnpriced = true;
			list.add(m);
		}
		vo.setMaterialList(list);
		vo.setHasUnpriced(hasUnpriced);
		return vo;
	}

	@Override
	public void monthlyAutoCalc() {
		List<Product> products = productMapper.selectList(
				new LambdaQueryWrapper<Product>()
						.isNotNull(Product::getRecipeId)
						.eq(Product::getDelFlag, "0")
		);
		for (Product p : products) {
			try {
				CostCalcSnapshotDto dto = new CostCalcSnapshotDto();
				dto.setProductId(p.getId());
				dto.setCalcType("MONTHLY");
				dto.setRemark("月度自动核算");
				saveSnapshot(dto);
			} catch (Exception e) {
				log.error("月度核算失败, productId="+ p.getId(), e);
			}
		}
	}

//	private String generateCalcNo() {
//		String prefix = "CC" + org.jeecg.common.util.DateUtils.date2Str(new Date(), "yyyyMMdd");
//		LambdaQueryWrapper<CostCalc> qw = new LambdaQueryWrapper<>();
//		qw.likeRight(CostCalc::getCalcNo, prefix).orderByDesc(CostCalc::getCalcNo).last("LIMIT 1");
//		CostCalc last = getOne(qw);
//		int seq = 1;
//		if (last != null) {
//			String seqStr = last.getCalcNo().substring(last.getCalcNo().length() - 4);
//			seq = Integer.parseInt(seqStr) + 1;
//		}
//		return prefix + String.format("%04d", seq);
//	}
}
