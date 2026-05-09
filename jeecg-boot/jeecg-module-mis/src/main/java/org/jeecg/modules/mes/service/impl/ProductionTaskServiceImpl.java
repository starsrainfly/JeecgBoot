package org.jeecg.modules.mes.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.common.enums.SerialNoPrefixEnum;
import org.jeecg.modules.common.service.ISerialNoService;
import org.jeecg.modules.mdm.entity.ProcessRoutingStep;
import org.jeecg.modules.mdm.service.IProcessRoutingStepService;
import org.jeecg.modules.mes.entity.ProductionBatch;
import org.jeecg.modules.mes.entity.ProductionTask;
import org.jeecg.modules.mes.mapper.ProductionTaskMapper;
import org.jeecg.modules.mes.service.IProductionTaskService;
import org.jeecg.modules.mes.vo.ProductionTaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @Description: 工序表
 * @Author: jeecg-boot
 * @Date:   2026-03-11
 * @Version: V1.0
 */
@Service
public class ProductionTaskServiceImpl extends ServiceImpl<ProductionTaskMapper, ProductionTask> implements IProductionTaskService {
    @Autowired
    private IProcessRoutingStepService routingStepService;

    @Autowired
    private ISerialNoService serialNoService;
    @Autowired
    private ProductionTaskMapper productionTaskMapper;


    @Override
    @Transactional
    public void generateTasks(ProductionBatch batch, String routingId, String packageInfo) {
        // 获取工艺路线明细（按顺序）
        List<ProcessRoutingStep> steps = routingStepService.selectByMainId(routingId);
        if (CollectionUtils.isEmpty(steps)) {
            throw new JeecgBootException("工艺路线明细为空");
        }

        for (ProcessRoutingStep step : steps) {
            ProductionTask task = new ProductionTask();

            String taskNo = serialNoService.generateSerialNo(SerialNoPrefixEnum.PRODUCTION_WORK_ORDER.getPrefix());
            task.setTaskNo(taskNo);  // WO20250311001
            task.setTaskName(taskNo + "-" + step.getStepName());
            task.setBatchId(batch.getId());
            task.setBatchNo(batch.getBatchNo());
            task.setOrderNo(batch.getOrderNo());
            task.setRoutingDetailId(step.getId());  // 关联工艺工序
            task.setSequence(step.getStepSeq());
            task.setTaskDesc(step.getStepDesc());
            // 计划信息
            task.setPlanEquipmentId(step.getEquipmentId());
            task.setPlanEquipmentCode(step.getEquipmentCode());
            task.setPlanEquipmentName(step.getEquipmentName());
            task.setPlanModel(step.getModel());
            task.setPlanEquipmentType(step.getEquipmentType());
            task.setPlanDuration(step.getDuration());
            task.setPlanEquipmentSettings(step.getEquipmentSettings());
            task.setProductId(batch.getProductId());
            task.setProductCode(batch.getProductCode());
            task.setProductName(batch.getProductName());
            task.setProductColor(batch.getProductColor());
            task.setCompanyId(batch.getCompanyId());
            task.setCompanyName(batch.getCompanyName());
            task.setQcRequired(step.getQcRequired());
            task.setStatus("PENDING");
            if("1".equals(step.getIsMaterialStep())){
                task.setTaskType("weighing");
            }
            else if("1".equals(step.getIsPackageStep())){
                task.setTaskType("package");
            }
            else {
                task.setTaskType("production");
            }

            // 如果是包装工序，追加包装信息
            if ("1".equals(step.getIsPackageStep()) && StringUtils.isNotBlank(packageInfo)) {
                String originalDesc = task.getTaskDesc();
                task.setTaskDesc((StringUtils.isNotBlank(originalDesc) ? originalDesc + "\n" : "") + packageInfo);
            }

            this.save(task);
        }
    }

    @Override
    public IPage<ProductionTaskVo> getPageList(Page<ProductionTaskVo> page, ProductionTaskVo productionTaskVo) {
        return productionTaskMapper.getPageList(page, productionTaskVo);
    }
}
