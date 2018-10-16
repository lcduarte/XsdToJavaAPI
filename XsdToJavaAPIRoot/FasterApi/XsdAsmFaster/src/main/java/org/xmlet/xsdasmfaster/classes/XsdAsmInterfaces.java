package org.xmlet.xsdasmfaster.classes;

import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.xmlet.xsdasmfaster.classes.utils.AttributeHierarchyItem;
import org.xmlet.xsdasmfaster.classes.utils.ElementHierarchyItem;
import org.xmlet.xsdasmfaster.classes.utils.InterfaceInfo;
import org.xmlet.xsdasmfaster.classes.utils.SequenceMethodInfo;
import org.xmlet.xsdparser.xsdelements.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.objectweb.asm.Opcodes.*;
import static org.xmlet.xsdasmfaster.classes.XsdAsmElements.generateClassMethods;
import static org.xmlet.xsdasmfaster.classes.XsdAsmElements.generateMethodsForElement;
import static org.xmlet.xsdasmfaster.classes.XsdAsmUtils.*;
import static org.xmlet.xsdasmfaster.classes.XsdSupportingStructure.*;

/**
 * This class is responsible to generate all the code that is related to interfaces.
 */
class XsdAsmInterfaces {

    /**
     * The value used to differentiate between two {@link XsdAttribute} object with the same name, only differing in
     * the case sensitive aspect.
     */
    private static final String ATTRIBUTE_CASE_SENSITIVE_DIFFERENCE = "Alt";

    /**
     * The suffix applied to all the interfaces that are hierarchy interfaces.
     */
    private static final String HIERARCHY_INTERFACES_SUFFIX = "HierarchyInterface";

    /**
     * The suffix applied to all the interfaces that are generated based on the {@link XsdAll} object.
     */
    private static final String ALL_SUFFIX = "All";

    /**
     * The suffix applied to all the interfaces that are generated based on the {@link XsdChoice} object.
     */
    private static final String CHOICE_SUFFIX = "Choice";


    /**
     * A {@link Map} with information regarding all the interfaces generated.
     */
    private Map<String, InterfaceInfo> createdInterfaces = new HashMap<>();

    /**
     * A {@link Map} with information regarding all the elements generated.
     */
    private Map<String, XsdAbstractElement> createdElements = new HashMap<>();

    /**
     * A {@link Map} with information regarding the hierarchy of attributeGroups.
     */
    private Map<String, AttributeHierarchyItem> attributeGroupInterfaces = new HashMap<>();

    /**
     * A {@link Map} with information regarding the hierarchy interfaces.
     */
    private Map<String, ElementHierarchyItem> hierarchyInterfaces = new HashMap<>();

    /**
     * An {@link XsdAsm} instance. Used to delegate element generation.
     */
    private XsdAsm xsdAsmInstance;


    /**
     * A {@link Map} containing information regarding sequence methods that need to be generated on a specific class,
     * identified by the key of the {@link Map} object.
     */
    private Map<String, Consumer<ClassWriter>> pendingSequenceMethods = new HashMap<>();

    XsdAsmInterfaces(XsdAsm instance) {
        this.xsdAsmInstance = instance;
    }

    /**
     * Generates all the required interfaces, based on the information gathered while creating the other classes.
     * It creates both types of interfaces:
     *  ElementGroupInterfaces - Interfaces that serve as a base to adding child elements to the current element;
     *  AttributeGroupInterfaces - Interface that serve as a base to adding attributes to the current element;
     * @param createdAttributes Information about the attributes that are already created.
     * @param apiName The name of the generated fluent interface.
     */
    void generateInterfaces(Map<String, List<XsdAttribute>> createdAttributes, String apiName) {
        attributeGroupInterfaces.keySet().forEach(attributeGroupInterface -> generateAttributesGroupInterface(createdAttributes, attributeGroupInterface, attributeGroupInterfaces.get(attributeGroupInterface), apiName));
        hierarchyInterfaces.values().forEach(hierarchyInterface -> generateHierarchyAttributeInterfaces(createdAttributes, hierarchyInterface, apiName));
    }

