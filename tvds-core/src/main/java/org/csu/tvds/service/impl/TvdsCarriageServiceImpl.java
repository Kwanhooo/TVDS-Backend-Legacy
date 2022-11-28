package org.csu.tvds.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ErrorCode;
import com.ruoyi.common.exception.ServiceException;
import org.apache.commons.io.FileUtils;
import org.csu.tvds.common.CarriageConstant;
import org.csu.tvds.common.PartConstant;
import org.csu.tvds.config.PathConfig;
import org.csu.tvds.core.AlignModel;
import org.csu.tvds.core.CropModel;
import org.csu.tvds.core.OCRModel;
import org.csu.tvds.core.abs.Input;
import org.csu.tvds.core.abs.Output;
import org.csu.tvds.core.io.SingleInput;
import org.csu.tvds.dto.structure.DayNode;
import org.csu.tvds.dto.structure.MonthNode;
import org.csu.tvds.dto.structure.YearNode;
import org.csu.tvds.entity.TvdsCarriage;
import org.csu.tvds.entity.TvdsPart;
import org.csu.tvds.mapper.TvdsCarriageMapper;
import org.csu.tvds.mapper.TvdsPartMapper;
import org.csu.tvds.service.TvdsCarriageService;
import org.csu.tvds.util.NumberParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.csu.tvds.config.PathConfig.BASE;

/**
 * @author kwanho
 */
@Service
public class TvdsCarriageServiceImpl extends ServiceImpl<TvdsCarriageMapper, TvdsCarriage> implements TvdsCarriageService {
    @Resource
    private TvdsCarriageMapper tvdsCarriageMapper;

    @Resource
    private TvdsPartMapper tvdsPartMapper;

    @Resource
    private NumberParser numberParser;

    @Resource
    private OCRModel ocrModel;

    @Resource
    private AlignModel imageRegistration;

    @Resource
    private CropModel cropModel;


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
        List<TvdsCarriage> carriages = this.list(new QueryWrapper<TvdsCarriage>().eq("time", targetDate));
        carriages.forEach(carriage -> {
            carriage.setOriginUrl(carriage.getOriginUrl());
        });

