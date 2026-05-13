package org.jeecg.modules.mdm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 库位二维码解析VO（JSON格式）
 *
 * 用于上架、移库时扫描库位二维码自动填充
 *
 * JSON示例：
 * {
 *   "qrType": "LOCATION",
 *   "warehouseId": "abc123",
 *   "warehouseCode": "WH001",
 *   "warehouseName": "原料仓",
 *   "areaId": "def456",
 *   "areaCode": "A01",
 *   "areaName": "暂存区",
 *   "shelfId": "ghi789",
 *   "shelfCode": "S01",
 *   "shelfName": "货架01",
 *   "locationId": "jkl012",
 *   "locationCode": "L01",
 *   "locationName": "库位01",
 *   "locationLevel": "LOCATION",
 *   "pathCode": "WH001-A01-S01-L01",
 *   "length": 1.20,
 *   "width": 0.80,
 *   "height": 1.50,
 *   "volume": 1.44
 * }
 */
@Data
@Schema(description = "库位二维码解析结果")
public class LocationQrVo {

    // 二维码类型
    @Schema(description = "二维码类型 LOCATION库位/PRODUCT产品/BATCH批次")
    private String qrType;

    // 仓库信息
    @Schema(description = "仓库ID")
    private String warehouseId;

    @Schema(description = "仓库编码")
    private String warehouseCode;

    @Schema(description = "仓库名称")
    private String warehouseName;

    // 区域信息
    @Schema(description = "区域ID")
    private String areaId;

    @Schema(description = "区域编码")
    private String areaCode;

    @Schema(description = "区域名称")
    private String areaName;

    // 货架信息
    @Schema(description = "货架ID")
    private String shelfId;

    @Schema(description = "货架编码")
    private String shelfCode;

    @Schema(description = "货架名称")
    private String shelfName;

    // 货位信息
    @Schema(description = "货位ID")
    private String locationId;

    @Schema(description = "货位编码")
    private String locationCode;

    @Schema(description = "货位名称")
    private String locationName;

    // 仓库级别
    @Schema(description = "仓库级别 WAREHOUSE/AREA/SHELF/LOCATION")
    private String locationLevel;

    // 组合路径
    @Schema(description = "组合路径编码")
    private String pathCode;

    // 货位尺寸（可选）
    @Schema(description = "长(m)")
    private java.math.BigDecimal length;

    @Schema(description = "宽(m)")
    private java.math.BigDecimal width;

    @Schema(description = "高(m)")
    private java.math.BigDecimal height;

    @Schema(description = "体积(m³)")
    private java.math.BigDecimal volume;
}