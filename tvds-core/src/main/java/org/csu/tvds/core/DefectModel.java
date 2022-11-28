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

@CoreModel(env = RuntimeConfig.TORCH_ENV)
@Component
public class DefectModel extends ModelDispatcher<String, Boolean> {

    {
        modelPath = "/home/kwanho/Workspace/Workspace-TVDS/TVDS-AI/tvds-ad/tvds-ad.py";
        template = new Template(RuntimeConfig.TORCH_ENV + " " + modelPath + " {0}");
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
            template.setValues(new String[]{inputImage});
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
