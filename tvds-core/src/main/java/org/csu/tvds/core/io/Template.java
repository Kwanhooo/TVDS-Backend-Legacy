package org.csu.tvds.core.io;

import org.csu.tvds.core.annotation.CoreIO;

@CoreIO
public class Template {
    protected String template;
    protected String[] values;

    public Template(String template) {
        this.template = template;
    }

    public Template(String template, String... values) {
        this.template = template;
        this.values = values;
    }

    public String resolve() {
        String result = template;
        for (int i = 0; i < values.length; i++) {
            result = result.replace("{" + i + "}", values[i]);
        }
        return result;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }
}