    /**
     * Generates all the hierarchy interfaces.
     * @param createdAttributes Information about the attributes that are already created.
     * @param hierarchyInterface Information about the hierarchy interface to create.
     * @param apiName The name of the generated fluent interface.
     */
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
     * The interfaces are represented in {@link XsdAbstractElement} objects as {@link XsdGroup}, {@link XsdAll},
     * {@link XsdSequence} and {@link XsdChoice}. The respective methods of the interfaces will be the elements from the
     * types enumerated previously.
     * @param element The element from which the interfaces will be obtained.
     * @param apiName The name of the generated fluent interface.
     * @return A {@link String} array containing the names of all the interfaces this method implements.
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
     * Generates an attribute group interface with all the required methods. It uses the information gathered about in
     * attributeGroupInterfaces.
     * @param createdAttributes A list with the names of the attribute classes already created.
     * @param attributeGroupName The interface name.
     * @param attributeHierarchyItem An object containing information about the methods of this interface and which interface, if any,
     *                               this interface extends.
     * @param apiName The name of the generated fluent interface.
     */
    private void generateAttributesGroupInterface(Map<String, List<XsdAttribute>> createdAttributes, String attributeGroupName, AttributeHierarchyItem attributeHierarchyItem, String apiName){
        String baseClassNameCamelCase = firstToUpper(attributeGroupName);
        String[] interfaces = getAttributeGroupObjectInterfaces(attributeHierarchyItem.getParentsName());
        StringBuilder signature = getAttributeGroupSignature(interfaces, apiName);

        ClassWriter interfaceWriter = generateClass(baseClassNameCamelCase, JAVA_OBJECT, interfaces, signature.toString(), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        attributeHierarchyItem.getOwnElements().forEach(elementAttribute -> {
            if (createdAttributes.keySet().stream().anyMatch(createdAttributeName -> createdAttributeName.equalsIgnoreCase(elementAttribute.getName()))){
                elementAttribute.setName(elementAttribute.getName() + ATTRIBUTE_CASE_SENSITIVE_DIFFERENCE);
            }

            generateMethodsAndCreateAttribute(createdAttributes, interfaceWriter, elementAttribute, elementTypeDesc, baseClassNameCamelCase, apiName);
        });

        writeClassToFile(baseClassNameCamelCase, interfaceWriter, apiName);
    }

    /**
     * Obtains the names of the attribute interfaces that the given {@link XsdElement} will implement.
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

        return listToArray(attributeGroups, CUSTOM_ATTRIBUTE_GROUP);
    }

    /**
     * Obtains hierarchy interface information from the received {@link XsdElement}.
     * @param element The received {@link XsdElement} object.
     * @param apiName The name of the generated fluent interface.
     * @return The names of the hierarchy interfaces.
     */
    private String[] getHierarchyInterfaces(XsdElement element, String apiName) {
        List<String> interfaceNames = new ArrayList<>();
        XsdElement base = getBaseFromElement(element);
        List<XsdAttribute> elementAttributes = getOwnAttributes(element).collect(Collectors.toList());
        List<ElementHierarchyItem> hierarchyInterfacesList = new ArrayList<>();

        while (base != null) {
            List<String> attributeNames = elementAttributes.stream().map(XsdAttribute::getName).collect(Collectors.toList());
            List<XsdAttribute> moreAttributes = getOwnAttributes(base).filter(attribute -> !attributeNames.contains(attribute.getName())).collect(Collectors.toList());
            elementAttributes.addAll(moreAttributes);

            hierarchyInterfacesList.add(new ElementHierarchyItem(base.getName() + HIERARCHY_INTERFACES_SUFFIX, moreAttributes, getInterfaces(base, apiName)));

            base = getBaseFromElement(base);
        }

        if (!hierarchyInterfacesList.isEmpty()){
            interfaceNames.add(hierarchyInterfacesList.get(0).getInterfaceName());

            hierarchyInterfacesList.forEach(item -> this.hierarchyInterfaces.put(item.getInterfaceName(), item));
        }

        return listToArray(interfaceNames);
    }

    /**
     * Obtains the attribute groups of a given element that are present in its type attribute.
     * @param complexType The {@link XsdComplexType} object with the type attribute.
     * @param extensionAttributeGroups A {@link Stream} of {@link XsdAttributeGroup} obtained from {@link XsdExtension}.
     * @return The names of the attribute groups present in the type attribute.
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
     * Recursively iterates order to define an hierarchy on the attribute group interfaces.
     * @param attributeGroups The attributeGroups contained in the element.
     * @return A {@link List} of attribute group interface names.
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
            return attributeGroups.stream().map(attributeGroup -> firstToUpper(attributeGroup.getName())).collect(Collectors.toList());
        }

        return getBaseAttributeGroupInterface(parents);
    }

    /**
     * Adds information about the attribute group interface to the attributeGroupInterfaces variable.
     * @param attributeGroup The attributeGroup to add.
     */
    private void addAttributeGroup(XsdAttributeGroup attributeGroup) {
        String interfaceName = firstToUpper(attributeGroup.getName());

        if (!attributeGroupInterfaces.containsKey(interfaceName)){
            List<XsdAttribute> ownElements = attributeGroup.getXsdElements()
                    .filter(attribute -> attribute.getParent().equals(attributeGroup))
                    .map(attribute -> (XsdAttribute) attribute)
                    .collect(Collectors.toList());

            List<String> parentNames = attributeGroup.getAttributeGroups().stream().map(XsdNamedElements::getName).collect(Collectors.toList());
            AttributeHierarchyItem attributeHierarchyItemItem = new AttributeHierarchyItem(parentNames, ownElements);

            attributeGroupInterfaces.put(interfaceName, attributeHierarchyItemItem);

            attributeGroup.getAttributeGroups().forEach(this::addAttributeGroup);
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
     * @param parentsName The list of interfaces that this interface will extend.
     * @return A {@link String} array containing the names of the interfaces that this interface will extend.
     */
    private String[] getAttributeGroupObjectInterfaces(List<String> parentsName) {
        return listToArray(parentsName.stream().map(XsdAsmUtils::firstToUpper).collect(Collectors.toList()), CUSTOM_ATTRIBUTE_GROUP);
    }

    /**
     * Obtains all the interfaces that a given element will implement.
     * @param element The {@link XsdElement} in which the class will be based.
     * @param apiName The name of the generated fluent interface.
     * @return A {@link String} array with all the interface names.
     */
    String[] getInterfaces(XsdElement element, String apiName) {
        String[] attributeGroupInterfacesArr =  getAttributeGroupInterfaces(element);
        String[] elementGroupInterfacesArr =  getElementInterfaces(element, apiName);
        String[] hierarchyInterfacesArr = getHierarchyInterfaces(element, apiName);

        return ArrayUtils.addAll(ArrayUtils.addAll(attributeGroupInterfacesArr, elementGroupInterfacesArr), hierarchyInterfacesArr);
    }

    /**
     * Delegates the interface generation to one of the possible {@link XsdGroup} element children.
     * @param groupName The group name of the {@link XsdGroup} element.
     * @param choiceElement The child {@link XsdChoice}.
     * @param allElement The child {@link XsdAll}.
     * @param sequenceElement The child {@link XsdSequence}.
     * @param className The className of the element which contains this group.
     * @param interfaceIndex The current interface index that serves as a base to distinguish interface names.
     * @param apiName The name of the generated fluent interface.
     * @return A {@link InterfaceInfo} object containing relevant interface information.
     */
    private InterfaceInfo groupMethod(String groupName, XsdChoice choiceElement, XsdAll allElement, XsdSequence sequenceElement, String className, int interfaceIndex, String apiName){
        if (allElement != null) {
            return iterativeCreation(allElement, className, interfaceIndex + 1, apiName, groupName).get(0);
        }

        if (choiceElement != null) {
            return iterativeCreation(choiceElement, className, interfaceIndex + 1, apiName, groupName).get(0);
        }

        if (sequenceElement != null) {
            iterativeCreation(sequenceElement, className, interfaceIndex + 1, apiName, groupName);
        }

        return new InterfaceInfo(TEXT_GROUP);
    }

    /**
     * Postpones the sequence method creation due to solution design.
     * @param xsdElements A {@link Stream} of {@link XsdElement}, ordered, that represent the sequence.
     * @param className The className of the element which contains this sequence.
     * @param interfaceIndex The current interfaceIndex that serves as a base to distinguish interface names.
     * @param apiName The name of the generated fluent interface.
     * @param groupName The groupName, that indicates if this sequence belongs to a group.
     */
    private void sequenceMethod(Stream<XsdAbstractElement> xsdElements, String className, int interfaceIndex, String apiName, String groupName) {
        pendingSequenceMethods.put(className, classWriter -> createSequence(classWriter, xsdElements, className, interfaceIndex, apiName, groupName));
    }

    /**
     * Obtains sequence information and creates all the required classes and methods.
     * @param classWriter The {@link ClassWriter} of the class that contains the sequence.
     * @param xsdElements A {@link Stream} of {@link XsdElement}, ordered, that represent the sequence.
     * @param className The className of the element which contains this sequence.
     * @param interfaceIndex The current interfaceIndex that serves as a base to distinguish interface names.
     * @param apiName The name of the generated fluent interface.
     * @param groupName The groupName, that indicates if this sequence belongs to a group.
     */
    private void createSequence(ClassWriter classWriter, Stream<XsdAbstractElement> xsdElements, String className, int interfaceIndex, String apiName, String groupName) {
        SequenceMethodInfo sequenceInfo = getSequenceInfo(xsdElements, className, interfaceIndex, 0, apiName, groupName);
        List<XsdAbstractElement> sequenceList = new ArrayList<>();
        List<String> sequenceNames = new ArrayList<>();

        sequenceList.addAll(sequenceInfo.getSequenceElements());

        sequenceNames.add(className);
        sequenceNames.addAll(sequenceInfo.getSequenceElementNames());

        for (int i = 0; i < sequenceNames.size(); i++) {
            boolean isLast = i == sequenceNames.size() - 1;
            boolean willPointToLast = i == sequenceNames.size() - 2;
            String typeName = getNextTypeName(className, groupName, firstToLower(getCleanName(sequenceNames.get(i))), isLast);

            if (isLast){
                if (!typeName.equals(className)){
                    createdElements.put(getCleanName(typeName), null);
                    writeClassToFile(typeName, generateInnerSequenceClass(typeName, className, apiName), apiName);
                }

                break;
            }

            String nextTypeName = getNextTypeName(className, groupName, firstToLower(getCleanName(sequenceNames.get(i + 1))), willPointToLast);

            createSequenceClasses(sequenceList.get(i), classWriter, className, typeName, nextTypeName, apiName, i == 0);

            ++interfaceIndex;
        }
    }

    /**
     * Creates classes and methods required to implement the sequence.
     * @param sequenceElement The current sequence element.
     * @param classWriter The {@link ClassWriter} of the first class, which contains the sequence.
     * @param className The name of the class which contains the sequence.
     * @param typeName The current sequence element type name.
     * @param nextTypeName The next sequence element type name.
     * @param apiName The name of the generated fluent interface.
     * @param isFirst Indication if this is the first element of the sequence.
     */
    private void createSequenceClasses(XsdAbstractElement sequenceElement, ClassWriter classWriter, String className, String typeName, String nextTypeName, String apiName, boolean isFirst){
        List<XsdElement> elements = null;

        if (sequenceElement instanceof XsdElement) {
            elements = Collections.singletonList((XsdElement) sequenceElement);
        }

        if (sequenceElement instanceof XsdGroup || sequenceElement instanceof XsdChoice || sequenceElement instanceof XsdAll) {
            elements = getAllElementsRecursively(sequenceElement);
        }

        if (elements != null){
            if (isFirst){
                createFirstSequenceInterface(classWriter, className, nextTypeName, apiName, elements);
            } else {
                createElementsForSequence(className, typeName, nextTypeName, apiName, elements);
            }
        }
    }

    /**
     * Adds a method to the element which contains the sequence.
     * @param classWriter The {@link ClassWriter} of the class which contains the sequence.
     * @param className The name of the class which contains the sequence.
     * @param nextTypeName The next sequence type name.
     * @param apiName The name of the generated fluent interface.
     * @param elements A {@link List} of {@link XsdElement} that are the first sequence value.
     */
    private void createFirstSequenceInterface(ClassWriter classWriter, String className, String nextTypeName, String apiName, List<XsdElement> elements){
        elements.forEach(element -> generateSequenceMethod(classWriter, className, getJavaType(element.getType()), getCleanName(element.getName()), className, nextTypeName, apiName));

        elements.forEach(element -> createElement(element, apiName));
    }

    /** Obtains the name of the next type of the sequence.
     * @param className The name of the class which contains the sequence.
     * @param groupName The groupName of this sequence, if any.
     * @param sequenceName The sequence name.
     * @param isLast Indication if the next type will be the last of the sequence.
     * @return The next sequence type name.
     */
    private String getNextTypeName(String className, String groupName, String sequenceName, boolean isLast){
        if (isLast)
            return groupName == null ? className + "Complete" : className;
        else
            return className + firstToUpper(sequenceName);
    }

    /**
     * Creates the inner classes that are used to support the sequence behaviour and the respective sequence methods.
     * @param className The name of the class which contains the sequence.
     * @param typeName The name of the next type to return.
     * @param apiName The name of the generated fluent interface.
     * @param nextTypeName The nextTypeName of the element that will implement the sequence.
     * @param sequenceElements The elements that serves as base to create this interface.
     */
    private void createElementsForSequence(String className, String typeName, String nextTypeName, String apiName, List<XsdElement> sequenceElements) {
        ClassWriter classWriter = generateInnerSequenceClass(typeName, className, apiName);

        sequenceElements.forEach(sequenceElement ->
                            generateSequenceMethod(classWriter, className, getJavaType(sequenceElement.getType()), getCleanName(sequenceElement.getName()), typeName, nextTypeName, apiName));

        sequenceElements.forEach(element -> createElement(element, apiName));

        addToCreateElements(typeName);

        writeClassToFile(typeName, classWriter, apiName);
    }

    /**
     * Creates the inner classes that are used to support the sequence behaviour.
     * @param typeName The name of the next type to return.
     * @param className The name of the class which contains the sequence.
     * @param apiName The name of the generated fluent interface.
     * @return The {@link ClassWriter} object which represents the inner class created to support sequence behaviour.
     */
    private ClassWriter generateInnerSequenceClass(String typeName, String className, String apiName) {
        ClassWriter classWriter = generateClass(typeName, JAVA_OBJECT, new String[] {CUSTOM_ATTRIBUTE_GROUP}, getClassSignature(new String[]{CUSTOM_ATTRIBUTE_GROUP}, typeName, apiName), ACC_PUBLIC + ACC_SUPER, apiName);

        generateClassMethods(classWriter, typeName, className, apiName, false);

        return classWriter;
    }

    /**
     * <xs:element name="personInfo">
         <xs:complexType>
             <xs:sequence>
                <xs:element name="firstName" type="xs:string"/>
       (...)
     * Generates the method present in the sequence interface for a sequence element.
     *  Example:
     *      PersonInfoFirstName firstName(String firstName);
     * @param classWriter The {@link ClassWriter} of the sequence interface.
     * @param className The name of the class which contains the sequence.
     * @param javaType The java type of the current sequence value.
     * @param addingChildName The name of the child to be added. Based in the example above, it would be firstName.
     * @param typeName The name of the current type, which would be PersonInfo based on the above example.
     * @param nextTypeName The name of the next type, which would be PersonInfoFirstName based on the above example.
     * @param apiName The name of the generated fluent interface.
     */
    private void generateSequenceMethod(ClassWriter classWriter, String className, String javaType, String addingChildName, String typeName, String nextTypeName, String apiName) {
        String type = getFullClassTypeName(typeName, apiName);
        String nextType = getFullClassTypeName(nextTypeName, apiName);
        String nextTypeDesc = getFullClassTypeNameDesc(nextTypeName, apiName);
        String addingType = getFullClassTypeName(addingChildName, apiName);

        javaType = javaType == null ? JAVA_STRING_DESC : javaType;

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, firstToLower(addingChildName), "(" + javaType + ")" + nextTypeDesc, "(" + javaType + ")L" + nextType + "<TZ;>;", null);
        mVisitor.visitLocalVariable(firstToLower(addingChildName), JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, addingType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, type, "visitor", elementVisitorTypeDesc);
        mVisitor.visitInsn(ICONST_1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, addingType, CONSTRUCTOR, "(" + elementTypeDesc + elementVisitorTypeDesc + "Z)V", false);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, addingType, "text", "(" + JAVA_OBJECT_DESC + ")" + elementTypeDesc, false);
        mVisitor.visitTypeInsn(CHECKCAST, addingType);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, addingType, "__", "()" + elementTypeDesc, false);
        mVisitor.visitInsn(POP);
        mVisitor.visitTypeInsn(NEW, nextType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, type, "parent", elementTypeDesc);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, type, "visitor", elementVisitorTypeDesc);

