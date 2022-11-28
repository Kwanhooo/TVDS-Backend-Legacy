package org.csu.tvds.core.io;

import org.csu.tvds.core.abs.Output;
import org.csu.tvds.core.annotation.CoreIO;

@CoreIO
public class SingleOutput<T> extends Output<T> {
    @Override
    public T getOutput() {
        return this.data;
    }

    @Override
    public void setOutput(T data) {
        this.data = data;
    }
}

