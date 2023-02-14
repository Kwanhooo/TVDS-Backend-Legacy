package org.csu.tvds.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.csu.tvds.entity.TvdsCarriage;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * @author kwanho
 */
@Repository
public interface TvdsCarriageMapper extends BaseMapper<TvdsCarriage> {

    List<LocalDate> selectUniqueDate();
    Integer selectUniqueInspectionCount();
    List<Integer> selectUniqueInspection();
}




