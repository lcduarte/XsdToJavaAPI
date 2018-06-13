package com.jeroenreijn.examples.htmlApiFaster;

import com.jeroenreijn.examples.model.Presentation;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.xmlet.htmlapifaster.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.Object;
import java.util.Map;

public class HtmlApiFasterView extends AbstractTemplateView {

    private Html<Html> documentRoot;

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        Iterable<Presentation> presentations = (Iterable<Presentation>) map.get("presentations");
        FasterCustomVisitor visitor = new FasterCustomVisitor(httpServletResponse.getWriter());

        documentRoot = new Html<>(visitor);
        presentationsTemplate(presentations);

        visitor.printToWriter(documentRoot);

        //System.out.println("Faster" + System.currentTimeMillis());

        //httpServletResponse.getWriter().write("This doesn't make any sense.");
    }

    private void presentationsTemplate(Iterable<Presentation> presentations){
        Head<Html<Html>> head = documentRoot.head();
        Body<Html<Html>> body = documentRoot.body();

        head.meta().attrCharset("utf-8").º()
                .meta().attrName("viewport").attrContent("width=device-width, initial-scale=1.0").º()
                .meta().attrHttpEquiv(EnumHttpEquivMeta.CONTENT_LANGUAGE).attrContent("IE=Edge").º()
                .title().text("JFall 2013 Presentations - htmlApiFaster").º()
                .link().attrRel(EnumRelLinkType.STYLESHEET).attrHref("/webjars/bootstrap/3.3.7-1/css/bootstrap.min.css").attrMedia(EnumMediaMediaType.SCREEN).º().º();

        Div<Body<Html<Html>>> elem = body
                .div().attrClass("container")
                .div().attrClass("page-header")
                .h1().text("JFall 2013 Presentations - htmlApiFaster").º().º();

        presentations.forEach(presentation ->
                elem.div().attrClass("panel panel-default")
                        .div().attrClass("panel-heading")
                        .h3().attrClass("panel-title").text(presentation.getTitle() + " - " + presentation.getSpeakerName()).º().º()
                        .div().attrClass("panel-body").text(presentation.getSummary()).º().º()
        );

        body.script().attrSrc("/webjars/jquery/3.1.1/jquery.min.js").º()
                .script().attrSrc("/webjars/bootstrap/3.3.7-1/js/bootstrap.min.js").º().º().º();
    }
}
