package org.csu.tvds.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.csu.tvds.dto.structure.YearNode;
import org.csu.tvds.entity.TvdsCarriage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author kwanho
 */
public interface TvdsCarriageService extends IService<TvdsCarriage> {
    List<YearNode> generateDateTree();

    List<TvdsCarriage> getAllImages();

    List<TvdsCarriage> getImagesByDate(String date);

    List<TvdsCarriage> getImagesByDateList(List<String> dateList);

    TvdsCarriage upload(MultipartFile file);

    TvdsCarriage ocr(String imageID);

    TvdsCarriage align(String imageID);

    TvdsCarriage crop(String imageID);

    TvdsCarriage marking(String imageID);
}
