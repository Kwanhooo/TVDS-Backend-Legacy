package org.csu.tvds.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.framework.web.service.TokenService;
import org.apache.commons.io.FileUtils;
import org.csu.tvds.config.PathConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/blob")
@CrossOrigin
public class BlobController extends BaseController {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private RedisCache redisCache;

    @RequestMapping("/get")
    public ResponseEntity<byte[]> get(String path) {
        String localPath = PathConfig.BASE + path;
        File fileToReturn = new File(localPath);
        if (!fileToReturn.exists()) {
            return null;
        }
        byte[] bytes = new byte[0];
        try {
            bytes = FileUtils.readFileToByteArray(fileToReturn);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @RequestMapping("/getUrl")
    public AjaxResult getUrl(String path) {
        System.out.println(path);
        String localPath = PathConfig.BASE + path;
        File fileToReturn = new File(localPath);
        System.out.println(fileToReturn.getName());
        String targetPath = Objects.requireNonNull(BlobController.class.getResource("/static")).getPath();
        System.out.println(targetPath);
        // 把fileToReturn拷贝到classpath:/cache下
        try {
            FileUtils.copyFileToDirectory(fileToReturn, new File(targetPath));
            System.out.println("拷贝成功");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this.success("http://10.26.101.106:14514/cache/" + fileToReturn.getName());
    }
}