        if (className.equals(nextTypeName)){
            mVisitor.visitInsn(ICONST_0);
        } else {
            mVisitor.visitInsn(ICONST_1);
        }

        mVisitor.visitMethodInsn(INVOKESPECIAL, nextType, CONSTRUCTOR, "(" + elementTypeDesc + elementVisitorTypeDesc + "Z)V", false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(5, 2);
        mVisitor.visitEnd();
    }

    /**
     * Obtains information about all the members that make up the sequence.
     * @param xsdElements The members that make the sequence.
     * @param className The name of the element that this sequence belongs to.
     * @param interfaceIndex The current interface index.
     * @param unnamedIndex A special index for elements that have no name, which will help distinguish them.
     * @param apiName The name of the generated fluent interface.
     * @param groupName The group name of the group that contains this sequence, if any.
     * @return A {@link SequenceMethodInfo} object which contains relevant information regarding sequence methods.
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
     * Generates an interface based on a {@link XsdAll} element.
     * @param directElements The direct elements of the {@link XsdAll} element. Each one will be represented as a method
     *                       in the all interface.
     * @param className The name of the class that contains the {@link XsdAll}.
     * @param interfaceIndex The current interface index.
     * @param apiName The name of the generated fluent interface.
     * @param groupName The name of the group which contains the {@link XsdAll} element, if any.
     * @return A {@link InterfaceInfo} object containing relevant interface information.
     */
    private InterfaceInfo allMethod(List<XsdElement> directElements, String className, int interfaceIndex, String apiName, String groupName){
        String interfaceName = groupName != null ? groupName : className;
        String interfaceFullName = firstToUpper(interfaceName + ALL_SUFFIX + interfaceIndex);

        if (createdInterfaces.containsKey(interfaceName)){
            return createdInterfaces.get(interfaceName);
        }

        return createAllInterface(interfaceFullName, directElements, interfaceIndex, apiName);
    }

