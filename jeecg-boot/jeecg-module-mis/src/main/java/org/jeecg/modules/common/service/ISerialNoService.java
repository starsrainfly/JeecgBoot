package org.jeecg.modules.common.service;

public interface ISerialNoService {
/**
 * 生成单号
 * @param prefix 前缀 (如 "PO", "SO", "MO")
 * @return 完整单号
 */
    String generateSerialNo(String prefix);
}
