package org.jeecg.modules.scm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.scm.dto.CostCalcSnapshotDto;
import org.jeecg.modules.scm.entity.CostCalcDetail;
import org.jeecg.modules.scm.entity.CostCalc;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.scm.vo.CostCalcDetailVo;
import org.jeecg.modules.scm.vo.CostCalcProductVo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 成本核算快照
 * @Author: jeecg-boot
 * @Date:   2026-07-28
 * @Version: V1.0
 */
public interface ICostCalcService extends IService<CostCalc> {

	/**
	 * 添加一对多
	 *
	 * @param costCalc
	 * @param costCalcDetailList
	 */
	public void saveMain(CostCalc costCalc,List<CostCalcDetail> costCalcDetailList) ;
	
	/**
	 * 修改一对多
	 *
   * @param costCalc
   * @param costCalcDetailList
	 */
	public void updateMain(CostCalc costCalc,List<CostCalcDetail> costCalcDetailList);
	
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

	IPage<CostCalcProductVo> queryProductList(Page<CostCalcProductVo> page, String productCode, String productName);

	CostCalcDetailVo calculateCost(String productId);

	CostCalc saveSnapshot(CostCalcSnapshotDto dto);

	CostCalcDetailVo getSnapshotDetail(String calcId);

	void monthlyAutoCalc();
	
}
