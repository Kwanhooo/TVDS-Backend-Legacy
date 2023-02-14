package org.csu.tvds.dto.vo;

import lombok.Data;

@Data
public class CarriageStatsVO {
    private long total;
    // 未检测数量
    private long undetected;
    // 检测中数量
    private long detecting;
    // 检测完成数量
    private long detected;
}
