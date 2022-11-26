package org.csu.tvds.core;

import org.csu.tvds.core.abs.Input;

public class SingleInput<T> extends Input<T> {
    @Override
    public T getInput() {
        return this.data;
    }

    @Override
    public void setInput(T data) {
        this.data = data;
    }
}
