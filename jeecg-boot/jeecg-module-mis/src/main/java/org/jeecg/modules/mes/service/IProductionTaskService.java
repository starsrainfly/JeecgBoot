package org.jeecg.modules.mes.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.mes.entity.ProductionBatch;
import org.jeecg.modules.mes.entity.ProductionTask;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mes.vo.ProductionTaskVo;

/**
 * @Description: 工序表
 * @Author: jeecg-boot
 * @Date:   2026-03-11
 * @Version: V1.0
 */
public interface IProductionTaskService extends IService<ProductionTask> {

    /**
     * 生成任务记录
     * @param batch
     * @param routingId
     * @param packageInfo
     */
    public void generateTasks(ProductionBatch batch, String routingId, String packageInfo);

    /**
     * 分页查询工单列表（关联工艺步骤）
     */
    IPage<ProductionTaskVo> getPageList(Page<ProductionTaskVo> page, ProductionTaskVo productionTaskVo);
}
