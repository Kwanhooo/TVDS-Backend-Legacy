package org.csu.tvds.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.csu.tvds.service.TvdsPartService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/image")
public class PartController extends BaseController {
    @Resource
    private TvdsPartService tvdsPartService;

    @RequestMapping("/getDateTree")
    public AjaxResult getDeptTree() {
        return success(tvdsPartService.generateDateTree());
    }

    @RequestMapping("/getAllImages")
    public AjaxResult getAllImages() {
        return success(tvdsPartService.getAllImages());
    }

    @RequestMapping("/getImagesByDate")
    public AjaxResult getImagesByDate(String date) {
        return success(tvdsPartService.getImagesByDate(date));
    }

    @RequestMapping("/getImagesByDateList")
    public AjaxResult getImagesByDateList(@RequestBody List<String> dates) {
        return success(tvdsPartService.getImagesByDateList(dates));
    }

    @RequestMapping("/detect")
    public AjaxResult detect(String imageID) {
        return success(tvdsPartService.detect(imageID));
    }
}
