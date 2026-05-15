package org.jeecg.modules.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jeecg.modules.mdm.vo.QrParseResult;

public class QrCodeParser {
    /**
     * 统一解析二维码内容
     */
    public static QrParseResult parse(String scanCode) {
        QrParseResult result = new QrParseResult();
        result.setRawContent(scanCode);

        if (scanCode == null || scanCode.trim().isEmpty()) {
            result.setParsed(false);
            result.setErrorMsg("扫码内容为空");
            return result;
        }

        String code = scanCode.trim();

        // 1. 新系统 JSON 格式
        if (code.startsWith("{")) {
            return parseJsonFormat(code, result);
        }

        // 2. 老系统 ; 分隔格式
        if (code.contains(";")) {
            return parseOldFormat(code, result);
        }

        // 3. | 分隔格式（goodsCode|batchNo|stockId）
        if (code.contains("|")) {
            return parsePipeFormat(code, result);
        }

        // 4. 纯文本（默认当作产品编码）
        result.setTagType("PRODUCT");
        result.setProductCode(code);
        result.setParsed(true);
        return result;
    }

    /**
     * 解析 JSON 格式
     */
    private static QrParseResult parseJsonFormat(String code, QrParseResult result) {
        try {
            JSONObject json = JSON.parseObject(code);
            String tagType = json.getString("t");
            result.setTagType(tagType);

            if ("PRODUCT".equals(tagType)) {
                result.setProductCode(json.getString("p"));
                result.setBatchNo(json.getString("b"));
                result.setProduceDate(json.getString("d"));
                result.setExpiryDate(json.getString("e"));
                result.setSpec(json.getString("s"));

            } else if ("LOCATION".equals(tagType)) {
                result.setWarehouseId(json.getString("w"));
                result.setAreaId(json.getString("a"));
                result.setShelfId(json.getString("sh"));
                result.setLocationId(json.getString("l"));
               // result.setLocationName(json.getString("n"));

            } else if ("MATERIAL".equals(tagType)) {
                result.setMaterialCode(json.getString("m"));
                result.setMaterialName(json.getString("n"));
                result.setLotNo(json.getString("b"));
                result.setSpec(json.getString("s"));
            }

            // 校验
            if (!validateByType(result)) {
                return result;
            }

            result.setParsed(true);
            return result;

        } catch (Exception e) {
            result.setParsed(false);
            result.setErrorMsg("JSON二维码格式错误: " + e.getMessage());
            return result;
        }
    }

    /**
     * 解析老系统 ; 分隔格式
     * 格式：orderNo;productNo;batchNo;productDate;deliverDate;singleWeight
     */
    private static QrParseResult parseOldFormat(String code, QrParseResult result) {
        String[] parts = code.split(";");
        if (parts.length >= 3) {
            result.setTagType("PRODUCT");
            result.setOrderNo(parts[0]);
            result.setProductCode(parts[1]);
            result.setBatchNo(parts[2]);
            if (parts.length > 3) result.setProduceDate(parts[3]);
            if (parts.length > 4) result.setExpiryDate(parts[4]);
            if (parts.length > 5) result.setSingleWeight(parts[5]);
            result.setParsed(true);
        } else {
            result.setParsed(false);
            result.setErrorMsg("老格式分隔符不足，至少需要3段");
        }
        return result;
    }

    /**
     * 解析 | 分隔格式
     */
    private static QrParseResult parsePipeFormat(String code, QrParseResult result) {
        String[] parts = code.split("\\|");
        result.setTagType("PRODUCT");
        result.setProductCode(parts[0]);
        if (parts.length > 1) {
            result.setBatchNo(parts[1]);
        }
        result.setParsed(true);
        return result;
    }

    /**
     * 按类型校验必填字段
     */
    private static boolean validateByType(QrParseResult result) {
        String type = result.getTagType();

        if (type == null) {
            result.setParsed(false);
            result.setErrorMsg("缺少标签类型字段(t)");
            return false;
        }

        switch (type) {
            case "PRODUCT":
                if (isEmpty(result.getProductCode())) {
                    result.setParsed(false);
                    result.setErrorMsg("产品标签缺少产品编码(p)");
                    return false;
                }
                break;
            case "LOCATION":
                if (isEmpty(result.getLocationId())) {
                    result.setParsed(false);
                    result.setErrorMsg("库位标签缺少货位编码(l)");
                    return false;
                }
                break;
            case "MATERIAL":
                if (isEmpty(result.getMaterialCode())) {
                    result.setParsed(false);
                    result.setErrorMsg("物料标签缺少物料编码(m)");
                    return false;
                }
                break;
            default:
                result.setParsed(false);
                result.setErrorMsg("未知标签类型: " + type);
                return false;
        }

        return true;
    }

    private static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
