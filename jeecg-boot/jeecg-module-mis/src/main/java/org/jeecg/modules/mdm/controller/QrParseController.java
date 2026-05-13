package org.jeecg.modules.mdm.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.mdm.vo.LocationQrVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 二维码统一解析服务
 * 支持：库位码、产品码、批次码
 */
@Tag(name = "mdm-二维码解析")
@RestController
@RequestMapping("/mdm/qrCode")
@Slf4j
public class QrParseController {
    /**
     * 统一解析二维码（JSON格式）
     */
    @AutoLog(value = "二维码-统一解析")
    @Operation(summary = "二维码-统一解析")
    @GetMapping(value = "/parse")
    public Result<?> parseQrCode(@RequestParam(name = "qrCode") String qrCode) {
        try {
            com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSON.parseObject(qrCode);
            String qrType = json.getString("qrType");

            // 兼容旧格式推断
            if (qrType == null) {
                if (json.containsKey("warehouseId") || json.containsKey("locationId")) {
                    qrType = "LOCATION";
                } else if (json.containsKey("batchNo") || json.containsKey("b")) {
                    qrType = "BATCH";
                } else if (json.containsKey("productCode") || json.containsKey("p")) {
                    qrType = "PRODUCT";
                }
            }

            switch (qrType) {
                case "LOCATION":
                    return parseLocationQr(json);
                case "PRODUCT":
                    return parseProductQr(json);
                case "BATCH":
                    return parseBatchQr(json);
                default:
                    return Result.error("未知的二维码类型: " + qrType);
            }
        } catch (Exception e) {
            return Result.error("二维码解析失败: " + e.getMessage());
        }
    }

    /**
     * 解析库位二维码
     */
    private Result<?> parseLocationQr(com.alibaba.fastjson.JSONObject json) {
        LocationQrVo vo = json.toJavaObject(LocationQrVo.class);
        // 校验必填字段
        if (vo.getWarehouseId() == null) {
            return Result.error("库位二维码缺少仓库ID");
        }

        return Result.OK(vo);
    }

    /**
     * 解析产品二维码（简化格式）
     */
    private Result<?> parseProductQr(com.alibaba.fastjson.JSONObject json) {
       // org.jeecg.modules.wms.vo.ProductQrVo vo = json.toJavaObject(org.jeecg.modules.wms.vo.ProductQrVo.class);
        return Result.OK("ok");
    }

    /**
     * 解析批次二维码
     */
    private Result<?> parseBatchQr(com.alibaba.fastjson.JSONObject json) {
       // org.jeecg.modules.wms.vo.BatchQrVo vo = json.toJavaObject(org.jeecg.modules.wms.vo.BatchQrVo.class);
        return Result.OK("ok");
    }
}
