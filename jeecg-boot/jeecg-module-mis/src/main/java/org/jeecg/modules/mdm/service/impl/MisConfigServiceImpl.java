package org.jeecg.modules.mdm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mdm.entity.MisConfig;
import org.jeecg.modules.mdm.mapper.MisConfigMapper;
import org.jeecg.modules.mdm.service.IMisConfigService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 系统配置
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Slf4j
@Service
public class MisConfigServiceImpl extends ServiceImpl<MisConfigMapper, MisConfig> implements IMisConfigService {

    // 本地缓存
    private final Map<String, String> configCache = new ConcurrentHashMap<>();

    @Override
    public String getValue(String module, String code) {
        String key = module + ":" + code;
        String cached = configCache.get(key);
        if (cached != null) {
            return cached;
        }
        MisConfig config = lambdaQuery()
                .eq(MisConfig::getConfigModule, module)
                .eq(MisConfig::getConfigCode, code)
                .eq(MisConfig::getDelFlag, "0")
                .one();
        if (config != null && StringUtils.isNotBlank(config.getConfigValue())) {
            configCache.put(key, config.getConfigValue());
            return config.getConfigValue();
        }
        return null;
    }

    @Override
    public boolean getBoolean(String module, String code, boolean defaultValue) {
        String val = getValue(module, code);
        return StringUtils.isNotBlank(val) ? Boolean.parseBoolean(val) : defaultValue;
    }

    @Override
    public void refreshCache() {
        configCache.clear();
        log.info("配置缓存已刷新");
    }
}
