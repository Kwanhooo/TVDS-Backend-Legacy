package org.csu.tvds.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ErrorCode;
import com.ruoyi.common.exception.ServiceException;
import org.csu.tvds.dto.structure.DayNode;
import org.csu.tvds.dto.structure.MonthNode;
import org.csu.tvds.dto.structure.YearNode;
import org.csu.tvds.entity.TbCompositeAlignedImg;
import org.csu.tvds.mapper.TbCompositeAlignedImgMapper;
import org.csu.tvds.service.TbCompositeAlignedImgService;
import org.csu.tvds.util.NumberParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kwanho
 * @description 针对表【tb_composite_aligned_img】的数据库操作Service实现
 * @createDate 2023-02-11 21:23:41
 */
@Service
public class TbCompositeAlignedImgServiceImpl extends ServiceImpl<TbCompositeAlignedImgMapper, TbCompositeAlignedImg>
        implements TbCompositeAlignedImgService {
    @Resource
    private TbCompositeAlignedImgMapper compositeAlignedImgMapper;

    @Resource
    private NumberParser numberParser;


    @Override
    public List<YearNode> generateDateTree() {
        List<LocalDate> dates = compositeAlignedImgMapper.selectUniqueDate();
        List<YearNode> tree = new ArrayList<>();
        dates.forEach(date -> {
            int year = date.getYear();
            boolean hasYear = false;
            for (YearNode yearNode : tree) {
                if (yearNode.label == year) {
                    hasYear = true;
                    boolean hasMonth = false;
                    for (MonthNode monthNode : yearNode.children) {
                        if (monthNode.label == date.getMonthValue()) {
                            hasMonth = true;
                            monthNode.children.add(new DayNode(monthNode.id + numberParser.parseTwoDigits(date.getDayOfMonth()), date.getDayOfMonth()));
                            break;
                        }
                    }
                    if (!hasMonth) {
                        MonthNode monthNode = new MonthNode(yearNode.id + numberParser.parseTwoDigits(date.getMonthValue()), date.getMonthValue());
                        monthNode.children.add(new DayNode(monthNode.id + numberParser.parseTwoDigits(date.getDayOfMonth()), date.getDayOfMonth()));
                        yearNode.children.add(monthNode);
                    }
                    break;
                }
            }
            if (!hasYear) {
                YearNode yearNode = new YearNode(String.valueOf(year), year);
                MonthNode monthNode = new MonthNode(yearNode.id + numberParser.parseTwoDigits(date.getMonthValue()), date.getMonthValue());
                monthNode.children.add(new DayNode(monthNode.id + numberParser.parseTwoDigits(date.getDayOfMonth()), date.getDayOfMonth()));
                yearNode.children.add(monthNode);
                tree.add(yearNode);
            }
        });
        return tree;
    }

    @Override
    public List<TbCompositeAlignedImg> getAllImages() {
        return this.list();
    }

    @Override
    public List<TbCompositeAlignedImg> getImagesByDate(String date) {
        if (date.length() < 10 || date.split("-").length < 2) {
            throw new ServiceException(ErrorCode.PARAMS_ERROR);
        }
        String[] split = date.split("-");
        String year = split[0];
        String month = split[1];
        String day = split[2];
        LocalDate targetDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        return this.list(new QueryWrapper<TbCompositeAlignedImg>().eq("createTime", targetDate));
    }

    @Override
    public List<TbCompositeAlignedImg> getImagesByDateList(List<String> dateList) {
        List<TbCompositeAlignedImg> resultImages = new ArrayList<>();
        dateList.forEach(date -> {
            List<TbCompositeAlignedImg> imagesByDate = getImagesByDate(date);
            resultImages.addAll(imagesByDate);
        });
        return resultImages;
    }

    @Override
    public TbCompositeAlignedImg upload(MultipartFile file) {
        return null;
    }

    @Override
    public TbCompositeAlignedImg ocr(String id) {
        return null;
    }

    @Override
    public TbCompositeAlignedImg align(String id) {
        return null;
    }

    @Override
    public TbCompositeAlignedImg crop(String id) {
        return null;
    }
}




