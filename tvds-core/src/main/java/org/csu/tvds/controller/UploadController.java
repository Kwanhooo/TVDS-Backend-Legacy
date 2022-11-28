package org.csu.tvds.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.csu.tvds.service.TvdsCarriageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/upload")
public class UploadController extends BaseController {
    @Resource
    private TvdsCarriageService tvdsCarriageService;

    @PostMapping("/carriage")
    public AjaxResult upload(@RequestParam("file") MultipartFile file) {
        return success(tvdsCarriageService.upload(file));
    }
}
