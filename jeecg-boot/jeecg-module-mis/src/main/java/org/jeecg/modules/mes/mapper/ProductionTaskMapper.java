package org.jeecg.modules.mes.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mes.entity.ProductionMaterial;
import org.jeecg.modules.mes.entity.ProductionTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.mes.vo.ProductionMaterialVo;
import org.jeecg.modules.mes.vo.ProductionTaskVo;

/**
 * @Description: 工序表
 * @Author: jeecg-boot
 * @Date:   2026-03-17
 * @Version: V1.0
 */
public interface ProductionTaskMapper extends BaseMapper<ProductionTask> {

    IPage<ProductionTaskVo> getPageList(@Param("page") Page<ProductionTaskVo> page,
                                            @Param("productionTaskVo") ProductionTaskVo productionTaskVo);
}
