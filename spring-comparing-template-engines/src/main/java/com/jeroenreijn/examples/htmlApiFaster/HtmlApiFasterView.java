package com.jeroenreijn.examples.htmlApiFaster;

import com.jeroenreijn.examples.model.Presentation;
import htmlflow.HtmlView;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.xmlet.htmlapifaster.EnumHttpEquivMeta;
import org.xmlet.htmlapifaster.EnumMediaMediaType;
import org.xmlet.htmlapifaster.EnumRelLinkType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

public class HtmlApiFasterView extends AbstractTemplateView {

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        Iterable<Presentation> presentations = (Iterable<Presentation>) map.get("presentations");
        PrintWriter pw = httpServletResponse.getWriter();

        pw.write(presentationsTemplate(presentations).render());
        pw.flush();
    }

    private HtmlView presentationsTemplate(Iterable<Presentation> presentations){
        return HtmlView.html()
            .head()
                .meta().attrCharset("utf-8").º()
                .meta().attrName("viewport").attrContent("width=device-width, initial-scale=1.0").º()
                .meta().attrHttpEquiv(EnumHttpEquivMeta.CONTENT_LANGUAGE).attrContent("IE=Edge").º()
                .title().text("JFall 2013 Presentations - htmlApiFaster").º()
                .link().attrRel(EnumRelLinkType.STYLESHEET).attrHref("/webjars/bootstrap/3.3.7-1/css/bootstrap.min.css").attrMedia(EnumMediaMediaType.SCREEN).º()
            .º()
            .body()
                .div().attrClass("container")
                    .div().attrClass("page-header")
                        .h1().text("JFall 2013 Presentations - htmlApiFaster")
                    .º()
                .º()
                .of(containerDiv ->
                    presentations.forEach(presentation ->
                        containerDiv
                            .div().attrClass("panel panel-default")
                                .div().attrClass("panel-heading")
                                    .h3().attrClass("panel-title").text(presentation.getTitle() + " - " + presentation.getSpeakerName()).º()
                                .º()
                                .div().attrClass("panel-body").text(presentation.getSummary()).º()
                            .º()
                    )
                ).º()
                .script().attrSrc("/webjars/jquery/3.1.1/jquery.min.js").º()
                .script().attrSrc("/webjars/bootstrap/3.3.7-1/js/bootstrap.min.js").º()
            .º()
        .º();
    }
}
