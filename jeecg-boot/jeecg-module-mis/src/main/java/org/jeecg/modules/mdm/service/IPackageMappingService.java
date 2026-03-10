package org.jeecg.modules.mdm.service;

import org.jeecg.modules.mdm.entity.PackageMapping;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 内外包装映射表
 * @Author: jeecg-boot
 * @Date:   2026-03-03
 * @Version: V1.0
 */
public interface IPackageMappingService extends IService<PackageMapping> {

    PackageMapping getByInnerAndOuter(String innerPackageId, String outerPackageId);
}
