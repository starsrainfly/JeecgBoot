package org.jeecg.modules.common.enums;

import lombok.Getter;

@Getter
public enum ApproveStatusEnum {
    PENDING("0","审核中"),
    PASS("1","审核通过"),
    REJECT("2","审核不通过");

    private final String code;
    private final String desc;
    ApproveStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
