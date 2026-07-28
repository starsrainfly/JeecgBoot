package org.jeecg.modules.mes.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.mdm.entity.QcItemConfig;
import org.jeecg.modules.mdm.mapper.QcItemConfigMapper;
import org.jeecg.modules.mes.entity.ProductionBatch;
import org.jeecg.modules.mes.entity.ProductionTask;
import org.jeecg.modules.mes.entity.QcRecord;
import org.jeecg.modules.mes.entity.QcRecordDetail;
import org.jeecg.modules.mes.mapper.QcRecordDetailMapper;
import org.jeecg.modules.mes.mapper.QcRecordMapper;
import org.jeecg.modules.mes.service.IProductionBatchService;
import org.jeecg.modules.mes.service.IProductionTaskService;
import org.jeecg.modules.mes.service.IQcRecordDetailService;
import org.jeecg.modules.mes.service.IQcRecordService;
import org.jeecg.modules.mes.vo.QcRecordPage;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.*;

/**
 * @Description: 质检记录
 * @Author: jeecg-boot
 * @Date:   2026-07-24
 * @Version: V1.0
 */
@Service
public class QcRecordServiceImpl extends ServiceImpl<QcRecordMapper, QcRecord> implements IQcRecordService {

	@Autowired
	private QcRecordMapper qcRecordMapper;
	@Autowired
	private QcRecordDetailMapper qcRecordDetailMapper;

	@Autowired
	private IQcRecordDetailService qcRecordDetailService;
	@Autowired
	private IProductionTaskService productionTaskService;
	@Autowired
	private IProductionBatchService productionBatchService;
	@Autowired
	private QcItemConfigMapper qcItemConfigMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void completeQc(QcRecordPage page) {
		// ===== 1. 校验 =====
		if (StrUtil.isBlank(page.getQcTaskId())) {
			throw new JeecgBootException("质检工单id不能为空");
		}
		ProductionTask task = productionTaskService.getById(page.getQcTaskId());
		if (task == null) {
			throw new JeecgBootException("质检工单不存在");
		}
		if (!"qc".equals(task.getTaskType())) {
			throw new JeecgBootException("该工单不是质检工单");
		}
		if ("COMPLETED".equals(task.getStatus())) {
			throw new JeecgBootException("该质检工单已完成，请勿重复提交");
		}
		if (StrUtil.isBlank(page.getQcResult())) {
			throw new JeecgBootException("请选择质检结果");
		}

		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		Date now = new Date();

		// ===== 2. 保存质检记录主表（VO 拷贝成实体再 save）=====
		QcRecord record = new QcRecord();
		BeanUtils.copyProperties(page, record);   // org.springframework.beans.BeanUtils
		record.setId(null);
		record.setInspectorId(loginUser.getId());
		record.setInspectorName(loginUser.getRealname());
		record.setInspectTime(now);
		// 工单信息兜底
		if (StrUtil.isBlank(record.getBatchId())) record.setBatchId(task.getBatchId());
		if (StrUtil.isBlank(record.getBatchNo())) record.setBatchNo(task.getBatchNo());
		if (StrUtil.isBlank(record.getOrderNo())) record.setOrderNo(task.getOrderNo());
		if (StrUtil.isBlank(record.getProductId())) record.setProductId(task.getProductId());
		if (StrUtil.isBlank(record.getProductCode())) record.setProductCode(task.getProductCode());
		if (StrUtil.isBlank(record.getProductName())) record.setProductName(task.getProductName());
		if (StrUtil.isBlank(record.getSourceTaskId())) record.setSourceTaskId(task.getSourceTaskId());
		record.setQcTaskNo(task.getTaskNo());   // 质检工单号

		// 来源工单提前查一次，step5 复用
		ProductionTask sourceTask = null;
		if (StrUtil.isNotBlank(task.getSourceTaskId())) {
			sourceTask = productionTaskService.getById(task.getSourceTaskId());
			if (sourceTask != null) {
				record.setSourceTaskNo(sourceTask.getTaskNo());   // 来源工单号
			}
		}
		this.save(record);

		// ===== 3. 保存明细 =====
		if (CollUtil.isNotEmpty(page.getQcRecordDetailList())) {
			int sort = 1;
			for (QcRecordDetail detail : page.getQcRecordDetailList()) {
				detail.setId(null);
				detail.setRecordId(record.getId());
				if (detail.getSortNo() == null) {
					detail.setSortNo(sort);
				}
				sort++;
				qcRecordDetailService.save(detail);
			}
		}

		// ===== 4. 完工质检工单（开工/完工合并，自动补开始时间）=====
		task.setStatus("COMPLETED");
		task.setQcStatus(record.getQcResult());   // PASS/FAIL/REWORK
		task.setQcRemark(record.getQcConclusion());
		if (task.getActualStartTime() == null) {
			task.setActualStartTime(now);
		}
		task.setActualEndTime(now);
		long duration = (task.getActualEndTime().getTime() - task.getActualStartTime().getTime()) / (1000 * 60);
		task.setActualDuration((int) duration);
		task.setActualOperatorId(loginUser.getId());
		task.setActualOperatorName(loginUser.getRealname());
		productionTaskService.updateById(task);

		// ===== 5. 回写来源工单：直接显示质检结论 =====
		if (sourceTask != null) {
			sourceTask.setQcStatus(record.getQcResult());  // PASS/FAIL/REWORK
			productionTaskService.updateById(sourceTask);
		}
	}

