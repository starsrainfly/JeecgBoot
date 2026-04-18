package org.jeecg.modules.common.service;

public interface ISerialNoService {
/**
 * 生成单号
 * @param prefix 前缀 (如 "PO", "SO", "MO")
 * @return 完整单号
 */
    String generateSerialNo(String prefix);

    /**
     * 生成客户编码
     * @param tradeType  --贸易类型 内贸还是外贸
     * @param districtCode  --区域代码
     * @param countryCode  --国家代码
     * @return
     */
    String generateCustomerCode(String tradeType, String districtCode, String countryCode);

    /**
     * 生成供应商编码
     * @param tradeType  --贸易类型 内贸还是外贸
     * @param districtCode  --区域代码
     * @param countryCode  --国家代码
     * @return
     */
    String generateSupplierCode(String tradeType, String districtCode, String countryCode);
}
