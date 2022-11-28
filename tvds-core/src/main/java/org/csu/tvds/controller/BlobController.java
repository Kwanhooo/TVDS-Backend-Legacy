package org.csu.tvds.controller;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.framework.web.service.TokenService;
import io.jsonwebtoken.Claims;
import org.apache.commons.io.FileUtils;
import org.csu.tvds.config.PathConfig;
import org.csu.tvds.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/blob")
@CrossOrigin
public class BlobController extends BaseController {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private RedisCache redisCache;

    @RequestMapping("/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable("filename") String filename, String token) throws IOException {
        if (SecurityConfig.BLOB_AUTH) {
            try {
                Claims claims = tokenService.parseToken(token);
                String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
                String userKey = tokenService.getTokenKey(uuid);
                LoginUser user = redisCache.getCacheObject(userKey);
                if (user == null) {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }
        Path path = Paths.get(PathConfig.BASE + filename);
        System.out.println(path.toFile().exists());
        byte[] bytes = FileUtils.readFileToByteArray(path.toFile());
        HttpHeaders headers = new HttpHeaders();
//        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

}
