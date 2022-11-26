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

    String upload(MultipartFile file);
}
