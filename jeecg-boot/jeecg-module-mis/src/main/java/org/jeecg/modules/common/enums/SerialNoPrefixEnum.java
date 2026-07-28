package org.jeecg.modules.common.enums;

import lombok.Getter;

@Getter
public enum SerialNoPrefixEnum {
    SALES_ORDER("SO", "销售订单"),
    PRODUCTION_PLAN("PL", "生产计划"),
    PRODUCTION_ORDER("MO", "生产订单"),
    PRODUCTION_WORK_ORDER("WO","生产工单"),
    PRODUCTION_BATCH_ORDER("BT","批次单"),
    RECEIVABLE_PLAN("SK", "应收计划"),      // 销售收款计划
    PAYABLE_PLAN("FK", "应付计划"),         // 采购付款计划
    RECEIPT_VOUCHER("RC", "收款单"),        // 实际收款单据
    PAYMENT_VOUCHER("PV", "付款单"),        // 实际付款单据
    PURCHASE_ORDER("PO", "采购订单"),
    STOCK_IN("RI","入库单"),
    STOCK_OUT("DO","出库单"),
    RETURN_ORDER("RO","退货单"),
    MATERIAL_REQUISITION("MR", "物料需求单"),
    DELIVERY_NOTE_ORDER("DN","发货单"),
    QUOTATION_ORDER("QT", "报价单"),
    SHELF_ON("SH","上架"),
    INVENTORY_CHECK("IC","盘库单"),
    INVENTORY_ADJUST("IA","调整单"),
    MOVE_STOCK("MS","移库"),
    LABEL_PRINT("LP","标签打印单"),
    COST_CALC("CC","成本核算");

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