	@Override
	public List<Map<String, Object>> previewItems(String taskId) {
		List<Map<String, Object>> items = new ArrayList<>();
		if (StrUtil.isBlank(taskId)) {
			return items;
		}
		ProductionTask task = productionTaskService.getById(taskId);
		if (task == null || StrUtil.isBlank(task.getBatchId())) {
			return items;
		}
		ProductionBatch batch = productionBatchService.getById(task.getBatchId());
		if (batch == null || StrUtil.isBlank(batch.getRecipeId())) {
			return items;  // 批次未关联配方，质检员手工添加
		}
		Map<String, Object> spec = this.baseMapper.selectRecipeSpec(batch.getRecipeId());
		if (spec == null || spec.isEmpty()) {
			return items;
		}
		List<QcItemConfig> configs = qcItemConfigMapper.selectList(
				new LambdaQueryWrapper<QcItemConfig>()
						.eq(QcItemConfig::getEnabled, "1")
						.orderByAsc(QcItemConfig::getSortNo)
		);
		for (QcItemConfig cfg : configs) {
			Object val = spec.get(cfg.getFieldName());
			if (val != null && StrUtil.isNotBlank(val.toString())) {
				Map<String, Object> item = new HashMap<>();
				item.put("itemName", cfg.getItemName());
				item.put("standard", val.toString() + (StrUtil.isNotBlank(cfg.getUnit()) ? " " + cfg.getUnit() : ""));
				item.put("actualValue", null);
				item.put("itemResult", null);
				items.add(item);
			}
		}
		return items;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(QcRecord qcRecord, List<QcRecordDetail> qcRecordDetailList) {
		qcRecordMapper.insert(qcRecord);
		if(qcRecordDetailList!=null && qcRecordDetailList.size()>0) {
			for(QcRecordDetail entity:qcRecordDetailList) {
				//外键设置
				entity.setRecordId(qcRecord.getId());
				qcRecordDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(QcRecord qcRecord,List<QcRecordDetail> qcRecordDetailList) {
		qcRecordMapper.updateById(qcRecord);
		
		//1.先删除子表数据
		qcRecordDetailMapper.deleteByMainId(qcRecord.getId());
		
		//2.子表数据重新插入
		if(qcRecordDetailList!=null && qcRecordDetailList.size()>0) {
			for(QcRecordDetail entity:qcRecordDetailList) {
				//外键设置
				entity.setRecordId(qcRecord.getId());
				qcRecordDetailMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		qcRecordDetailMapper.deleteByMainId(id);
		qcRecordMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			qcRecordDetailMapper.deleteByMainId(id.toString());
			qcRecordMapper.deleteById(id);
		}
	}
	
}
