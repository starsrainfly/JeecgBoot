package org.jeecg.modules.mdm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mdm.entity.PackageMapping;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 内外包装映射表
 * @Author: jeecg-boot
 * @Date:   2026-03-03
 * @Version: V1.0
 */
public interface PackageMappingMapper extends BaseMapper<PackageMapping> {

    PackageMapping getByInnerAndOuter(String innerPackageId, String outerPackageId);
}
