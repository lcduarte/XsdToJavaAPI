import org.xmlet.regexfaster.*;

public class RegexVisitor extends ElementVisitor {

    private StringBuilder stringBuilder = new StringBuilder();

    public void visitElement(Element var1) {

    }

    public void visitAttribute(String var1, String var2) {

    }

    public void visitParent(Element var1) {

    }

    public <R> void visitText(Text<? extends Element, R> var1) {

    }

    public <R> void visitComment(Text<? extends Element, R> var1) {

    }

    String getRegex() {
        return stringBuilder.toString();
    }

    /* Element methods */

    public <Z extends Element> void visitElementOctal(Octal<Z> var1) {
        stringBuilder.append('\\');
    }

    public <Z extends Element> void visitElementAnyDigit(AnyDigit<Z> var1) {
        stringBuilder.append("\\d");
    }

    public <Z extends Element> void visitElementNamedBackReference(NamedBackReference<Z> var1) {
        stringBuilder.append("\\k<");
    }

    public <Z extends Element> void visitElementFromFirstUntilLast(FromFirstUntilLast<Z> var1) {
        stringBuilder.append('[');
    }

    public <Z extends Element> void visitParentFromFirstUntilLast(FromFirstUntilLast<Z> var1) {
        stringBuilder.append(']');
    }

    public <Z extends Element> void visitElementAtStringEnd(AtStringEnd<Z> var1) {
        stringBuilder.append("\\z");
    }

    public <Z extends Element> void visitElementAtBeginning(AtBeginning<Z> var1) {
        stringBuilder.append('^');
    }

    public <Z extends Element> void visitElementTab(Tab<Z> var1) {
        stringBuilder.append('\t');
    }

    public <Z extends Element> void visitElementNoUnnamedModes(NoUnnamedModes<Z> var1) {
        stringBuilder.append('n');
    }

    public <Z extends Element> void visitElementCaseInsensitive(CaseInsensitive<Z> var1) {
        stringBuilder.append('i');
    }

    public <Z extends Element> void visitElementActivateOption(ActivateOption<Z> var1) {
        stringBuilder.append("(?");
    }

    public <Z extends Element> void visitParentActivateOption(ActivateOption<Z> var1) {
        stringBuilder.append(')');
    }

    public <Z extends Element> void visitElementOneOrMore(OneOrMore<Z> var1) {
        stringBuilder.append('+');
    }

    public <Z extends Element> void visitElementCharacterGroup(CharacterGroup<Z> var1) {
        stringBuilder.append('[');
    }

    public <Z extends Element> void visitParentCharacterGroup(CharacterGroup<Z> var1) {
        stringBuilder.append(']');
    }

    public <Z extends Element> void visitElementAtEnd(AtEnd<Z> var1) {
        stringBuilder.append('$');
    }

    public <Z extends Element> void visitElementBetweenNAndMTimes(BetweenNAndMTimes<Z> var1) {
        stringBuilder.append('{');
    }

    public <Z extends Element> void visitParentBetweenNAndMTimes(BetweenNAndMTimes<Z> var1) {
        stringBuilder.append('}');
    }

    public <Z extends Element> void visitElementBell(Bell<Z> var1) {
        stringBuilder.append("\\a");
    }

    public <Z extends Element> void visitElementAtStringBeginning(AtStringBeginning<Z> var1) {
        stringBuilder.append("\\A");
    }

    public <Z extends Element> void visitElementOneOrMoreAsFewAsPossible(OneOrMoreAsFewAsPossible<Z> var1) {
        stringBuilder.append("+?");
    }

    public <Z extends Element> void visitElementConsecutiveMatch(ConsecutiveMatch<Z> var1) {
        stringBuilder.append("\\G");
    }

    public <Z extends Element> void visitElementFormFeed(FormFeed<Z> var1) {
        stringBuilder.append("\\f");
    }

    public <Z extends Element> void visitElementBetweenBoundaries(BetweenBoundaries<Z> var1) {
        stringBuilder.append("\\b");
    }

    public <Z extends Element> void visitElementUnicode(Unicode<Z> var1) {
        stringBuilder.append("\\u");
    }

    public <Z extends Element> void visitElementIfGroupMatch(IfGroupMatch<Z> var1) {
        stringBuilder.append("(?(");
    }

