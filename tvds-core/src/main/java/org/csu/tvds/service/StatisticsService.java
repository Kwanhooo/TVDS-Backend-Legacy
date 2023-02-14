package org.csu.tvds.service;

import org.csu.tvds.dto.vo.CarriageStatsVO;
import org.csu.tvds.dto.vo.DetectStatsVO;
import org.csu.tvds.dto.vo.MissionStatsVO;

import java.util.List;

public interface StatisticsService {
    CarriageStatsVO getCarriageStats();

    List<MissionStatsVO> getMissionStats();
    DetectStatsVO getDetectStats();
}
