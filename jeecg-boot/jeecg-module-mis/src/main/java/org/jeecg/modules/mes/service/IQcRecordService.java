package org.jeecg.modules.mes.service;

import org.jeecg.modules.mes.entity.QcRecordDetail;
import org.jeecg.modules.mes.entity.QcRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mes.vo.QcRecordPage;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Description: 质检记录
 * @Author: jeecg-boot
 * @Date:   2026-07-24
 * @Version: V1.0
 */
public interface IQcRecordService extends IService<QcRecord> {

	/**
	 * 添加一对多
	 *
	 * @param qcRecord
	 * @param qcRecordDetailList
	 */
	public void saveMain(QcRecord qcRecord,List<QcRecordDetail> qcRecordDetailList) ;
	
	/**
	 * 修改一对多
	 *
   * @param qcRecord
   * @param qcRecordDetailList
	 */
	public void updateMain(QcRecord qcRecord,List<QcRecordDetail> qcRecordDetailList);
	
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

	public void completeQc(QcRecordPage page);

	public List<Map<String, Object>> previewItems(String taskId);
	
}
