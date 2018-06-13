package com.jeroenreijn.examples.htmlApiFaster;

import org.springframework.web.servlet.view.AbstractTemplateViewResolver;

public class HtmlApiFasterViewResolver extends AbstractTemplateViewResolver {

    public HtmlApiFasterViewResolver() {
        this.setViewClass(this.requiredViewClass());
    }

    protected Class<?> requiredViewClass() {
        return HtmlApiFasterView.class;
    }

}
