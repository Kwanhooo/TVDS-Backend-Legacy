package org.csu.tvds.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MissionStatsVO {
    private int status;
    private int inspection;
    private int carriageNo;
}
