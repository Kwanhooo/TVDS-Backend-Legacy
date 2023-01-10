package org.csu.tvds.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.csu.tvds.entity.TvdsCarriage;
import org.csu.tvds.service.TvdsCarriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("carriage")
@Slf4j
public class CarriageController extends BaseController {
    @Resource
    TvdsCarriageService tvdsCarriageService;

    @Autowired
    RedisTemplate<Object, Object> redisTemplate;

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
        TvdsCarriage ocrResult;
        try {
            ocrResult = tvdsCarriageService.ocr(imageID);
        } catch (Exception e) {
            return error("OCR未能识别出结果");
        }
        return success(ocrResult);
    }

    @RequestMapping("/align")
    public AjaxResult align(String imageID) {
        return success(tvdsCarriageService.align(imageID));
    }

    @RequestMapping("/crop")
    public AjaxResult crop(String imageID) {
        return success(tvdsCarriageService.crop(imageID));
    }

    @Scheduled(cron = "*/5 * * * * ?")
    public void execute() {
        redisTemplate.opsForValue().set("heartbeat", "HEARTBEAT");
        System.out.println(redisTemplate.opsForValue().get("heartbeat"));
    }
}
