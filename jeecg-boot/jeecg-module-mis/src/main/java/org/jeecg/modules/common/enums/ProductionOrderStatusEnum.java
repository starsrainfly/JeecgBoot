package org.jeecg.modules.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductionOrderStatusEnum {
    DRAFT("0", "草稿"),
    DELIVER("1", "下达"),
    PARTIAL_COMPLETED("2", "部分完成"),
    COMPLETED("3", "完成"),

    CANCELLED("9","作废");

    @EnumValue
    private final String code;

    @JsonValue
    private final String desc;

    ProductionOrderStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public String getValue() {
        return code;
    }


    public String getLabel() {
        return desc;
    }
}
