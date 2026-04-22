package org.jeecg.modules.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum SalesOrderStatusEnum {
    APPLY("0", "申请中"),
    APPROVE("1", "审核中"),
    WAIT_DELIVERY("2", "待发货"),
    PARTIAL_DELIVERY("3", "部分发货"),
    COMPLETED("4","已完成"),
    CANCELLED("9","作废");

    @EnumValue
    private final String code;

    @JsonValue
    private final String desc;

    SalesOrderStatusEnum(String code, String desc) {
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
