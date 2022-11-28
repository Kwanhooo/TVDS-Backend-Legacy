package org.csu.tvds.core;

import org.apache.commons.io.IOUtils;
import org.csu.tvds.config.PathConfig;
import org.csu.tvds.config.RuntimeConfig;
import org.csu.tvds.core.abs.Input;
import org.csu.tvds.core.abs.ModelDispatcher;
import org.csu.tvds.core.abs.Output;
import org.csu.tvds.core.annotation.CoreModel;
import org.csu.tvds.core.io.SingleInput;
import org.csu.tvds.core.io.SingleOutput;
import org.csu.tvds.core.io.Template;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.csu.tvds.config.RuntimeConfig.TENSORFLOW_ENV;

@CoreModel(env = RuntimeConfig.TENSORFLOW_ENV)
@Component
public class CropModel extends ModelDispatcher<String, Boolean> {
    private static final String OUTPUT_PATH = PathConfig.PARTS_BASE;
    private static final String jsonPath = "/home/kwanho/Workspace/Workspace-TVDS/TVDS-AI/tvds-registration/images/template/X70/part_index.json";

    {
        modelPath = "/home/kwanho/Workspace/Workspace-TVDS/TVDS-AI/tvds-registration/utils.py";
        template = new Template(TENSORFLOW_ENV + " " + modelPath + " {0} {1} {2}");
    }

    @Override
    public Output<Boolean> dispatch(Input<String> input) {
        SingleOutput<Boolean> output = new SingleOutput<>();
        output.setOutput(null);
        if (!(input instanceof SingleInput)) {
            output.setSucceed(false);
            return output;
        }
        String imagePath = input.getInput();
        File file = new File(imagePath);
        if (!file.exists()) {
            System.out.println("Image file not found: " + imagePath);
            output.setSucceed(false);
            return output;
        }
        try {
            template.setValues(new String[]{imagePath, OUTPUT_PATH, jsonPath});
            System.out.println(template.resolve());
            Process proc = Runtime.getRuntime().exec(template.resolve());
            InputStream inputStream = proc.getInputStream();
            List<String> lines = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
            inputStream.close();
            if (lines.size() == 0) {
                output.setSucceed(false);
                return output;
            }
            output.setSucceed(true);
            lines.forEach(line -> {
                if (line != null && line.trim().equals("True")) {
                    output.setOutput(true);
                }
            });
            if (output.getOutput() == null) {
                output.setOutput(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }
}
