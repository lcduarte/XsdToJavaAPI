package org.xmlet.androidlayoutsapitest;

import org.junit.Assert;
import org.junit.Test;
import org.xmlet.androidlayoutsapi.*;

@SuppressWarnings("Duplicates")
public class AndroidLayoutsApiTest {

    @Test
    public void testAndroidHierarchy(){
        CustomVisitor visitor = new CustomVisitor();

        RelativeLayout relativeLayout = new RelativeLayout<>()
                .attrAndroidGravity(EnumAndroidGravity.CENTER)                           /* Method from RelativeLayout */
                .attrAndroidAddStatesFromChildren(EnumAndroidAddStatesFromChildren.TRUE) /* Method from ViewGroup */
                .attrAndroidLayoutX(null);                                               /* Method from View */

        boolean implementsView = false;
        boolean implementsViewGroup = false;

        for (Class<?> aClass : RelativeLayout.class.getInterfaces()) {
            if (aClass.equals(ViewGroupHierarchyInterface.class)){
                implementsViewGroup = true;
            }
        }

        for (Class<?> aClass : ViewGroupHierarchyInterface.class.getInterfaces()) {
            if (aClass.equals(ViewHierarchyInterface.class)){
                implementsView = true;
            }
        }

        relativeLayout.accept(visitor);
        String result = visitor.getResult();

        String expected =   "<relativeLayout androidgravity=\"center\" androidaddStatesFromChildren=\"true\" androidlayoutx=\"null\">\n" +
                            "</relativeLayout>\n";

        Assert.assertEquals(expected, result);
        Assert.assertTrue(implementsView);
        Assert.assertTrue(implementsViewGroup);
    }

    /**
     * <LinearLayout
     *      orientation="vertical"
     *      width="match_parent"
     *      height="wrap_content">
     *
     *      <LinearLayout
     *          orientation="horizontal"
     *          width="match_parent"
     *          height="wrap_content">
     *
     *          <ImageView
     *              width="wrap_content"
     *              height="wrap_content">
     *
     *          <TextView
     *              width="match_parent"
     *              height="wrap_content"
     *              lines="2">
     *      </LinearLayout>
     * </LinearLayout>
     */
    @Test
    public void testSimpleAndroidLayout(){
        CustomVisitor visitor = new CustomVisitor();

        LinearLayout linearLayout =
                new LinearLayout<>()
                        .attrAndroidOrientation(EnumAndroidOrientation.VERTICAL)
                        .attrAndroidLayoutWidth("match_parent")
                        .attrAndroidLayoutHeight("wrap_content")
                        .linearLayout()
                            .attrAndroidOrientation(EnumAndroidOrientation.HORIZONTAL)
                            .attrAndroidLayoutWidth("match_parent")
                            .attrAndroidLayoutHeight("wrap_content")
                            .imageView()
                                .attrAndroidLayoutWidth("wrap_content")
                                .attrAndroidLayoutHeight("wrap_content")
                            .º()
                            .textView()
                                .attrAndroidWidth("match_parent")
                                .attrAndroidHeight("weight_content")
                                .attrAndroidLines("2")
                            .º()
                        .º();

        linearLayout.accept(visitor);
        String result = visitor.getResult();

        String expected =   "<linearLayout androidorientation=\"vertical\" androidlayoutwidth=\"match_parent\" androidlayoutheight=\"wrap_content\">\n" +
                                "\t<linearLayout androidorientation=\"horizontal\" androidlayoutwidth=\"match_parent\" androidlayoutheight=\"wrap_content\">\n" +
                                    "\t\t<imageView androidlayoutwidth=\"wrap_content\" androidlayoutheight=\"wrap_content\">\n" +
                                    "\t\t</imageView>\n" +
                                    "\t\t<textView androidwidth=\"match_parent\" androidheight=\"weight_content\" androidlines=\"2\">\n" +
                                    "\t\t</textView>\n" +
                                "\t</linearLayout>\n" +
                            "</linearLayout>\n";

        Assert.assertEquals(expected, result);
    }
}
