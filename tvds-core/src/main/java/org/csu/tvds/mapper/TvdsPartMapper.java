package org.csu.tvds.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.csu.tvds.entity.TvdsPart;

import java.time.LocalDate;
import java.util.List;

/**
 * @author kwanho
 * @description 针对表【tvds_part】的数据库操作Mapper
 * @createDate 2022-11-26 23:24:58
 * @Entity org.csu.tvds.entity.TvdsPart
 */
public interface TvdsPartMapper extends BaseMapper<TvdsPart> {
    List<LocalDate> selectUniqueDate();
}




