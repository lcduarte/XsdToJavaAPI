package com.jeroenreijn.examples.htmlApi;

import org.springframework.web.servlet.view.AbstractTemplateViewResolver;

public class HtmlApiViewResolver extends AbstractTemplateViewResolver {

    public HtmlApiViewResolver() {
        this.setViewClass(this.requiredViewClass());
    }

    protected Class<?> requiredViewClass() {
        return HtmlApiView.class;
    }

}
