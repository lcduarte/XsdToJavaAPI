package com.jeroenreijn.examples.emptyview;

import com.jeroenreijn.examples.htmlApi.HtmlApiView;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;

public class EmptyViewResolver extends AbstractTemplateViewResolver {

    public EmptyViewResolver() {
        this.setViewClass(this.requiredViewClass());
    }

    protected Class<?> requiredViewClass() {
        return EmptyView.class;
    }
}
