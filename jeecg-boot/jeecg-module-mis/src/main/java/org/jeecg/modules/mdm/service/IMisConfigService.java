package org.jeecg.modules.mdm.service;

import org.jeecg.modules.mdm.entity.MisConfig;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 系统配置
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
public interface IMisConfigService extends IService<MisConfig> {
    /**
     * 获取配置值
     */
    String getValue(String module, String code);

    /**
     * 获取布尔配置
     */
    boolean getBoolean(String module, String code, boolean defaultValue);

    /**
     * 刷新缓存
     */
    void refreshCache();
}
