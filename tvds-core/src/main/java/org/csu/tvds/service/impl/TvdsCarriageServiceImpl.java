package org.csu.tvds.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ErrorCode;
import com.ruoyi.common.exception.ServiceException;
import org.apache.commons.io.FileUtils;
import org.csu.tvds.common.PathConstant;
import org.csu.tvds.dto.structure.DayNode;
import org.csu.tvds.dto.structure.MonthNode;
import org.csu.tvds.dto.structure.YearNode;
import org.csu.tvds.entity.TvdsCarriage;
import org.csu.tvds.mapper.TvdsCarriageMapper;
import org.csu.tvds.service.TvdsCarriageService;
import org.csu.tvds.util.NumberParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kwanho
 */
@Service
public class TvdsCarriageServiceImpl extends ServiceImpl<TvdsCarriageMapper, TvdsCarriage>
        implements TvdsCarriageService {
    @Resource
    TvdsCarriageMapper tvdsCarriageMapper;

    @Resource
    private NumberParser numberParser;

    @Override
    public List<YearNode> generateDateTree() {
        List<LocalDate> dates = tvdsCarriageMapper.selectUniqueDate();
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
    public List<TvdsCarriage> getAllImages() {
        return this.list();
    }

    @Override
    public List<TvdsCarriage> getImagesByDate(String date) {
        if (date.length() < 10 || date.split("-").length < 2) {
            throw new ServiceException(ErrorCode.PARAMS_ERROR);
        }
        String[] split = date.split("-");
        String year = split[0];
        String month = split[1];
        String day = split[2];
        LocalDate targetDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        return this.list(new QueryWrapper<TvdsCarriage>().eq("time", targetDate));
    }

    @Override
    public List<TvdsCarriage> getImagesByDateList(List<String> dateList) {
        List<TvdsCarriage> resultImages = new ArrayList<>();
        dateList.forEach(date -> {
            List<TvdsCarriage> imagesByDate = getImagesByDate(date);
            resultImages.addAll(imagesByDate);
        });
        return resultImages;
    }

    @Override
    public String upload(MultipartFile file) {
        String url;
        try {
            String filename = file.getOriginalFilename();
            FileUtils.writeByteArrayToFile(new File(PathConstant.BASE + filename), file.getBytes());
            assert filename != null;
            String[] split = filename.split("-");
            String inspection = split[0];
            String year = split[1];
            String month = split[2];
            String day = split[3];
            String imageID = filename.split("\\.")[0];
            LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
            TvdsCarriage carriage = new TvdsCarriage();
            carriage.setCarriageNo(null);
            carriage.setInspection(Integer.valueOf(inspection));
            carriage.setImageID(imageID);
            carriage.setTime(date);
            carriage.setStatus(0);

            url = PathConstant.URL_BASE + filename;
            carriage.setImageUrl(url);
            this.save(carriage);
        } catch (IOException e) {
            throw new ServiceException(ErrorCode.SYSTEM_ERROR);
        }
        return url;
    }
}




