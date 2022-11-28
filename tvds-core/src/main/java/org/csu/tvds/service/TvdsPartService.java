package org.csu.tvds.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.csu.tvds.dto.structure.YearNode;
import org.csu.tvds.entity.TvdsPart;

import java.util.List;

/**
 * @author kwanho
 */
public interface TvdsPartService extends IService<TvdsPart> {
    List<YearNode> generateDateTree();

    List<TvdsPart> getImagesByDate(String date);

    List<TvdsPart> getImagesByDateList(List<String> dateList);

    List<TvdsPart> getAllImages();

    TvdsPart detect(String imageID);
}
