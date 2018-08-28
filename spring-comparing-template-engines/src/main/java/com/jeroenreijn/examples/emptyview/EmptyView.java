package com.jeroenreijn.examples.emptyview;

import org.springframework.web.servlet.view.AbstractTemplateView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

public class EmptyView extends AbstractTemplateView {

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        PrintWriter pw = httpServletResponse.getWriter();

        pw.write("");
        pw.flush();
    }
}
