package org.csu.tvds.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.csu.tvds.service.TvdsCarriageService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("carriage")
public class CarriageController extends BaseController {
    @Resource
    TvdsCarriageService tvdsCarriageService;

    @RequestMapping("/getDateTree")
    public AjaxResult getDeptTree() {
        return success(tvdsCarriageService.generateDateTree());
    }

    @RequestMapping("/getAllImages")
    public AjaxResult getAllImages() {
        return success(tvdsCarriageService.getAllImages());
    }

    @RequestMapping("/getImagesByDate")
    public AjaxResult getImagesByDate(String date) {
        return success(tvdsCarriageService.getImagesByDate(date));
    }

    @RequestMapping("/getImagesByDateList")
    public AjaxResult getImagesByDateList(@RequestBody List<String> dates) {
        return success(tvdsCarriageService.getImagesByDateList(dates));
    }

    @RequestMapping("/ocr")
    public AjaxResult ocr(String imageID) {
        return success(tvdsCarriageService.ocr(imageID));
    }

    @RequestMapping("/align")
    public AjaxResult align(String imageID) {
        return success(tvdsCarriageService.align(imageID));
    }

    @RequestMapping("/crop")
    public AjaxResult crop(String imageID) {
        return success(tvdsCarriageService.crop(imageID));
    }
}
