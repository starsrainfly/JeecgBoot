package org.jeecg.modules.mes.service;

import org.jeecg.modules.mes.entity.LabelPrintTask;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: mis_label_print_task
 * @Author: jeecg-boot
 * @Date:   2026-04-27
 * @Version: V1.0
 */
public interface ILabelPrintTaskService extends IService<LabelPrintTask> {
    /**
     * 生成标签预览图片
     */
    String generateLabelImage(String taskId);

    /**
     * 生成标签预览图片（指定DPI）
     */
    String generateLabelImage(String taskId, int dpi);
}
