package org.csu.tvds.core.abs;

import org.csu.tvds.core.Template;

public abstract class ModelDispatcher<I, O> {
    protected String modelPath;
    protected Template template;

    protected abstract Output<O> dispatch(Input<I> input);
}
