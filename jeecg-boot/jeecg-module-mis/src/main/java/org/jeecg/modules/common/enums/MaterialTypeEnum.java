package org.jeecg.modules.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum MaterialTypeEnum {

    RAW("RAW", "源材料"),
    INNER_PACK("INNER_PACK", "内包装"),
    OUTER_PACK("OUTER_PACK", "外包装");


    @EnumValue
    private final String code;

    @JsonValue
    private final String desc;

    MaterialTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
