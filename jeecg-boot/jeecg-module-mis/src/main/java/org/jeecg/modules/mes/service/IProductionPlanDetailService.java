package org.jeecg.modules.mes.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.mes.entity.ProductionPlanDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mes.vo.ProductionPlanDetailVo;

import java.util.List;

/**
 * @Description: 生产计划明细表
 * @Author: jeecg-boot
 * @Date:   2026-03-08
 * @Version: V1.0
 */
public interface IProductionPlanDetailService extends IService<ProductionPlanDetail> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<ProductionPlanDetail>
	 */
	public List<ProductionPlanDetail> selectByMainId(String mainId);

	/**
	 * 查询可用计划明细（已发布且未全部分配）
	 *
	 * @param page 分页参数
	 * @param planNo 计划编号（可选）
	 * @param productCode 产品编码（可选）
	 * @return 分页结果
	 */
	IPage<ProductionPlanDetailVo> listAvailableForOrder(Page<ProductionPlanDetailVo> page,
														String planNo,
														String productCode);
}
