package org.csu.tvds.core.abs;

import org.csu.tvds.core.io.Template;

public abstract class ModelDispatcher<I, O> {
    protected String modelPath;
    protected Template template;

    public abstract Output<O> dispatch(Input<I> input);
}
