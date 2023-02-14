package org.csu.tvds.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.csu.tvds.common.PartConstant;
import org.csu.tvds.dto.vo.CarriageStatsVO;
import org.csu.tvds.dto.vo.DetectStatsVO;
import org.csu.tvds.dto.vo.MissionStatsVO;
import org.csu.tvds.entity.TvdsPart;
import org.csu.tvds.mapper.TvdsCarriageMapper;
import org.csu.tvds.mapper.TvdsPartMapper;
import org.csu.tvds.service.StatisticsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.csu.tvds.common.MissionCache.missions;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Resource
    private TvdsCarriageMapper tvdsCarriageMapper;
    @Resource
    private TvdsPartMapper tvdsPartMapper;

    @Override
    public CarriageStatsVO getCarriageStats() {
        List<Integer> uniqueInspection = tvdsCarriageMapper.selectUniqueInspection();
        List<Integer> uniqueDetectedInspection = tvdsPartMapper.selectUniqueDetectedInspection();
        CarriageStatsVO carriageStatsVO = new CarriageStatsVO();
        carriageStatsVO.setTotal(uniqueInspection.size());
        uniqueInspection.removeAll(uniqueDetectedInspection);
        carriageStatsVO.setUndetected(uniqueInspection.size());
        carriageStatsVO.setDetected(uniqueDetectedInspection.size());
        carriageStatsVO.setDetecting(missions.size());
        return carriageStatsVO;
    }

    @Override
    public List<MissionStatsVO> getMissionStats() {
        List<MissionStatsVO> result = new ArrayList<>();
        List<TvdsPart> parts = tvdsPartMapper.selectList
                (
                        new QueryWrapper<TvdsPart>()
                                .ne("status", PartConstant.UNDETECTED)
                                .orderByDesc("updateTime")
                                .last("limit 5")
                );

        missions.forEach(mission -> {
            MissionStatsVO vo = new MissionStatsVO();
            vo.setCarriageNo(mission.getCarriageNo());
            vo.setInspection(mission.getInspection());
            vo.setStatus(PartConstant.UNDETECTED);
            result.add(vo);
        });

        parts.forEach(part -> {
            MissionStatsVO vo = new MissionStatsVO();
            vo.setCarriageNo(part.getCarriageNo());
            vo.setInspection(part.getInspection());
            vo.setStatus(part.getStatus());
            result.add(vo);
        });
        return result;
    }

    @Override
    public DetectStatsVO getDetectStats() {
        DetectStatsVO detectStatsVO = new DetectStatsVO();
        detectStatsVO.setTotalDefects(tvdsPartMapper.selectUniqueDefectCount());
        return detectStatsVO;
    }
}
