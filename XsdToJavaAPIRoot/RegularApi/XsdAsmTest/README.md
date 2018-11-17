# XsdAsmTests

<div align="justify"> 
    This project contais multiple tests for the <a href="https://github.com/xmlet/XsdAsm">XsdAsm</a>. The version of the 
    tags used on this project indicate which version of the XsdAsm library that commit tests.
    <br />
    <br />
    This test library uses four different test libraries based on the following XSD files:
    <br />
    <br />
    <ul>
        <li>
            <i>Html</i> - The HTML5 specification XSD, which is used to test the main behaviour of XsdAsm.
        </li>
        <li>
            <i>Android</i> - The Android layouts file XSD, which is used to test the element hierarchy that can be defined in XSD through the <i>xsd:extension</i> elements.
        </li>
        <li>
            <i>Wpfe</i> - The Wpfe XSD, which introduces many interface layers.
        </li>
        <li>
            <i>TestMin</i> - A manually created XSD meant to create a DSL covering aspects that the other XSD files fail to cover.
        </li>
    </ul>
</div>

## How to run these tests?

<div align="justify"> 
    To test any currently released XsdAsm version just run the respective tests as they are.
    <br />
    If you want to fork XsdAsm, make changes and then test them, follow the following instructions:
    <br />
    <br />
    <ol>
        <li>
            In XsdAsm after performing the changes: mvn clean install
        </li>
        <li>
            In XsdAsmTests: mvn clean test
        </li>
    </ol>
</div>