    /**
     * Creates the interface based on the information present in the {@link XsdAll} objects.
     * @param interfaceName The interface name.
     * @param directElements The direct elements of the {@link XsdAll} element. Each one will be represented as a method
     *                       in the all interface.
     * @param interfaceIndex The current interface index.
     * @param apiName The name of the generated fluent interface.
     * @return A {@link InterfaceInfo} object containing relevant interface information.
     */
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
     * Generates an interface based on a {@link XsdChoice} element.
     * @param groupElements The contained groupElements.
     * @param directElements The direct elements of the {@link XsdChoice} element.
     * @param className The name of the class that contains the {@link XsdChoice} element.
     * @param interfaceIndex The current interface index.
     * @param apiName The name of the generated fluent interface.
     * @param groupName The name of the group in which this {@link XsdChoice} element is contained, if any.
     * @return A {@link List} of {@link InterfaceInfo} objects containing relevant interface information.
     */
    private List<InterfaceInfo> choiceMethod(List<XsdGroup> groupElements, List<XsdElement> directElements, String className, int interfaceIndex, String apiName, String groupName){
        List<InterfaceInfo> interfaceInfoList = new ArrayList<>();
        String interfaceName;

        if (groupName != null){
            interfaceName = firstToUpper(groupName + CHOICE_SUFFIX);
        } else {
            interfaceName = className + CHOICE_SUFFIX + interfaceIndex;
        }

        if (createdInterfaces.containsKey(interfaceName)){
            interfaceInfoList.add(createdInterfaces.get(interfaceName));
            return interfaceInfoList;
        }

        return createChoiceInterface(groupElements, directElements, interfaceName, className, groupName, interfaceIndex, apiName);
    }

