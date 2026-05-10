package org.jeecg.modules.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductionTaskStatusEnum {
    PENDING("PENDING", "待派工"),
    ASSIGNED("ASSIGNED", "已派工"),
    PROCESSING("PROCESSING", "进行中"),
    COMPLETED("COMPLETED", "完成");



    @EnumValue
    private final String code;

    @JsonValue
    private final String desc;

    ProductionTaskStatusEnum(String code, String desc) {
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
