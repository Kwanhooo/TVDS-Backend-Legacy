package org.csu.tvds;

import org.csu.tvds.core.AlignModel;
import org.csu.tvds.core.CropModel;
import org.csu.tvds.core.DefectModel;
import org.csu.tvds.core.OCRModel;
import org.csu.tvds.core.abs.Input;
import org.csu.tvds.core.abs.ModelDispatcher;
import org.csu.tvds.core.abs.Output;
import org.csu.tvds.core.io.SingleInput;
import org.junit.jupiter.api.Test;

class TvdsCoreApplicationTests {
    @Test
    void testDefectRecognize() {
        ModelDispatcher<String, Boolean> defectRecognizes = new DefectModel();
        Input<String> input = new SingleInput<>("11451-2022-11-12-123.jpg");
        Output<Boolean> output = defectRecognizes.dispatch(input);
        System.out.println(output.isSucceed());
        System.out.println(output.getOutput());
    }

    @Test
    void testOCR() {
        ModelDispatcher<String, String> ocr = new OCRModel();
        Input<String> input = new SingleInput<>("3907/20220123001_2_2.jpg");
        Output<String> output = ocr.dispatch(input);
        System.out.println(output.isSucceed());
        System.out.println(output.getOutput());
    }

    @Test
    void testRegistration() {
        ModelDispatcher<String, Boolean> defectRecognizes = new AlignModel();
        Input<String> input = new SingleInput<>("20220123001_2_2.jpg");
        Output<Boolean> output = defectRecognizes.dispatch(input);
        System.out.println(output.isSucceed());
        System.out.println(output.getOutput());
    }

    @Test
    void testCrop() {
        ModelDispatcher<String, Boolean> defectRecognizes = new CropModel();
        Input<String> input = new SingleInput<>("/home/kwanho/Workspace/Workspace-TVDS/TVDS-Backend/blob/aligned/3907_2_1.jpg");
        Output<Boolean> output = defectRecognizes.dispatch(input);
        System.out.println(output.isSucceed());
        System.out.println(output.getOutput());
    }
}