    /**
     * Generates an interface based on a {@link XsdChoice} element.
     * @param groupElements The contained groupElements.
     * @param directElements The direct elements of the {@link XsdChoice} element.
     * @param interfaceName The choice interface name.
     * @param className The name of the class that contains the {@link XsdChoice} element.
     * @param interfaceIndex The current interface index.
     * @param apiName The name of the generated fluent interface.
     * @param groupName The name of the group in which this {@link XsdChoice} element is contained, if any.
     * @return A {@link List} of {@link InterfaceInfo} objects containing relevant interface information.
     */
    private List<InterfaceInfo> createChoiceInterface(List<XsdGroup> groupElements, List<XsdElement> directElements, String interfaceName, String className, String groupName, int interfaceIndex, String apiName) {
        List<InterfaceInfo> interfaceInfoList = new ArrayList<>();
        List<String> extendedInterfaces = new ArrayList<>();

        for (XsdGroup groupElement : groupElements) {
            InterfaceInfo interfaceInfo = iterativeCreation(groupElement, className, interfaceIndex + 1, apiName, groupName).get(0);

            interfaceIndex = interfaceInfo.getInterfaceIndex();
            extendedInterfaces.add(interfaceInfo.getInterfaceName());
            interfaceInfoList.add(interfaceInfo);
        }

        Set<String> ambiguousMethods = getAmbiguousMethods(interfaceInfoList);

        if (ambiguousMethods.isEmpty() && directElements.isEmpty()){
            return interfaceInfoList;
        }

        String[] extendedInterfacesArr = listToArray(extendedInterfaces, TEXT_GROUP);

        ClassWriter classWriter = generateClass(interfaceName, JAVA_OBJECT, extendedInterfacesArr, getInterfaceSignature(extendedInterfacesArr, apiName), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        String interfaceType = getFullClassTypeName(interfaceName, apiName);

        directElements.forEach(child -> {
            generateMethodsForElement(classWriter, child, interfaceType, apiName);
            createElement(child, apiName);
        });

        ambiguousMethods.forEach(ambiguousMethodName ->
            generateMethodsForElement(classWriter, ambiguousMethodName, interfaceType, apiName, new String[]{"Ljava/lang/Override;"})
        );

        writeClassToFile(interfaceName, classWriter, apiName);

        List<InterfaceInfo> choiceInterface = new ArrayList<>();

        choiceInterface.add(new InterfaceInfo(interfaceName, interfaceIndex, directElements.stream().map(XsdElement::getName).collect(Collectors.toList()), interfaceInfoList));

        return choiceInterface;
    }

    /**
     * This method functions as an iterative process for interface creation.
     * @param element The element which could have more interface information.
     * @param className The name of the class which contains the interfaces.
     * @param interfaceIndex The current interface index.
     * @param apiName The name of the generated fluent interface.
     * @param groupName The name of the group in which this {@link XsdChoice} element is contained, if any.
     * @return A {@link List} of {@link InterfaceInfo} objects containing relevant interface information.
     */
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
            sequenceMethod(element.getXsdElements(), className, interfaceIndex, apiName, groupName);
        }