    public <Z extends Element> void visitParentIfGroupMatch(IfGroupMatch<Z> var1) {
        stringBuilder.append(')');
    }

    public <Z extends Element> void visitElementZeroOrMoreAsFewAsPossible(ZeroOrMoreAsFewAsPossible<Z> var1) {
        stringBuilder.append("*?");
    }

    public <Z extends Element> void visitElementNewLine(NewLine<Z> var1) {
        stringBuilder.append("\\n");
    }

    public <Z extends Element> void visitElementAnyLetter(AnyLetter<Z> var1) {
        stringBuilder.append("\\w");
    }

    public <Z extends Element> void visitElementAnyWhiteSpace(AnyWhiteSpace<Z> var1) {
        stringBuilder.append("\\s");
    }

    public <Z extends Element> void visitElementBackReference(BackReference<Z> var1) {
        stringBuilder.append('\\');
    }

    public <Z extends Element> void visitElementOffBoundaries(OffBoundaries<Z> var1) {
        stringBuilder.append("\\B");
    }

    public <Z extends Element> void visitElementAnyNonDigit(AnyNonDigit<Z> var1) {
        stringBuilder.append("\\D");
    }

    public <Z extends Element> void visitElementIfMatch(IfMatch<Z> var1) {
        stringBuilder.append("(?(");
    }

    public <Z extends Element> void visitParentIfMatch(IfMatch<Z> var1) {
        stringBuilder.append(')');
    }

    public <Z extends Element> void visitElementMatchPreviousBetweenNAndMTimesButAsFewPossible(MatchPreviousBetweenNAndMTimesButAsFewPossible<Z> var1) {
        stringBuilder.append('{');
    }

    public <Z extends Element> void visitParentMatchPreviousBetweenNAndMTimesButAsFewPossible(MatchPreviousBetweenNAndMTimesButAsFewPossible<Z> var1) {
        stringBuilder.append('}');
    }

    public <Z extends Element> void visitElementBackspace(Backspace<Z> var1) {
        stringBuilder.append("\\b");
    }

    public <Z extends Element> void visitElementMultilineMode(MultilineMode<Z> var1) {
        stringBuilder.append('m');
    }

    public <Z extends Element> void visitElementAnyNonWhiteSpace(AnyNonWhiteSpace<Z> var1) {
        stringBuilder.append("\\S");
    }

    public <Z extends Element> void visitElementRegexComment(RegexComment<Z> var1) {
        stringBuilder.append("(?#");
    }

    public <Z extends Element> void visitParentRegexComment(RegexComment<Z> var1) {
        stringBuilder.append(')');
    }

    public <Z extends Element> void visitElementAnyChar(AnyChar<Z> var1) {
        stringBuilder.append('.');
    }

    public <Z extends Element> void visitElementAtLeastNTimes(AtLeastNTimes<Z> var1) {
        stringBuilder.append('{');
    }

    public <Z extends Element> void visitParentAtLeastNTimes(AtLeastNTimes<Z> var1) {
        stringBuilder.append(",}");
    }

    public <Z extends Element> void visitElementZeroOrOneAsFewAsPossible(ZeroOrOneAsFewAsPossible<Z> var1) {
        stringBuilder.append("*?");
    }

    public <Z extends Element> void visitElementEscape(Escape<Z> var1) {
        stringBuilder.append("\\e");
    }

    public <Z extends Element> void visitElementHexadecimal(Hexadecimal<Z> var1) {
        stringBuilder.append("\\x");
    }

    public <Z extends Element> void visitElementRegexLineComment(RegexLineComment<Z> var1) {
        stringBuilder.append('#');
    }

    public <Z extends Element> void visitElementUnicodeNotBlock(UnicodeNotBlock<Z> var1) {
        stringBuilder.append("\\P{");
    }

    public <Z extends Element> void visitParentUnicodeNotBlock(UnicodeNotBlock<Z> var1) {
        stringBuilder.append('}');
    }

    public <Z extends Element> void visitElementOr(Or<Z> var1) {
        stringBuilder.append('|');
    }

    public <Z extends Element> void visitElementAtStringEndOrNewline(AtStringEndOrNewline<Z> var1) {
        stringBuilder.append("\\Z");
    }

