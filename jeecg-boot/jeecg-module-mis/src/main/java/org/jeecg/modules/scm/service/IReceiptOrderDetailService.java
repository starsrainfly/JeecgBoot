package org.jeecg.modules.scm.service;

import org.jeecg.modules.scm.entity.ReceiptOrderDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 收款明细
 * @Author: jeecg-boot
 * @Date:   2026-04-23
 * @Version: V1.0
 */
public interface IReceiptOrderDetailService extends IService<ReceiptOrderDetail> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<ReceiptOrderDetail>
	 */
	public List<ReceiptOrderDetail> selectByMainId(String mainId);
}