        interfaceInfoList.forEach(interfaceInfo -> createdInterfaces.put(interfaceInfo.getInterfaceName(), interfaceInfo));

        return interfaceInfoList;
    }

    /**
     * Creates a class based on a {@link XsdElement} if it wasn't been already.
     * @param element The element that serves as base to creating the class.
     * @param apiName The name of the generated fluent interface.
     */
    private void createElement(XsdElement element, String apiName) {
        String elementName = element.getName();

        if (!createdElements.containsKey(getCleanName(elementName))){
            createdElements.put(getCleanName(elementName), element);
            xsdAsmInstance.generateClassFromElement(element, apiName);
        }
    }

    /**
     * Creates a class based on a {@link XsdElement} if it wasn't been already.
     * @param elementName The name of the element.
     */
    private void addToCreateElements(String elementName) {
        HashMap<String, String> elementAttributes = new HashMap<>();

        elementAttributes.put(XsdAbstractElement.NAME_TAG, elementName);

        if (!createdElements.containsKey(getCleanName(elementName))){
            createdElements.put(getCleanName(elementName), new XsdElement(null, elementAttributes));
        }
    }

    /**
     * Iterates in a given {@link XsdAbstractElement} object in order to obtain all the contained {@link XsdElement} objects.
     * @param element The element to iterate on.
     * @return All the {@link XsdElement} objects contained in the received element.
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

    /**
     * Adds the received {@link XsdElement} to the list of created elements.
     * @param element The received {@link XsdElement}.
     */
    private void addCreatedElement(XsdElement element){
        createdElements.put(getCleanName(element.getName()), element);
    }

    /**
     * Adds a {@link List} of {@link XsdElement} to the list of created elements.
     * @param elementList The {@link List} to add.
     */
    void addCreatedElements(List<XsdElement> elementList) {
        elementList.forEach(this::addCreatedElement);
    }

    /**
     * Verifies if there is any postponed sequence method creation in pendingSequenceMethods and performs the method if
     * it exists.
     * @param classWriter The {@link ClassWriter} object for the class that contains the postponed sequence method creation.
     * @param className The name of the class that contains the sequence.
     */
    void checkForSequenceMethod(ClassWriter classWriter, String className) {
        Consumer<ClassWriter> sequenceMethodCreator = pendingSequenceMethods.get(className);

        if (sequenceMethodCreator != null){
            sequenceMethodCreator.accept(classWriter);
        }
    }

    /**
     * @return Obtains all the names of the elements that were created in the current execution.
     */
    Set<String> getExtraElementsForVisitor() {
        return createdElements.keySet();
    }

}
