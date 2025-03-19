package com.dyhhhhh.common.enums;

import lombok.Getter;

@Getter
public enum ActivityType {
    PAGE("page"),
    EXAM("exam"),
    FORUM("forum"),
    MATERIAL("material"),
    ONLINE_VIDEO("online_video"),
    WEB_LINK("web_link"),
    HOMEWORK("homework"),
    MIX_TASK("mix_task"),
    VOCABULARY("vocabulary");
    private final String typeName;

    ActivityType(String typeName) {
        this.typeName = typeName;
    }

    // 根据字符串获取枚举
    public static ActivityType fromTypeName(String typeName) {
        for (ActivityType type : values()) {
            if (type.typeName.equalsIgnoreCase(typeName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知类型: " + typeName);
    }
}
