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


@CoreModel(env = RuntimeConfig.TENSORFLOW_ENV)
@Component
public class OCRModel extends ModelDispatcher<String, String> {

    {
        modelPath = AI_BASE + "tvds-ocr/utils.py";
        template = new Template(RuntimeConfig.TENSORFLOW_ENV + " " + modelPath + " {0}");
    }

    @Override
    public Output<String> dispatch(Input<String> input) {
        SingleOutput<String> output = new SingleOutput<>();
        output.setOutput(null);
        if (!(input instanceof SingleInput)) {
            output.setSucceed(false);
            return output;
        }
        String imagePath = input.getInput();
        try {
            template.setValues(new String[]{imagePath});
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
                if (line != null && line.trim().length() > 0) {
                    output.setOutput(line);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }
}
