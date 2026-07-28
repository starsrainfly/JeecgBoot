package org.jeecg.modules.mes.service;

import org.jeecg.modules.mes.entity.QcRecordDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 质检记录明细
 * @Author: jeecg-boot
 * @Date:   2026-07-24
 * @Version: V1.0
 */
public interface IQcRecordDetailService extends IService<QcRecordDetail> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<QcRecordDetail>
	 */
	public List<QcRecordDetail> selectByMainId(String mainId);
}
