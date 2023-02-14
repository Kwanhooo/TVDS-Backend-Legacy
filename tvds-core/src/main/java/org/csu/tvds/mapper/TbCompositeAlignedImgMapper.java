package org.csu.tvds.mapper;

import org.csu.tvds.entity.TbCompositeAlignedImg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.time.LocalDate;
import java.util.List;

/**
* @author kwanho
* @description 针对表【tb_composite_aligned_img】的数据库操作Mapper
* @createDate 2023-02-11 21:23:41
* @Entity org.csu.tvds.entity.TbCompositeAlignedImg
*/
public interface TbCompositeAlignedImgMapper extends BaseMapper<TbCompositeAlignedImg> {

    List<LocalDate> selectUniqueDate();
}




