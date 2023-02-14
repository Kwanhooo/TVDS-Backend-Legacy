package org.csu.tvds.common;

import org.csu.tvds.dto.vo.MissionStatsVO;

import java.util.ArrayList;
import java.util.List;

public class MissionCache {
    static {
        missions = new ArrayList<>();
    }

    public static List<MissionStatsVO> missions;
}
