package org.jeecg.modules.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public class StockEnum {

    @Getter
    public enum StockInType {

        PURCHASE("PURCHASE", "采购入库"),
        PRODUCTION("PRODUCTION", "生产入库"),
        TRANSFER("TRANSFER", "调拨入库"),
        RETURN("RETURN", "销售退货"),
        INVENTORY("ADJUST_PROFIT", "盘盈入库"),
        RECYCLE("RECYCLE","退废/回收"),
        OTHER("OTHER", "其他入库");

        @EnumValue  // 1. 存入数据库时，只存 code ("PURCHASE")
        private final String code;

        @JsonValue    // 2. 返回给前端 JSON 时，只返回 code ("PURCHASE")
        private final String desc;

        StockInType(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    @Getter
    public enum StockOutType {
        SALES("SALES", "销售出库"),
        RETURN("RETURN", "采购退货"),
        TRANSFER("TRANSFER", "调拨出库"),
        PRODUCTION("PRODUCTION", "生产领料"),
        INVENTORY("ADJUST_LOSS", "盘亏出库"),
        SCRAP("SCRAP","报废出库");


        @EnumValue  // 1. 存入数据库时，只存 code ("PURCHASE")
        private final String code;

        @JsonValue    // 2. 返回给前端 JSON 时，只返回 code ("PURCHASE")
        private final String desc;

        StockOutType(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    @Getter
    public enum StockInStatus {
        APPLY("APPLY","申请中"),
        FINISHED("FINISHED","已完成"),
        CANCEL("CANCEL","已取消");
        private final String code;
        private final String desc;
        StockInStatus(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    @Getter
    public enum StockOutStatus {
        APPLY("APPLY","申请中"),
        FINISHED("FINISHED","已完成"),
        CANCEL("CANCEL","已取消");
        private final String code;
        private final String desc;
        StockOutStatus(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }
}
