package org.jeecg.modules.scm.service;

import org.jeecg.modules.scm.entity.SalesOrderLine;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 销售订单明细表
 * @Author: jeecg-boot
 * @Date:   2026-02-07
 * @Version: V1.0
 */
public interface ISalesOrderLineService extends IService<SalesOrderLine> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<SalesOrderLine>
	 */
	public List<SalesOrderLine> selectByMainId(String mainId);
}
