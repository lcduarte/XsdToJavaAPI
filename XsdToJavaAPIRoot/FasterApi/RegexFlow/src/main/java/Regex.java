import org.xmlet.regexfaster.AnyLetter;
import org.xmlet.regexfaster.Element;
import org.xmlet.regexfaster.ElementVisitor;
import org.xmlet.regexfaster.ZeroOrMore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex implements Element<Regex, Element> {

    private final String regex;
    private final RegexVisitor visitor;

    public Regex(Consumer<Regex> consumer){
        visitor = new RegexVisitor();
        consumer.accept(this);

        regex = visitor.getRegex();
    }

    public List<String> match(String toMatch, boolean ignoreEmptyStrings){
        List<String> res = new ArrayList<>();
        Matcher matcher = Pattern.compile(this.regex, Pattern.CANON_EQ).matcher(toMatch);

        while(matcher.find()){
            String match = matcher.group();

            if (ignoreEmptyStrings && match.length() != 0){
                res.add(matcher.group());
            }
        }

        return res;
    }

    public List<String> match(String toMatch){
        return match(toMatch, true);
    }

    public AnyLetter<Regex> anyLetter(){
        return new AnyLetter<>(this);
    }


    public ZeroOrMore<Regex> zeroOrMore() {
        return new ZeroOrMore<>(this);
    }

    @Override
    public Regex self() {
        return null;
    }

    @Override
    public ElementVisitor getVisitor() {
        return visitor;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Element º() {
        return null;
    }

    @Override
    public Element getParent() {
        return null;
    }

}
