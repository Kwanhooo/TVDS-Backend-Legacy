package org.csu.tvds.core.abs;

public abstract class Output<T> {
    protected T data;
    private boolean isSucceed;

    public abstract T getOutput();

    public abstract void setOutput(T data);

    public boolean isSucceed() {
        return isSucceed;
    }

    public void setSucceed(boolean succeed) {
        isSucceed = succeed;
    }
}
