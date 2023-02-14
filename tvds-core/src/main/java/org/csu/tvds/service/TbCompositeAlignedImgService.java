package org.csu.tvds.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.csu.tvds.dto.structure.YearNode;
import org.csu.tvds.entity.TbCompositeAlignedImg;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author kwanho
 */
public interface TbCompositeAlignedImgService extends IService<TbCompositeAlignedImg> {
    List<YearNode> generateDateTree();

    List<TbCompositeAlignedImg> getAllImages();

    List<TbCompositeAlignedImg> getImagesByDate(String date);

    List<TbCompositeAlignedImg> getImagesByDateList(List<String> dateList);

    TbCompositeAlignedImg upload(MultipartFile file);

    TbCompositeAlignedImg ocr(String id);

    TbCompositeAlignedImg align(String id);

    TbCompositeAlignedImg crop(String id);

//    TbCompositeAlignedImg marking(String id);
}
