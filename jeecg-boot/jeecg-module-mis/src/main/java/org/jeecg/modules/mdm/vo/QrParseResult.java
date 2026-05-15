package org.jeecg.modules.mdm.vo;

import lombok.Data;

@Data
public class QrParseResult {
    /** 标签类型：PRODUCT / LOCATION / MATERIAL / UNKNOWN */
    private String tagType;

    /** 原始扫码内容 */
    private String rawContent;

    /** 是否解析成功 */
    private boolean parsed;

    /** 解析失败原因 */
    private String errorMsg;

    // ===== 产品标签字段 =====
    private String productCode;
    private String batchNo;
    private String produceDate;
    private String expiryDate;
    private String spec;

    // ===== 库位标签字段 =====
    private String warehouseId;
    private String areaId;
    private String shelfId;
    private String locationId;
    private String pathCode;

    // ===== 物料标签字段 =====
    private String materialId;
    private String materialCode;
    private String materialName;
    private String lotNo;

    // ===== 老系统兼容字段 =====
    private String orderNo;
    private String singleWeight;

    public boolean isProductTag() {
        return "PRODUCT".equals(tagType);
    }

    public boolean isLocationTag() {
        return "LOCATION".equals(tagType);
    }

    public boolean isMaterialTag() {
        return "MATERIAL".equals(tagType);
    }
}
