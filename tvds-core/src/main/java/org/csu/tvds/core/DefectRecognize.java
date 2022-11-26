package org.csu.tvds.core;

import org.csu.tvds.common.PathConstant;
import org.csu.tvds.core.abs.Input;
import org.csu.tvds.core.abs.ModelDispatcher;
import org.csu.tvds.core.abs.Output;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
public class DefectRecognize extends ModelDispatcher<String, Boolean> {
    {
        template = new Template("/home/kwanho/Environment/anaconda3/envs/Exp/bin/python /home/kwanho/Workspace/Workspace-TVDS/tvds-ad/tvds-ad.py " + "{0}");
    }

    @Override
    protected Output<Boolean> dispatch(Input<String> input) {
        SingleOutput<Boolean> output = new SingleOutput<>();
        if (!(input instanceof SingleInput)) {
            output.setSucceed(false);
            return output;
        }
        String imageID = input.getInput();
        output.setOutput(false);
        try {
            template.setValues(new String[]{PathConstant.BASE + imageID + ".jpg"});
            Process proc = Runtime.getRuntime().exec(template.resolve());
            InputStream is = proc.getInputStream();
            InputStreamReader reader = new InputStreamReader(proc.getInputStream());
            BufferedReader in = new BufferedReader(reader);
            String line = "";
            while ((line = in.readLine()) != null) {
                if (line.contains("DEFECT")) {
                    output.setOutput(true);
                }
            }
            in.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }
}
