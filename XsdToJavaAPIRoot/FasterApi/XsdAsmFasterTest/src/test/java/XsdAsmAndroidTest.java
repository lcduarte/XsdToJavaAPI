import org.junit.Assert;
import org.junit.Test;
import org.xmlet.androidFaster.*;

public class XsdAsmAndroidTest {

    @Test
    public void testAndroidHierarchy(){
        CustomVisitorAndroid visitor = new CustomVisitorAndroid();

        RelativeLayout<Element> relativeLayout = new RelativeLayout<>(visitor)
                                                .attrAndroidGravity(EnumAndroidGravityRelativeLayout.CENTER)                        /* Method from RelativeLayout */
                                                .attrAndroidAddStatesFromChildren(EnumAndroidAddStatesFromChildrenViewGroup.TRUE)   /* Method from ViewGroup */
                                                .attrAndroidLayoutX("");                                                            /* Method from View */

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

        relativeLayout.__();

        String result = visitor.getResult();

        String expected =   "<relativeLayout android:gravity=\"center\" android:addStatesFromChildren=\"true\" android:layout_x=\"\">\n" +
                            "</relativeLayout>";


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
        CustomVisitorAndroid visitor = new CustomVisitorAndroid();

        new LinearLayout<>(visitor)
            .attrAndroidOrientation(EnumAndroidOrientationLinearLayout.VERTICAL)
            .attrAndroidLayoutWidth("match_parent")
            .attrAndroidLayoutHeight("wrap_content")
            .linearLayout()
                .attrAndroidOrientation(EnumAndroidOrientationLinearLayout.HORIZONTAL)
                .attrAndroidLayoutWidth("match_parent")
                .attrAndroidLayoutHeight("wrap_content")
                .imageView()
                    .attrAndroidLayoutWidth("wrap_content")
                    .attrAndroidLayoutHeight("wrap_content")
                .__()
                .textView()
                    .attrAndroidWidth("match_parent")
                    .attrAndroidHeight("weight_content")
                    .attrAndroidLines("2")
                .__()
            .__()
        .__();

        String result = visitor.getResult();

        String expected =   "<linearLayout android:orientation=\"vertical\" android:layout_width=\"match_parent\" android:layout_height=\"wrap_content\">\n" +
	                            "\t<linearLayout android:orientation=\"horizontal\" android:layout_width=\"match_parent\" android:layout_height=\"wrap_content\">\n" +
		                            "\t\t<imageView android:layout_width=\"wrap_content\" android:layout_height=\"wrap_content\">\n" +
		                            "\t\t</imageView>\n" +
		                            "\t\t<textView android:width=\"match_parent\" android:height=\"weight_content\" android:lines=\"2\">\n" +
		                            "\t\t</textView>\n" +
	                            "\t</linearLayout>\n" +
                            "</linearLayout>";

        Assert.assertEquals(expected, result);
    }

}
