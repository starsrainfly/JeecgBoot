package org.jeecg.modules.mdm.service.impl;

import org.jeecg.modules.mdm.entity.PackageMapping;
import org.jeecg.modules.mdm.mapper.PackageMappingMapper;
import org.jeecg.modules.mdm.service.IPackageMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 内外包装映射表
 * @Author: jeecg-boot
 * @Date:   2026-03-03
 * @Version: V1.0
 */
@Service
public class PackageMappingServiceImpl extends ServiceImpl<PackageMappingMapper, PackageMapping> implements IPackageMappingService {

    @Autowired
    private PackageMappingMapper packageMappingMapper;
    @Override
    public PackageMapping getByInnerAndOuter(String innerPackageId, String outerPackageId) {
        return packageMappingMapper.getByInnerAndOuter(innerPackageId, outerPackageId);
    }
}
