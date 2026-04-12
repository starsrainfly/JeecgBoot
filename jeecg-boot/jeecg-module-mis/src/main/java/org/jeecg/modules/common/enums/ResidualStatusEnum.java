package org.jeecg.modules.common.enums;

import lombok.Getter;

@Getter
public enum ResidualStatusEnum {
    AVAILABLE("AVAILABLE", "可用"),
    LOCKED("LOCKED", "锁定"),
    USED("USED", "已用完");

    private final String code;
    private final String desc;

    ResidualStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() { return code; }
    public String getDesc() { return desc; }
}
