package org.xmlet.xsdasmfaster.classes;

import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.*;
import org.xmlet.xsdasmfaster.classes.Utils.*;
import org.xmlet.xsdparser.xsdelements.*;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.objectweb.asm.Opcodes.*;
import static org.xmlet.xsdasmfaster.classes.XsdAsmElements.generateMethodsForElement;
import static org.xmlet.xsdasmfaster.classes.XsdAsmUtils.*;
import static org.xmlet.xsdasmfaster.classes.XsdAsmUtils.getFullClassTypeName;
import static org.xmlet.xsdasmfaster.classes.XsdSupportingStructure.*;

class XsdAsmInterfaces {

    private static final String ATTRIBUTE_CASE_SENSITIVE_DIFERENCE = "Alt";
    private static final String HIERARCHY_INTERFACES_SUFFIX = "HierarchyInterface";
    private static final String SEQUENCE_SUFFIX = "Sequence";
    private static final String ALL_SUFFIX = "All";
    private static final String CHOICE_SUFFIX = "Choice";

    private Map<String, InterfaceInfo> createdInterfaces = new HashMap<>();
    private Map<String, XsdAbstractElement> createdElements = new HashMap<>();
    private Map<String, AttributeHierarchyItem> attributeGroupInterfaces = new HashMap<>();
    private Map<String, ElementHierarchyItem> hierarchyInterfaces = new HashMap<>();
    private XsdAsm xsdAsmInstance;

    XsdAsmInterfaces(XsdAsm instance) {
        this.xsdAsmInstance = instance;
    }

    /**
     * Generates all the required interfaces, based on the information gathered while
     * creating the other classes. It creates both types of interfaces:
     * ElementGroupInterfaces - Interfaces that serve as a base to adding child elements to the current element;
     * AttributeGroupInterfaces - Interface that serve as a base to adding attributes to the current element;
     * @param createdAttributes A list with the names of the attribute classes already created.
     * @param apiName The api this class will belong.
     */
    void generateInterfaces(Map<String, List<XsdAttribute>> createdAttributes, String apiName) {
        attributeGroupInterfaces.keySet().forEach(attributeGroupInterface -> generateAttributesGroupInterface(createdAttributes, attributeGroupInterface, attributeGroupInterfaces.get(attributeGroupInterface), apiName));
        hierarchyInterfaces.values().forEach(hierarchyInterface -> generateHierarchyAttributeInterfaces(createdAttributes, hierarchyInterface, apiName));
    }

