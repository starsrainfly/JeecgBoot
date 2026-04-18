package org.jeecg.modules.wms.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.wms.entity.Delivery;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.wms.vo.DeliveryTaskVo;

/**
 * @Description: 发货表
 * @Author: jeecg-boot
 * @Date:   2026-04-15
 * @Version: V1.0
 */
public interface DeliveryMapper extends BaseMapper<Delivery> {

    /**
     * 待发货任务列表
     */
    IPage<DeliveryTaskVo> queryTaskList(Page<DeliveryTaskVo> page, @Param("param") Map<String, Object> param);
}
