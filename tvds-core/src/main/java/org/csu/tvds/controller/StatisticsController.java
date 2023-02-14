package org.csu.tvds.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.csu.tvds.service.StatisticsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/statistics")
public class StatisticsController extends BaseController {
    @Resource
    StatisticsService statisticsService;

    @RequestMapping("/carriageStats")
    public AjaxResult carriageStats() {
        return this.success(statisticsService.getCarriageStats());
    }

    @RequestMapping("/missionStats")
    public AjaxResult missionStats() {
        return this.success(statisticsService.getMissionStats());
    }

    @RequestMapping("/detectStats")
    public AjaxResult detectStats() {
        return this.success(statisticsService.getDetectStats());
    }
}
