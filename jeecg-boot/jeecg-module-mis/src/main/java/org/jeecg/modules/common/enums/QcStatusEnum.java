package org.jeecg.modules.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum QcStatusEnum {
    WAIT_CHECK("WAIT_CHECK", "待报检"),
    CHECKING("CHECKING", "质检中"),
    PASS("PASS", "质检合格"),
    FAIL("FAIL", "质检不合格");

    @EnumValue
    private final String code;

    @JsonValue
    private final String desc;

    QcStatusEnum(String code, String desc) {
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
