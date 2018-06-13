package org.xmlet.androidlayoutsapitest;

import org.junit.Test;
import org.xmlet.androidlayoutsapi.EnumAndroidOrientation;
import org.xmlet.androidlayoutsapi.ImageView;
import org.xmlet.androidlayoutsapi.LinearLayout;
import org.xmlet.androidlayoutsapi.TextView;

public class AndroidLayoutsApiTest {

    @Test
    public void testSimpleAndroidLayout() throws Exception{
        new LinearLayout<>()
                .attrAndroidOrientation(EnumAndroidOrientation.VERTICAL)
                .attrAndroidLayoutWidth("match_parent")
                .attrAndroidLayoutHeight("wrap_content")
                .addChild(
                        new LinearLayout()
                                .attrAndroidOrientation(EnumAndroidOrientation.HORIZONTAL)
                                .attrAndroidLayoutWidth("match_parent")
                                .attrAndroidLayoutHeight("wrap_content")
                                .addChild(
                                        new ImageView()
                                                .attrAndroidLayoutWidth("wrap_content")
                                                .attrAndroidLayoutHeight("wrap_content")
                                ).addChild(
                                new TextView()
                                        .attrAndroidWidth("match_parent")
                                        .attrAndroidHeight("weight_content")
                                        .attrAndroidLines("2")
                        )
                );
    }
}
