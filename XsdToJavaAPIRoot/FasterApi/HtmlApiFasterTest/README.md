[![Build](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3AhtmlApiFasterTest&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AhtmlApiFasterTest)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3AhtmlApiFasterTest&metric=coverage)](https://sonarcloud.io/component_measures/domain/Coverage?id=com.github.xmlet%3AhtmlApiFasterTest)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3AhtmlApiFasterTest&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AhtmlApiFasterTest)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3AhtmlApiFasterTest&metric=bugs)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AhtmlApiFasterTest)

# HtmlApiFasterTests

<div align="justify"> 
    This project contais multiple tests for the <a href="https://github.com/xmlet/HtmlApiFaster">HtmlApiFaster</a>. The version of the 
    tags used on this project indicate which version of the HtmlApiFaster library that commit tests.
    <br />
    The main goal of the tests of this library is to assert that the HTML restrictions are being validated and that the usage
    of the HtmlApiFaster with a concrete Visitor implementation works in the intended way. In these tests the implemented Visitor
    uses the HtmlApiFaster to write well formed and indented HTML, which results in the creation of an HTML document that 
    respects the rules of the HTML language.
</div>

## How to run these tests?

<div align="justify"> 
    To test any currently released HtmlApiFaster version just run the respective tests as they are.
    <br />
    If you want to fork HtmlApiFaster, make changes and then test them, follow the following instructions:
    <br />
    <br />
    <ol>
        <li>
            In HtmlApiFaster after performing the changes: mvn clean install
        </li>
        <li>
            In HtmlApiFasterTests: mvn clean test
        </li>
    </ol>
</div>