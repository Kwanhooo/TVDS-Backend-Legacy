package org.csu.tvds.core.abs;

public abstract class Input<T> {
    public T data;

    public abstract T getInput();

    public abstract void setInput(T data);

    public Input(T data) {
        this.data = data;
    }
}
