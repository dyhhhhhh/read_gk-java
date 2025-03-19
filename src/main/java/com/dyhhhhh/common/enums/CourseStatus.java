package com.dyhhhhh.common.enums;

import lombok.Getter;

@Getter
public enum CourseStatus {
    /** 已结束的课程 */
    CLOSED(0, "已结束"),
    /** 进行中的课程 */
    ONGOING(1, "进行中");

    /**
     * -- GETTER --
     *  获取枚举对应的数值
     *
     * @return 状态值（0或1）
     */
    private final int value;
    /**
     * -- GETTER --
     *  获取状态描述
     *
     * @return 中文描述（如"已结束"）
     */
    private final String description;

    CourseStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 根据整数值获取枚举实例
     * @param value 状态值（0或1）
     * @return 对应的CourseStatus枚举
     * @throws IllegalArgumentException 当传入无效值时
     */
    public static CourseStatus fromValue(int value) {
        return switch (value) {
            case 0 -> CLOSED;
            case 1 -> ONGOING;
            default -> throw new IllegalArgumentException("无效课程状态值: " + value);
        };
    }

    /**
     * 是否为进行中状态
     */
    public boolean isOngoing() {
        return this == ONGOING;
    }

    /**
     * 是否为已结束状态
     */
    public boolean isClosed() {
        return this == CLOSED;
    }
}
