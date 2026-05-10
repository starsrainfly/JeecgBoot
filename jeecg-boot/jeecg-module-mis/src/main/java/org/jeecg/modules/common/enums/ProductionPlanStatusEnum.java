package org.jeecg.modules.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductionPlanStatusEnum {
    PENDING("0", "编排中"),
    PUBLISHED("1", "已发布"),
    PROCESSING("2", "进行中"),
    COMPLETED("3", "完成");



    @EnumValue
    private final String code;

    @JsonValue
    private final String desc;

    ProductionPlanStatusEnum(String code, String desc) {
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
