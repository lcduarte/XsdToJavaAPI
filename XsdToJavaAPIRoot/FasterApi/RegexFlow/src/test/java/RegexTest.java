import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class RegexTest {

    @Test
    public void testRegex(){
        List<String> result1 = new Regex(RegexTest::anyLetterRegex).match("Hello, Luis.");

        Assert.assertEquals(9, result1.size());
        Assert.assertEquals("H", result1.get(0));
        Assert.assertEquals("e", result1.get(1));
        Assert.assertEquals("l", result1.get(2));
        Assert.assertEquals("l", result1.get(3));
        Assert.assertEquals("o", result1.get(4));
        Assert.assertEquals("L", result1.get(5));
        Assert.assertEquals("u", result1.get(6));
        Assert.assertEquals("i", result1.get(7));
        Assert.assertEquals("s", result1.get(8));

        List<String> result2 = new Regex(RegexTest::anyWordRegex).match("Hello, Luis.");

        Assert.assertEquals(2, result2.size());
        Assert.assertEquals("Hello", result2.get(0));
        Assert.assertEquals("Luis", result2.get(1));
    }

    private static void anyLetterRegex(Regex regex){
        regex.anyLetter();
    }

    private static void anyWordRegex(Regex regex){
        regex.anyLetter().º().zeroOrMore();
    }
}
