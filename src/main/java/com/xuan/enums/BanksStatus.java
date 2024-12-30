package com.xuan.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum BanksStatus {
    NOT_SHARED(0,"未分享"),
    PENDING(1,"审核中"),
    REJECTED(2,"被拒绝"),
    PUBLIC(3,"已公开");

    @EnumValue //告诉MP,该字段的值作为数据库值
    private final int value;
    @JsonValue //返回给前端时,会返回desc字段的文字内容
    private final String desc;


    BanksStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

}
