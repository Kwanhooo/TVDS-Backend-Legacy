package org.csu.tvds.core;

import org.apache.commons.io.IOUtils;
import org.csu.tvds.config.RuntimeConfig;
import org.csu.tvds.core.abs.Input;
import org.csu.tvds.core.abs.ModelDispatcher;
import org.csu.tvds.core.abs.Output;
import org.csu.tvds.core.annotation.CoreModel;
import org.csu.tvds.core.io.SingleInput;
import org.csu.tvds.core.io.SingleOutput;
import org.csu.tvds.core.io.Template;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.csu.tvds.config.PathConfig.AI_BASE;

@CoreModel(env = RuntimeConfig.TORCH_ENV)
@Component
public class DefectModel extends ModelDispatcher<String, Boolean> {
    private static final String MODEL_PATH = AI_BASE + "tvds-ad/model/spring.tch";
    private static final String BEARING_NPY = AI_BASE + "tvds-ad/logs/bearing.npy";

    {
        modelPath = AI_BASE + "tvds-ad/tvds-ad.py";
        template = new Template(RuntimeConfig.TORCH_ENV + " " + modelPath + " {0} {1} {2}");
    }

    @Override
    public Output<Boolean> dispatch(Input<String> input) {
        SingleOutput<Boolean> output = new SingleOutput<>();
        if (!(input instanceof SingleInput)) {
            output.setSucceed(false);
            output.setOutput(null);
            return output;
        }
        String inputImage = input.getInput();
        output.setOutput(false);
        try {
            template.setValues(new String[]{inputImage, MODEL_PATH, BEARING_NPY});
            System.out.println(template.resolve());
            Process proc = Runtime.getRuntime().exec(template.resolve());
            InputStream inputStream = proc.getInputStream();
            List<String> lines = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
            inputStream.close();
            if (lines.size() == 0) {
                output.setOutput(null);
                return output;
            }
            output.setSucceed(true);
            lines.forEach(line -> {
                if (line.contains("DEFECT")) {
                    output.setOutput(true);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }
}
