package org.csu.tvds.core;

import org.csu.tvds.core.abs.Output;

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