        return carriages;
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
    public TvdsCarriage upload(MultipartFile file) {
        String filename = file.getOriginalFilename();
        assert filename != null;
        // inspection_20221128xxx_seat_carriageNo.jpg
        // 分割出信息
        String[] meta = filename.split("_");
        if (meta.length < 4) {
            throw new ServiceException(ErrorCode.PARAMS_ERROR);
        }
        String inspection = meta[0];
        String dateAndNo = meta[1];
        int year = Integer.parseInt(dateAndNo.substring(0, 4));
        int month = Integer.parseInt(dateAndNo.substring(4, 6));
        int day = Integer.parseInt(dateAndNo.substring(6, 8));
        String seat = meta[2];
        String carriageNo = meta[3].split("\\.")[0];
        // 扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        // 生成新的文件名
        String newFilename = dateAndNo + "_" + seat + "_" + carriageNo + extension;
        // inspection这一目录如果不存在，则创建
        File inspectionDir = new File(PathConfig.UPLOAD_BASE + inspection);
        if (!inspectionDir.exists()) {
            boolean mkdir = inspectionDir.mkdirs();
            if (!mkdir) {
                throw new ServiceException(ErrorCode.FILE_UPLOAD_ERROR);
            }
        }
        // 保存文件
        File dest = new File(PathConfig.UPLOAD_BASE + inspection + "/" + newFilename);
        try {
            file.transferTo(dest);
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.FILE_UPLOAD_ERROR);
        }
        // 保存到数据库
        TvdsCarriage tvdsCarriage = new TvdsCarriage();
        tvdsCarriage.setImageID(filename.split("\\.")[0]);
        tvdsCarriage.setOriginUrl("origin/" + inspection + "/" + newFilename);
        tvdsCarriage.setStatus(CarriageConstant.UPLOAD_OK);
        tvdsCarriage.setInspection(Integer.valueOf(inspection));
        tvdsCarriage.setTime(LocalDate.of(year, month, day));
        tvdsCarriage.setSeat(Integer.valueOf(seat));
        tvdsCarriage.setCarriageNo(Integer.valueOf(carriageNo));
        tvdsCarriage.setModel(null);
        tvdsCarriage.setCarriageID(null);
        try {
            this.save(tvdsCarriage);
        } catch (Exception e) {
            throw new ServiceException("已存在相同的图片", ErrorCode.FILE_UPLOAD_ERROR.getCode());
        }
        return tvdsCarriage;
    }

    public TvdsCarriage ocr(String imageID) {
        TvdsCarriage carriageToOcr = this.getOne(new QueryWrapper<TvdsCarriage>().eq("imageID", imageID));
        if (carriageToOcr == null) {
            throw new ServiceException(ErrorCode.PARAMS_ERROR);
        }
        String imageUrl = carriageToOcr.getOriginUrl();
        Input<String> inputPath = new SingleInput<>(BASE + imageUrl);
        System.out.println(inputPath);
        Output<String> output = ocrModel.dispatch(inputPath);
        if (!output.isSucceed()) {
            throw new ServiceException(ErrorCode.MODEL_RUN_ERROR);
        }
        String result = output.getOutput();
        String model = result.split("_")[4];
        String carriageID = result.split("_")[5];
        carriageToOcr.setStatus(CarriageConstant.OCR_OK);
        carriageToOcr.setModel(model);
        carriageToOcr.setCarriageID(Integer.valueOf(carriageID));
        this.updateById(carriageToOcr);
        return carriageToOcr;
    }

    @Override
    public TvdsCarriage align(String imageID) {
        TvdsCarriage carriageToAlign = this.getOne(new QueryWrapper<TvdsCarriage>().eq("imageID", imageID));
        if (carriageToAlign == null) {
            throw new ServiceException(ErrorCode.PARAMS_ERROR);
        }
        String imageUrl = carriageToAlign.getOriginUrl();
        Input<String> inputPath = new SingleInput<>(BASE + imageUrl);
        Output<Boolean> output = imageRegistration.dispatch(inputPath);
        if (!output.isSucceed()) {
            throw new ServiceException(ErrorCode.MODEL_RUN_ERROR);
        }
        Boolean result = output.getOutput();
        if (!result) {
            throw new ServiceException("配准失败", ErrorCode.MODEL_RESULT_INVALID.getCode());
        }
        String alignFilename = carriageToAlign.getInspection() + "_" + carriageToAlign.getSeat() + "_" + carriageToAlign.getCarriageNo() + ".jpg";
        carriageToAlign.setStatus(CarriageConstant.ALIGN_OK);
        carriageToAlign.setAlignedUrl("aligned/" + alignFilename);
        this.updateById(carriageToAlign);
        return carriageToAlign;
    }

    @Override
    public TvdsCarriage crop(String imageID) {
        TvdsCarriage carriageToCrop = this.getOne(new QueryWrapper<TvdsCarriage>().eq("imageID", imageID));
        if (carriageToCrop == null) {
            throw new ServiceException(ErrorCode.PARAMS_ERROR);
        }
        String imageUrl = carriageToCrop.getAlignedUrl();
        if (imageUrl == null || Integer.compare(carriageToCrop.getStatus(), CarriageConstant.ALIGN_OK) != 0) {
            throw new ServiceException("请先进行配准操作", ErrorCode.WORKFLOW_ERROR.getCode());
        }
        Input<String> inputPath = new SingleInput<>(BASE + imageUrl);
        Output<Boolean> output = cropModel.dispatch(inputPath);
        if (!output.isSucceed()) {
            throw new ServiceException(ErrorCode.MODEL_RUN_ERROR);
        }
        Boolean result = output.getOutput();
        if (!result) {
            throw new ServiceException("切割失败", ErrorCode.MODEL_RESULT_INVALID.getCode());
        }
        String dir = carriageToCrop.getInspection() + "_" + carriageToCrop.getSeat() + "_" + carriageToCrop.getCarriageNo();
        String outputDir = PathConfig.PARTS_BASE + dir;
        File outputDirFile = new File(outputDir);
        // 遍历这个目录下的所有文件
        FileUtils.iterateFiles(outputDirFile, null, true).forEachRemaining(file -> {
            TvdsPart part = new TvdsPart();
            part.setImageID(dir + "_" + file.getName().split("\\.")[0]);
            part.setImageUrl("parts/" + dir + "/" + file.getName());
            part.setStatus(PartConstant.UNDETECTED);
            part.setInspection(carriageToCrop.getInspection());
            part.setTime(carriageToCrop.getTime());
            part.setSeat(carriageToCrop.getSeat());
            part.setCarriageNo(carriageToCrop.getCarriageNo());
            part.setCarriageID(carriageToCrop.getCarriageID());
            tvdsPartMapper.insert(part);
        });
        carriageToCrop.setStatus(CarriageConstant.CROP_OK);
        this.updateById(carriageToCrop);
        return carriageToCrop;
    }
}