    public <Z extends Element> void visitElementCarriageReturn(CarriageReturn<Z> var1) {
        stringBuilder.append("\\r");
    }

    public <Z extends Element> void visitElementControlChar(ControlChar<Z> var1) {
        stringBuilder.append("\\c");
    }

    public <Z extends Element> void visitElementIgnoreUnescapedWhiteSpaces(IgnoreUnescapedWhiteSpaces<Z> var1) {
        stringBuilder.append('x');
    }

    public <Z extends Element> void visitElementAnyNonLetter(AnyNonLetter<Z> var1) {
        stringBuilder.append("\\W");
    }

    public <Z extends Element> void visitElementAtLeastNTimesButAsFewAsPossible(AtLeastNTimesButAsFewAsPossible<Z> var1) {
        stringBuilder.append('{');
    }

    public <Z extends Element> void visitParentAtLeastNTimesButAsFewAsPossible(AtLeastNTimesButAsFewAsPossible<Z> var1) {
        stringBuilder.append(",}?");
    }

    public <Z extends Element> void visitElementVerticalTab(VerticalTab<Z> var1) {
        stringBuilder.append("\\v");
    }

    public <Z extends Element> void visitElementCharacterNotGroup(CharacterNotGroup<Z> var1) {
        stringBuilder.append("[^");
    }

    public <Z extends Element> void visitParentCharacterNotGroup(CharacterNotGroup<Z> var1) {
        stringBuilder.append("]");
    }

    public <Z extends Element> void visitElementZeroOrMore(ZeroOrMore<Z> var1) {
        stringBuilder.append("*");
    }

    public <Z extends Element> void visitElementMatchPreviousNTimes(MatchPreviousNTimes<Z> var1) {
        stringBuilder.append("{");
    }

    public <Z extends Element> void visitParentMatchPreviousNTimes(MatchPreviousNTimes<Z> var1) {
        stringBuilder.append("}?");
    }

    public <Z extends Element> void visitElementOtherChar(OtherChar<Z> var1) {
        stringBuilder.append("\\");
    }

    public <Z extends Element> void visitElementUnicodeBlock(UnicodeBlock<Z> var1) {
        stringBuilder.append("\\p{");
    }

    public <Z extends Element> void visitParentUnicodeBlock(UnicodeBlock<Z> var1) {
        stringBuilder.append("}");
    }

    public <Z extends Element> void visitElementSingleLineMode(SingleLineMode<Z> var1) {
        stringBuilder.append("s");
    }

    public <Z extends Element> void visitElementNTimes(NTimes<Z> var1) {
        stringBuilder.append("{");
    }

    public <Z extends Element> void visitParentNTimes(NTimes<Z> var1) {
        stringBuilder.append("}");
    }

    public void visitAttributeExpression(String expression) {
        stringBuilder.append(expression);
    }

    public void visitAttributeOneChar(String oneChar) {
        stringBuilder.append(oneChar);
    }

    public void visitAttributeLast(String last) {
        stringBuilder.append('-').append(last);
    }

    public void visitAttributeThen(String then) {
        stringBuilder.append(")").append(then);
    }

    public void visitAttributeFourDigits(String fourDigits) {
        stringBuilder.append(fourDigits);
    }

    public void visitAttributeM(String m) {
        stringBuilder.append(",").append(m);
    }

    public void visitAttributeN(String n) {
        stringBuilder.append(n);
    }

    public void visitAttributeTwoDigits(String twoDigits) {
        stringBuilder.append(twoDigits);
    }

    public void visitAttributeNumber(String number) {
        stringBuilder.append(number);
    }

    public void visitAttributeElse(String var1) {
        stringBuilder.append("|").append(var1);
    }

    public void visitAttributeName(String name) {
        stringBuilder.append(name);
    }

    public void visitAttributeThreeDigits(String threeDigits) {
        stringBuilder.append(threeDigits);
    }

    public void visitAttributeComment(String comment) {
        stringBuilder.append(comment);
    }

    public void visitAttributeFirst(String first) {
        stringBuilder.append(first);
    }

    public void visitAttributeOption(String option) {
        stringBuilder.append(option);
    }
}
