package org.jeecg.modules.mes.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.mes.entity.QcRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 质检记录
 * @Author: jeecg-boot
 * @Date:   2026-07-24
 * @Version: V1.0
 */
public interface QcRecordMapper extends BaseMapper<QcRecord> {

    /**
     * 根据配方id查询技术指标（转检验项目用）
     * 注意：返回Map的key为驼峰命名（开启下划线转驼峰），与 mis_qc_item_config.field_name 对应
     */
    @Select("SELECT * FROM mis_recipe_spec WHERE recipe_id = #{recipeId} LIMIT 1")
    Map<String , Object> selectRecipeSpec(@Param("recipeId") String recipeId);
}
