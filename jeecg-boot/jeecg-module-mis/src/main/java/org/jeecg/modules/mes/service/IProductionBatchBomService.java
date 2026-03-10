package org.jeecg.modules.mes.service;

import org.jeecg.modules.mes.entity.ProductionBatchBom;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 生产批次物料清单
 * @Author: jeecg-boot
 * @Date:   2026-03-06
 * @Version: V1.0
 */
public interface IProductionBatchBomService extends IService<ProductionBatchBom> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<ProductionBatchBom>
	 */
	public List<ProductionBatchBom> selectByMainId(String mainId);
}
