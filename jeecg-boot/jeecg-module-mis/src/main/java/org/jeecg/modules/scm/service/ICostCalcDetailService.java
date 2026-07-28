package org.jeecg.modules.scm.service;

import org.jeecg.modules.scm.entity.CostCalcDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 成本核算快照明细
 * @Author: jeecg-boot
 * @Date:   2026-07-28
 * @Version: V1.0
 */
public interface ICostCalcDetailService extends IService<CostCalcDetail> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<CostCalcDetail>
	 */
	public List<CostCalcDetail> selectByMainId(String mainId);
}
