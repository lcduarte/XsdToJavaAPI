package XsdAsm;

import XsdElements.XsdAbstractElement;
import XsdElements.XsdElement;
import XsdElements.XsdReferenceElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static XsdAsm.XsdAsmElements.generateClassFromElement;
import static XsdAsm.XsdAsmUtils.*;
import static XsdAsm.XsdAsmVisitors.generateVisitors;
import static XsdAsm.XsdSupportingStructure.createSupportingInfrastructure;

public class XsdAsm {

    private XsdAsmInterfaces interfaceGenerator = new XsdAsmInterfaces(this);
    private List<String> createdAttributes = new ArrayList<>();

    public void generateClassFromElements(Stream<XsdAbstractElement> elements, String apiName){
        createGeneratedFilesDirectory(apiName);

        createSupportingInfrastructure(apiName);

        List<XsdElement> elementList = elements.filter(element -> element instanceof XsdElement)
                .map(element -> (XsdElement) element)
                .collect(Collectors.toList());

        elementList.forEach(element -> interfaceGenerator.addCreatedElement(element));

        elementList.forEach(element -> generateClassFromElement(element, apiName));

        interfaceGenerator.generateInterfaces(createdAttributes, apiName);

        generateVisitors(interfaceGenerator.getExtraElementsForVisitor(), apiName);
    }

    void generateClassFromElement(XsdElement element, String apiName){
        XsdAsmElements.generateClassFromElement(interfaceGenerator, createdAttributes, element, apiName);
    }
}
