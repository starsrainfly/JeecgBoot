package org.jeecg.modules.common.enums;

import lombok.Getter;

@Getter
public enum SerialNoPrefixEnum {
    SALES_ORDER("SO", "销售订单"),
    PRODUCTION_PLAN("PL", "生产计划"),
    PRODUCTION_ORDER("MO", "生产订单"),
    PRODUCTION_WORK_ORDER("WO","生产工单"),
    PRODUCTION_BATCH_ORDER("BT","批次单号"),
    RECEIPT_PLAN("RP", "收款计划"), // 新增
    PAYMENT_PLAN("PP", "付款计划"), // 预留
    RECEIPT_COLLECTION("RC","收款单号"),
    PURCHASE_ORDER("PO", "采购订单"),
    STOCK_IN("RI","入库单"),
    STOCK_OUT("DO","出库单"),
    RETURN_ORDER("RO","退货单号"),
    MATERIAL_REQUISITION("MR", "物料需求单"),
    DELIVERY_NOTE_ORDER("DN","发货单"),
    QUOTATION_ORDER("QT", "报价单");

    private final String prefix;
    private final String description;

    SerialNoPrefixEnum(String prefix, String description) {
        this.prefix = prefix;
        this.description = description;
    }

    // 可选：提供根据业务类型获取前缀的方法
    public static String getPrefixByType(String type) {
        for (SerialNoPrefixEnum e : values()) {
            if (e.name().equalsIgnoreCase(type)) {
                return e.prefix;
            }
        }
        throw new IllegalArgumentException("Unknown serial type: " + type);
    }
}
