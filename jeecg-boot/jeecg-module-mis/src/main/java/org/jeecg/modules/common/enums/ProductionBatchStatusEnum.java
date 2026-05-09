package org.jeecg.modules.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductionBatchStatusEnum {
    PENDING("PENDING", "待配料"),
    WEIGHING("WEIGHING", "配料中"),
    WEIGHED("WEIGHED", "配料完成"),
    PRODUCING("PRODUCING", "生产中"),

    COMPLETED("COMPLETED","完成");

    @EnumValue
    private final String code;

    @JsonValue
    private final String desc;

    ProductionBatchStatusEnum(String code, String desc) {
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
