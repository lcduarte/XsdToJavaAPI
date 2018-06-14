package com.jeroenreijn.examples.htmlApi;

import com.jeroenreijn.examples.model.Presentation;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.xmlet.htmlapi.EnumHttpEquivMeta;
import org.xmlet.htmlapi.EnumMediaMediaType;
import org.xmlet.htmlapi.EnumRelLinkType;
import org.xmlet.htmlapi.Html;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class HtmlApiView extends AbstractTemplateView {

    private static Html<Html> documentRoot;

    static {
        documentRoot = new Html<>();

        documentRoot.
                head()
                    .meta().attrCharset("utf-8").º()
                    .meta().attrName("viewport").attrContent("width=device-width, initial-scale=1.0").º()
                    .meta().attrHttpEquiv(EnumHttpEquivMeta.CONTENT_LANGUAGE).attrContent("IE=Edge").º()
                    .title().text("JFall 2013 Presentations - htmlApi").º()
                    .link().attrRel(EnumRelLinkType.STYLESHEET).attrHref("/webjars/bootstrap/3.3.7-1/css/bootstrap.min.css").attrMedia(EnumMediaMediaType.SCREEN).º().º()
                .body()
                    .div().attrClass("container")
                        .div().attrClass("page-header")
                            .h1().text("JFall 2013 Presentations - htmlApi").º().º()
                        .<Iterable<Presentation>>binder( (elem, presentations) ->
                                    presentations.forEach(presentation ->
                                        elem.div().attrClass("panel panel-default")
                                                .div().attrClass("panel-heading")
                                                    .h3().attrClass("panel-title").text(presentation.getTitle() + " - " + presentation.getSpeakerName()).º().º()
                                                .div().attrClass("panel-body").text(presentation.getSummary()).º().º()
                )).º()
                .script().attrSrc("/webjars/jquery/3.1.1/jquery.min.js").º()
                .script().attrSrc("/webjars/bootstrap/3.3.7-1/js/bootstrap.min.js");
    }

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        Iterable<Presentation> presentations = (Iterable<Presentation>) map.get("presentations");
        CustomVisitor<Iterable<Presentation>> visitor = new CustomVisitor<>(httpServletResponse.getWriter(), presentations);

        documentRoot.accept(visitor);

        //System.out.println("Normal" + System.currentTimeMillis());

        //visitor.performPrint();
    }
}
