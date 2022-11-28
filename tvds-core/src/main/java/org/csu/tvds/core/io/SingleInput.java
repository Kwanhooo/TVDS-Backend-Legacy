package org.csu.tvds.core.io;

import org.csu.tvds.core.abs.Input;
import org.csu.tvds.core.annotation.CoreIO;

@CoreIO
public class SingleInput<T> extends Input<T> {
    public SingleInput(T data) {
        super(data);
    }

    @Override
    public T getInput() {
        return this.data;
    }

    @Override
    public void setInput(T data) {
        this.data = data;
    }
}