    private void generateHierarchyAttributeInterfaces(Map<String, List<XsdAttribute>> createdAttributes, ElementHierarchyItem hierarchyInterface, String apiName) {
        String interfaceName = hierarchyInterface.getInterfaceName();
        List<String> extendedInterfaceList = hierarchyInterface.getInterfaces();
        String[] extendedInterfaces = listToArray(extendedInterfaceList, ELEMENT);

        ClassWriter classWriter = generateClass(interfaceName, JAVA_OBJECT, extendedInterfaces, getInterfaceSignature(extendedInterfaces, apiName), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        hierarchyInterface.getAttributes().forEach(attribute ->
                generateMethodsAndCreateAttribute(createdAttributes, classWriter, attribute, elementTypeDesc, interfaceName, apiName)
        );

        writeClassToFile(interfaceName, classWriter, apiName);
    }

    /**
     * This method obtains the element interfaces which his class will be implementing.
     * The interfaces are represented in XsdAbstractElements as XsdGroups, XsdAll, XsdSequence and XsdChoice.
     * The respective methods of the interfaces will be the elements from the given XsdGroup, XsdAll, XsdSequence and XsdChoice.
     * @param element The element from which the interfaces will be obtained.
     * @return A string array containing the names of all the interfaces this method implements in
     * interface-like names, e.g. flowContent will be IFlowContent.
     */
    private String[] getElementInterfaces(XsdElement element, String apiName){
        XsdAbstractElement child = getElementInterfacesElement(element);
        List<String> interfaceNames = null;

        if (child != null){
            List<InterfaceInfo> interfaceInfo = iterativeCreation(child, getCleanName(element), 0, apiName, null);
            interfaceNames = interfaceInfo.stream().map(InterfaceInfo::getInterfaceName).collect(Collectors.toList());
        }

        return listToArray(interfaceNames, TEXT_GROUP);
    }

    /**
     * Generates a interface with all the required methods. It uses the information gathered about in attributeGroupInterfaces.
     * @param createdAttributes A list with the names of the attribute classes already created.
     * @param attributeGroupName The interface name.
     * @param attributeHierarchyItem An object containing information about the methods of this interface and which interface, if any,
     *                               this interface extends.
     * @param apiName The api this class will belong.
     */
    private void generateAttributesGroupInterface(Map<String, List<XsdAttribute>> createdAttributes, String attributeGroupName, AttributeHierarchyItem attributeHierarchyItem, String apiName){
        String baseClassNameCamelCase = toCamelCase(attributeGroupName);
        String[] interfaces = getAttributeGroupObjectInterfaces(attributeHierarchyItem.getParentsName());
        StringBuilder signature = getAttributeGroupSignature(interfaces, apiName);

        ClassWriter interfaceWriter = generateClass(baseClassNameCamelCase, JAVA_OBJECT, interfaces, signature.toString(), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        attributeHierarchyItem.getOwnElements().forEach(elementAttribute -> {
            if (createdAttributes.keySet().stream().anyMatch(createdAttributeName -> createdAttributeName.equalsIgnoreCase(elementAttribute.getName()))){
                elementAttribute.setName(elementAttribute.getName() + ATTRIBUTE_CASE_SENSITIVE_DIFERENCE);
            }

            generateMethodsAndCreateAttribute(createdAttributes, interfaceWriter, elementAttribute, elementTypeDesc, baseClassNameCamelCase, apiName);
        });

        writeClassToFile(baseClassNameCamelCase, interfaceWriter, apiName);
    }

    /**
     * Obtains the names of the attribute interfaces that the given element will implement.
     * @param element The element that contains the attributes.
     * @return The elements interfaces names.
     */
    private String[] getAttributeGroupInterfaces(XsdElement element){
        List<String> attributeGroups = new ArrayList<>();
        XsdComplexType complexType = element.getXsdComplexType();
        Stream<XsdAttributeGroup> extensionAttributeGroups = Stream.empty();
        XsdExtension extension = getXsdExtension(element);

        if (complexType != null){
            if (extension != null){
                extensionAttributeGroups = extension.getXsdAttributeGroup();
            }

            attributeGroups.addAll(getTypeAttributeGroups(complexType, extensionAttributeGroups));
        }

        return listToArray(attributeGroups);
    }

    private String[] getHierarchyInterfaces(XsdElement element, String apiName) {
        List<String> interfaceNames = new ArrayList<>();
        XsdElement base = getBaseFromElement(element);
        List<XsdAttribute> elementAttributes = getOwnAttributes(element).collect(Collectors.toList());
        List<ElementHierarchyItem> hierarchyInterfaces = new ArrayList<>();

        while (base != null) {
            List<String> attributeNames = elementAttributes.stream().map(XsdAttribute::getName).collect(Collectors.toList());
            List<XsdAttribute> moreAttributes = getOwnAttributes(base).filter(attribute -> !attributeNames.contains(attribute.getName())).collect(Collectors.toList());
            elementAttributes.addAll(moreAttributes);

            hierarchyInterfaces.add(new ElementHierarchyItem(base.getName() + HIERARCHY_INTERFACES_SUFFIX, moreAttributes, getInterfaces(base, apiName)));

            base = getBaseFromElement(base);
        }

        if (!hierarchyInterfaces.isEmpty()){
            interfaceNames.add(hierarchyInterfaces.get(0).getInterfaceName());

            hierarchyInterfaces.forEach(item -> this.hierarchyInterfaces.put(item.getInterfaceName(), item));
        }

        return listToArray(interfaceNames);
    }

    /**
     * Obtains the attribute groups of a given element that are present in its type attribute.
     * @param complexType The XsdComplexType object with the type attribute.
     * @return The names of the attribute groups.
     */
    private Collection<String> getTypeAttributeGroups(XsdComplexType complexType, Stream<XsdAttributeGroup> extensionAttributeGroups) {
        Stream<XsdAttributeGroup> attributeGroups = complexType.getXsdAttributes()
                .filter(attribute -> attribute.getParent() instanceof XsdAttributeGroup)
                .map(attribute -> (XsdAttributeGroup) attribute.getParent())
                .distinct();

        attributeGroups = Stream.concat(attributeGroups, extensionAttributeGroups);

        attributeGroups = Stream.concat(attributeGroups, complexType.getXsdAttributeGroup());

        List<XsdAttributeGroup> attributeGroupList = attributeGroups.distinct().collect(Collectors.toList());

        attributeGroupList.forEach(this::addAttributeGroup);

        if (!attributeGroupList.isEmpty()){
            return getBaseAttributeGroupInterface(complexType.getXsdAttributeGroup().collect(Collectors.toList()));
        }

        return Collections.emptyList();
    }

    /**
     * Recursively iterates in parents of attributes in order to try finding a common attribute group.
     * @param attributeGroups The attributeGroups contained in the element.
     * @return The elements super class name.
     */
    private List<String> getBaseAttributeGroupInterface(List<XsdAttributeGroup> attributeGroups){
        List<XsdAttributeGroup> parents = new ArrayList<>();

        attributeGroups.forEach(attributeGroup -> {
            XsdAbstractElement parent = attributeGroup.getParent();

            if (parent instanceof XsdAttributeGroup && !parents.contains(parent)){
                parents.add((XsdAttributeGroup) parent);
            }
        });

        if (attributeGroups.size() == 1 || parents.isEmpty()){
            return attributeGroups.stream().map(attributeGroup -> toCamelCase(attributeGroup.getName())).collect(Collectors.toList());
        }

        return getBaseAttributeGroupInterface(parents);
    }

    /**
     * Adds information about the attribute group interface to the attributeGroupInterfaces variable.
     * @param attributeGroup The attributeGroup to add.
     */
    private void addAttributeGroup(XsdAttributeGroup attributeGroup) {
        String interfaceName = toCamelCase(attributeGroup.getName());

        if (!attributeGroupInterfaces.containsKey(interfaceName)){
            List<XsdAttribute> ownElements = attributeGroup.getXsdElements()
                    .filter(attribute -> attribute.getParent().equals(attributeGroup))
                    .map(attribute -> (XsdAttribute) attribute)
                    .collect(Collectors.toList());

            List<String> parentNames = attributeGroup.getAttributeGroups().stream().map(XsdNamedElements::getName).collect(Collectors.toList());
            AttributeHierarchyItem attributeHierarchyItemItem = new AttributeHierarchyItem(parentNames, ownElements);

            attributeGroupInterfaces.put(interfaceName, attributeHierarchyItemItem);
        }
    }

    /** Obtains the signature for the attribute group interfaces based on the implemented interfaces.
     * @param interfaces The implemented interfaces.
     * @return The signature of this interface.
     */
    private StringBuilder getAttributeGroupSignature(String[] interfaces, String apiName) {
        StringBuilder signature = new StringBuilder("<T::L" + elementType + "<TT;TZ;>;Z::" + elementTypeDesc + ">" + JAVA_OBJECT_DESC);

        if (interfaces.length == 0){
            signature.append("L").append(elementType).append("<TT;TZ;>;");
        } else {
            for (String anInterface : interfaces) {
                signature.append("L").append(getFullClassTypeName(anInterface, apiName)).append("<TT;TZ;>;");
            }
        }

        return signature;
    }

    /**
     * Obtains an array with the names of the interfaces implemented by a attribute group interface
     * with the given parents, as in interfaces that will be extended.
     * @param parentsName The list of interfaces that this interface will extend
     * @return A string array containing the names of the interfaces that this interface will extend.
     */
    private String[] getAttributeGroupObjectInterfaces(List<String> parentsName) {
        return listToArray(parentsName.stream().map(XsdAsmUtils::toCamelCase).collect(Collectors.toList()), ELEMENT);
    }

    /**
     * Obtains all the interfaces that a given element will implement.
     * @param element The element in which the class will be based.
     * @return A string array with all the interface names.
     */
    String[] getInterfaces(XsdElement element, String apiName) {
        String[] attributeGroupInterfacesArr =  getAttributeGroupInterfaces(element);
        String[] elementGroupInterfacesArr =  getElementInterfaces(element, apiName);
        String[] hierarchyInterfacesArr = getHierarchyInterfaces(element, apiName);

        return ArrayUtils.addAll(ArrayUtils.addAll(attributeGroupInterfacesArr, elementGroupInterfacesArr), hierarchyInterfacesArr);
    }

    /**
     * Generates an interface based on a XsdGroup element.
     * @param groupName The group name of the XsdGroup element.
     * @param choiceElement The child XsdChoice.
     * @param allElement The child XsdAll.
     * @param sequenceElement The child XsdSequence.
     * @param className The className of the element which contains this group.
     * @param interfaceIndex The current interface index that serves as a base to distinguish interface names.
     * @param apiName The name of the API to be generated.
     * @return A pair with the key being the name of the created group interface and the current interface index after the creation of the interface.
     */
    private InterfaceInfo groupMethod(String groupName, XsdChoice choiceElement, XsdAll allElement, XsdSequence sequenceElement, String className, int interfaceIndex, String apiName){
        if (allElement != null) {
            return iterativeCreation(allElement, className, interfaceIndex + 1, apiName, groupName).get(0);
        }

        if (choiceElement != null) {
            return iterativeCreation(choiceElement, className, interfaceIndex + 1, apiName, groupName).get(0);
        }

        if (sequenceElement != null) {
            return iterativeCreation(sequenceElement, className, interfaceIndex + 1, apiName, groupName).get(0);
        }

        return new InterfaceInfo(TEXT_GROUP);
    }

    /**
     * Generates an interface based on a XsdSequence element.
     * @param xsdElements The elements, ordered, that represent the sequence.
     * @param className The className of the element which contains this sequence.
     * @param interfaceIndex The current interfaceIndex that serves as a base to distinguish interface names.
     * @param apiName The name of the API to be generated.
     * @param groupName The groupName, that indicates if this sequence belongs to a group.
     * @return A pair with the key being the name of the created sequence interface and the current interface index after the creation of the interface and its dependant interfaces.
     */
    private InterfaceInfo sequenceMethod(Stream<XsdAbstractElement> xsdElements, String className, int interfaceIndex, String apiName, String groupName) {
        String interfaceNameBase = groupName != null ? toCamelCase(groupName + SEQUENCE_SUFFIX) : className + SEQUENCE_SUFFIX;
        String interfaceName = interfaceNameBase + interfaceIndex;

        if (createdInterfaces.containsKey(interfaceName)){
            return createdInterfaces.get(interfaceName);
        }

        return createSequenceInterface(xsdElements, interfaceName, className, interfaceIndex, apiName, groupName);
    }

    private InterfaceInfo createSequenceInterface(Stream<XsdAbstractElement> xsdElements, String interfaceName, String className, int interfaceIndex, String apiName, String groupName) {
        SequenceMethodInfo sequenceInfo = getSequenceInfo(xsdElements, className, interfaceIndex, 0, apiName, groupName);
        List<XsdAbstractElement> sequenceList = new ArrayList<>();
        List<String> sequenceNames = new ArrayList<>();

        //sequenceList.add(createdElements.get(firstToLower(className)));
        sequenceList.addAll(sequenceInfo.getSequenceElements());

        sequenceNames.add(className);
        sequenceNames.addAll(sequenceInfo.getSequenceElementNames());

        for (int i = 0; i < sequenceNames.size(); i++) {
            XsdAbstractElement sequenceElement = i == sequenceList.size() ? null : sequenceList.get(i);
            boolean isLast = i == sequenceNames.size() - 1;
            boolean willPointToLast = i == sequenceNames.size() - 2;
            boolean isFirst = i == 0;
            String sequenceName = firstToLower(getCleanName(sequenceNames.get(i)));
            String typeName = getNextTypeName(className, groupName, sequenceName, isLast);
            boolean isRepeatable = typeName.equals(className);

            if (isLast){
                if (!isRepeatable){
                    writeClassToFile(typeName, generateInnerSequenceClass(typeName, className, apiName), apiName);
                }

                break;
            }

            String nextTypeName = getNextTypeName(className, groupName, firstToLower(getCleanName(sequenceNames.get(i + 1))), willPointToLast);

            isRepeatable = nextTypeName.equals(className);

            if (sequenceElement instanceof XsdElement){
                List<XsdElement> elements = Collections.singletonList((XsdElement) sequenceElement);

                if (isFirst){
                    createFirstSequenceInterface(interfaceName, nextTypeName, apiName, isRepeatable, elements);
                } else {
                    createElementsForSequence(className, typeName, apiName, nextTypeName, isRepeatable, elements);
                }
            }

            if (sequenceElement instanceof XsdGroup || sequenceElement instanceof XsdChoice || sequenceElement instanceof XsdAll){
                List<XsdElement> elements = getAllElementsRecursively(sequenceElement);

                if (isFirst){
                    createFirstSequenceInterface(interfaceName, nextTypeName, apiName, isRepeatable, elements);
                } else {
                    createElementsForSequence(className, typeName, apiName, nextTypeName, isRepeatable, elements);
                }
            }

            ++interfaceIndex;
        }

        //TODO
        return new InterfaceInfo(interfaceName, interfaceIndex, sequenceNames.subList(0, 1), new ArrayList<>());
    }

    private void createFirstSequenceInterface(String interfaceName, String nextTypeName, String apiName, boolean isRepeatable, List<XsdElement> elements){
        ClassWriter classWriter = generateClass(interfaceName, JAVA_OBJECT, new String[] {TEXT_GROUP}, getInterfaceSignature(new String[] {TEXT_GROUP}, apiName), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        elements.stream().map(XsdNamedElements::getName).forEach(sequenceName -> generateSequenceMethod(classWriter, getCleanName(sequenceName), interfaceName, nextTypeName, apiName, true, isRepeatable));

        elements.forEach(element -> createElement(element, apiName));

        writeClassToFile(interfaceName, classWriter, apiName);
    }

    private String getNextTypeName(String className, String groupName, String sequenceName, boolean isLast){
        if (isLast)
            return groupName == null ? className + "Complete" : className;
        else
            return className + toCamelCase(sequenceName);
    }

    /**
     * @param typeName The name of the next type to return.
     * @param apiName The name of the API to be generated.
     * @param nextTypeName The nextTypeName of the element that will implement the sequence.
     * @param sequenceElements The elements that serves as base to create this interface.
     */
    private void createElementsForSequence(String className, String typeName, String apiName, String nextTypeName, boolean isRepeatable, List<XsdElement> sequenceElements) {
        ClassWriter classWriter = generateInnerSequenceClass(typeName, className, apiName);

        sequenceElements.stream().map(XsdNamedElements::getName).forEach(sequenceName -> generateSequenceMethod(classWriter, getCleanName(sequenceName), typeName, nextTypeName, apiName, false, isRepeatable));

        sequenceElements.forEach(element -> createElement(element, apiName));

        writeClassToFile(typeName, classWriter, apiName);
    }

    private ClassWriter generateInnerSequenceClass(String typeName, String className, String apiName) {
        ClassWriter classWriter = generateClass(typeName, abstractElementType, new String[] {TEXT_GROUP}, getClassSignature(new String[] {TEXT_GROUP}, typeName, apiName), ACC_PUBLIC + ACC_SUPER, apiName);

        XsdAsmElements.generateSelfMethods(classWriter, typeName, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + elementTypeDesc + ")V", "(TZ;)V", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitLdcInsn(firstToLower(className));
        mVisitor.visitMethodInsn(INVOKESPECIAL, abstractElementType, CONSTRUCTOR, "(" + elementTypeDesc + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "º", "()" + elementTypeDesc, "()TZ;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, "parent", elementTypeDesc);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        return classWriter;
    }

    /**
     * <xs:element name="personInfo">
         <xs:complexType>
             <xs:sequence>
                <xs:element name="firstName" type="xs:string"/>
       (...)
     * Generates the method present in the sequence interface for a sequence element.
     * @param classWriter The classWriter of the sequence interface.
     * @param addingChildName The name of the child to be added. Based in the example above, it would be firstName.
     * @param typeName The name of the next type, which would be PersonInfoFirstName based on the above example.
     * @param nextTypeName The name of the class that contains the sequence.
     * @param apiName The name of the API to be generated.
     */
    private void generateSequenceMethod(ClassWriter classWriter, String addingChildName, String typeName, String nextTypeName, String apiName, boolean isFirst, boolean isRepeatable) {
        String addingType = getFullClassTypeName(firstToUpper(addingChildName), apiName);
        String type = getFullClassTypeName(typeName, apiName);
        String nextType = getFullClassTypeName(nextTypeName, apiName);
        String nextTypeDesc = getFullClassTypeNameDesc(nextTypeName, apiName);

        //TODO Receber o tipo do elemento, se o tiver. Isto implica ter de mudar Text possivelmente.
        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, firstToLower(addingChildName), "(" + JAVA_STRING_DESC + ")" + nextTypeDesc, "(" + JAVA_STRING_DESC + ")L" + nextType + "<" + (isFirst ? "TT" : "TZ") + ";>;", null);
        mVisitor.visitLocalVariable(firstToLower(addingChildName), JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, addingType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);

        if (isFirst){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, type, "self", "()" + elementTypeDesc, true);
        }

        mVisitor.visitMethodInsn(INVOKESPECIAL, addingType, CONSTRUCTOR, "(" + elementTypeDesc + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, addingType, "text", "(" + JAVA_STRING_DESC + ")" + elementTypeDesc, false);
        mVisitor.visitTypeInsn(CHECKCAST, addingType);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, addingType, "º", "()" + elementTypeDesc, false);
        mVisitor.visitInsn(POP);

        if (!isRepeatable){
            mVisitor.visitTypeInsn(NEW, nextType);
            mVisitor.visitInsn(DUP);
            mVisitor.visitVarInsn(ALOAD, 0);

            if (isFirst){
                mVisitor.visitMethodInsn(INVOKEINTERFACE, type, "self", "()" + elementTypeDesc, true);
            } else {
                mVisitor.visitFieldInsn(GETFIELD, type, "parent", elementTypeDesc);
            }

            mVisitor.visitMethodInsn(INVOKESPECIAL, nextType, CONSTRUCTOR, "(" + elementTypeDesc + ")V", false);
        } else {
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitFieldInsn(GETFIELD, type, "parent", elementTypeDesc);
            mVisitor.visitTypeInsn(CHECKCAST, nextType);
        }

        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();
    }

    /**
     * Obtains information about all the members that make up the sequence.
     * @param xsdElements The members that make the sequence.
     * @param className The name of the element that this sequence belongs to.
     * @param interfaceIndex The current interface index.
     * @param unnamedIndex A special index for elements that have no name, which will help distinguish them.
     * @param apiName The name of the API to be generated.
     * @param groupName The group name of the group that contains this sequence, if any.
     * @return A pair, which contains a second pair as key, having the elements as key and the elements name as value, and another pair as value, which contains values for the two indexes.
     */
    private SequenceMethodInfo getSequenceInfo(Stream<XsdAbstractElement> xsdElements, String className, int interfaceIndex, int unnamedIndex, String apiName, String groupName){
        List<XsdAbstractElement> xsdElementsList = xsdElements.collect(Collectors.toList());
        SequenceMethodInfo sequenceMethodInfo = new SequenceMethodInfo(xsdElementsList.stream().filter(element -> !(element instanceof XsdSequence)).collect(Collectors.toList()), interfaceIndex, unnamedIndex);

        for (XsdAbstractElement element : xsdElementsList) {
            if (element instanceof XsdElement){
                String elementName = ((XsdElement) element).getName();

                if (elementName != null){
                    sequenceMethodInfo.addElementName(elementName);
                } else {
                    sequenceMethodInfo.addElementName(className + "SequenceUnnamed" + sequenceMethodInfo.getUnnamedIndex());
                    sequenceMethodInfo.incrementUnnamedIndex();
                }
            } else {
                if (element instanceof XsdSequence){
                    sequenceMethodInfo.receiveChildSequence(getSequenceInfo(element.getXsdElements(), className, interfaceIndex, unnamedIndex, apiName, groupName));
                } else {
                    InterfaceInfo interfaceInfo = iterativeCreation(element, className, interfaceIndex + 1, apiName, groupName).get(0);

                    sequenceMethodInfo.setInterfaceIndex(interfaceInfo.getInterfaceIndex());
                    sequenceMethodInfo.addElementName(interfaceInfo.getInterfaceName());
                }
            }
        }

        return sequenceMethodInfo;
    }

    /**
     * Generates an interface based on a XsdAll element.
     * @param directElements The direct elements of the XsdAll element. Each one will be represented as a method in the all interface.
     * @param className The name of the class that contains the XsdAll.
     * @param interfaceIndex The current interface index.
     * @param apiName The name of the API to be generated.
     * @param groupName The name of the group which contains the XsdAll element, if any.
     * @return A pair with the interface name and the interface index.
     */
    private InterfaceInfo allMethod(List<XsdElement> directElements, String className, int interfaceIndex, String apiName, String groupName){
        String interfaceName = groupName != null ? groupName : className;
        String interfaceFullName = toCamelCase(interfaceName + ALL_SUFFIX + interfaceIndex);

        if (createdInterfaces.containsKey(interfaceName)){
            return createdInterfaces.get(interfaceName);
        }

        return createAllInterface(interfaceFullName, directElements, interfaceIndex, apiName);
    }

    private InterfaceInfo createAllInterface(String interfaceName, List<XsdElement> directElements, int interfaceIndex, String apiName) {
        String[] extendedInterfacesArr = new String[]{TEXT_GROUP};

        ClassWriter classWriter = generateClass(interfaceName, JAVA_OBJECT, extendedInterfacesArr, getInterfaceSignature(extendedInterfacesArr, apiName), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        directElements.forEach(child -> {
            generateMethodsForElement(classWriter, child, getFullClassTypeName(interfaceName, apiName), apiName);
            createElement(child, apiName);
        });

        writeClassToFile(interfaceName, classWriter, apiName);

        List<String> methodNames = directElements.stream().map(XsdElement::getName).collect(Collectors.toList());

        return new InterfaceInfo(interfaceName, interfaceIndex, methodNames);
    }

    /**
     * Generates an interface based on a XsdChoice element.
     * @param groupElements The contained groupElements.
     * @param directElements The direct elements of the XsdChoice element.
     * @param className The name of the class that contains the XsdChoice element.
     * @param interfaceIndex The current interface index.
     * @param apiName The name of the API to be generated.
     * @param groupName The name of the group in which this XsdChoice element is contained, if any.
     * @return A pair with the interface name and the interface index.
     */
    private List<InterfaceInfo> choiceMethod(List<XsdGroup> groupElements, List<XsdElement> directElements, String className, int interfaceIndex, String apiName, String groupName){
        List<InterfaceInfo> interfaceInfoList = new ArrayList<>();
        String interfaceName;

        if (groupName != null){
            interfaceName = toCamelCase(groupName + CHOICE_SUFFIX);
        } else {
            interfaceName = className + CHOICE_SUFFIX + interfaceIndex;
        }

        if (createdInterfaces.containsKey(interfaceName)){
            interfaceInfoList.add(createdInterfaces.get(interfaceName));
            return interfaceInfoList;
        }

        return createChoiceInterface(groupElements, directElements, interfaceName, className, groupName, interfaceIndex, apiName);
    }

    private List<InterfaceInfo> createChoiceInterface(List<XsdGroup> groupElements, List<XsdElement> directElements, String interfaceName, String className, String groupName, int interfaceIndex, String apiName) {
        List<InterfaceInfo> interfaceInfoList = new ArrayList<>();
        List<String> extendedInterfaces = new ArrayList<>();

        for (XsdGroup groupElement : groupElements) {
            InterfaceInfo interfaceInfo = iterativeCreation(groupElement, className, interfaceIndex + 1, apiName, groupName).get(0);

            interfaceIndex = interfaceInfo.getInterfaceIndex();
            extendedInterfaces.add(interfaceInfo.getInterfaceName());
            interfaceInfoList.add(interfaceInfo);
        }

        Set<InterfaceMethodInfo> ambiguousMethods = getAmbiguousMethods(interfaceInfoList);

        if (ambiguousMethods.isEmpty() && directElements.isEmpty()){
            return interfaceInfoList;
        }

        String[] extendedInterfacesArr = listToArray(extendedInterfaces, TEXT_GROUP);

        ClassWriter classWriter = generateClass(interfaceName, JAVA_OBJECT, extendedInterfacesArr, getInterfaceSignature(extendedInterfacesArr, apiName), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        String interfaceType = getFullClassTypeName(interfaceName, apiName);

        directElements.forEach(child -> {
            XsdAsmElements.generateMethodsForElement(classWriter, child, interfaceType, apiName);
            createElement(child, apiName);
        });

        ambiguousMethods.forEach(ambiguousMethod ->
            XsdAsmElements.generateMethodsForElement(classWriter, ambiguousMethod.getMethodName(), interfaceType, apiName, new String[]{"Ljava/lang/Override;"})
        );

        writeClassToFile(interfaceName, classWriter, apiName);

        List<InterfaceInfo> choiceInterface = new ArrayList<>();

        choiceInterface.add(new InterfaceInfo(interfaceName, interfaceIndex, directElements.stream().map(XsdElement::getName).collect(Collectors.toList()), interfaceInfoList));

        return choiceInterface;
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<InterfaceInfo> iterativeCreation(XsdAbstractElement element, String className, int interfaceIndex, String apiName, String groupName){
        List<XsdChoice> choiceElements = new ArrayList<>();
        List<XsdGroup> groupElements = new ArrayList<>();
        List<XsdAll> allElements = new ArrayList<>();
        List<XsdSequence> sequenceElements = new ArrayList<>();
        List<XsdElement> directElements = new ArrayList<>();

        Map<Class, List> mapper = new HashMap<>();

        mapper.put(XsdGroup.class, groupElements);
        mapper.put(XsdChoice.class, choiceElements);
        mapper.put(XsdAll.class, allElements);
        mapper.put(XsdSequence.class, sequenceElements);
        mapper.put(XsdElement.class, directElements);

        //noinspection unchecked
        element.getXsdElements()
                .forEach(elementChild ->
                        mapper.get(elementChild.getClass()).add(elementChild)
                );

        List<InterfaceInfo> interfaceInfoList = new ArrayList<>();

        if (element instanceof XsdGroup){
            XsdChoice choiceElement = choiceElements.size() == 1 ? choiceElements.get(0) : null;
            XsdSequence sequenceElement = sequenceElements.size() == 1 ? sequenceElements.get(0) : null;
            XsdAll allElement = allElements.size() == 1 ? allElements.get(0) : null;

            interfaceInfoList.add(groupMethod(((XsdGroup) element).getName(), choiceElement, allElement, sequenceElement, className, interfaceIndex, apiName));
        }

        if (element instanceof XsdAll){
            interfaceInfoList.add(allMethod(directElements, className, interfaceIndex, apiName, groupName));
        }

        if (element instanceof XsdChoice){
            interfaceInfoList = choiceMethod(groupElements, directElements, className, interfaceIndex, apiName, groupName);
        }

        if (element instanceof  XsdSequence){
            interfaceInfoList.add(sequenceMethod(element.getXsdElements(), className, interfaceIndex, apiName, groupName));
        }

        if (interfaceInfoList.isEmpty()){
            throw new InvalidParameterException("Invalid element interface type.");
        }

        interfaceInfoList.forEach(interfaceInfo -> createdInterfaces.put(interfaceInfo.getInterfaceName(), interfaceInfo));

        return interfaceInfoList;
    }

    /**
     * Creates a class based on a XsdElement if it wasn't been already.
     * @param element The element that serves as base to creating the class.
     * @param apiName The name of the API to be generated.
     */
    private void createElement(XsdElement element, String apiName) {
        String elementName = element.getName();

        if (!createdElements.containsKey(elementName)){
            createdElements.put(elementName, element);
            xsdAsmInstance.generateClassFromElement(element, apiName);
        }
    }

    /**
     * Iterates in a given XsdAbstractElement object in order to obtain all the contained xsdelements.
     * @param element The element to iterate on.
     * @return All the xsdelements contained in the element.
     */
    private List<XsdElement> getAllElementsRecursively(XsdAbstractElement element) {
        List<XsdElement> allGroupElements = new ArrayList<>();
        List<XsdAbstractElement> directElements = element.getXsdElements().collect(Collectors.toList());

        allGroupElements.addAll(
                directElements.stream()
                        .filter(elem1 -> elem1 instanceof XsdElement)
                        .map(elem1 -> (XsdElement) elem1)
                        .collect(Collectors.toList()));

        for (XsdAbstractElement elem : directElements) {
            if ((elem instanceof XsdMultipleElements || elem instanceof XsdGroup) && elem.getXsdElements() != null) {
                allGroupElements.addAll(getAllElementsRecursively(elem));
            }
        }

        return allGroupElements;
    }

    Set<String> getExtraElementsForVisitor() {
        return createdElements.keySet();
    }

    private void addCreatedElement(XsdElement element){
        createdElements.put(element.getName(), element);
    }

    void addCreatedElements(List<XsdElement> elementList) {
        elementList.forEach(this::addCreatedElement);
    }

}
