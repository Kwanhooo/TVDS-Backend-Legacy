package org.csu.tvds.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ErrorCode;
import com.ruoyi.common.exception.ServiceException;
import org.csu.tvds.dto.structure.DayNode;
import org.csu.tvds.dto.structure.MonthNode;
import org.csu.tvds.dto.structure.YearNode;
import org.csu.tvds.entity.TvdsPart;
import org.csu.tvds.mapper.TvdsPartMapper;
import org.csu.tvds.service.TvdsPartService;
import org.csu.tvds.util.NumberParser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kwanho
 */
@Service
public class TvdsPartServiceImpl extends ServiceImpl<TvdsPartMapper, TvdsPart>
        implements TvdsPartService {

    @Resource
    private NumberParser numberParser;

    /**
     * 生成日期树
     *
     * @return 日期树
     */
    @Override
    public List<YearNode> generateDateTree() {
        // 获得所有的日期
        List<LocalDate> dates = this.baseMapper.selectUniqueDate();
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
    public List<TvdsPart> getImagesByDate(String date) {
        if (date.length() < 10 || date.split("-").length < 2) {
            throw new ServiceException(ErrorCode.PARAMS_ERROR);
        }
        String[] split = date.split("-");
        String year = split[0];
        String month = split[1];
        String day = split[2];
        LocalDate targetDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        return this.list(new QueryWrapper<TvdsPart>().eq("time", targetDate));
    }

    @Override
    public List<TvdsPart> getImagesByDateList(List<String> dateList) {
        List<TvdsPart> resultImages = new ArrayList<>();
        dateList.forEach(date -> {
            List<TvdsPart> imagesByDate = getImagesByDate(date);
            resultImages.addAll(imagesByDate);
        });
        return resultImages;
    }

    @Override
    public List<TvdsPart> getAllImages() {
        return this.list();
    }
}




