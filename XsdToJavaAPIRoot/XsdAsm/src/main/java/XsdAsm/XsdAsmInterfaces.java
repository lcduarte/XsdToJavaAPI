package XsdAsm;

import XsdElements.*;
import javafx.util.Pair;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static XsdAsm.XsdAsmElements.generateMethodsForElement;
import static XsdAsm.XsdAsmUtils.*;
import static XsdAsm.XsdSupportingStructure.*;
import static org.objectweb.asm.Opcodes.*;

class XsdAsmInterfaces {

    private static final String ATTRIBUTE_CASE_SENSITIVE_DIFERENCE = "Alt";

    private List<String> createdInterfaces = new ArrayList<>();
    private Set<String> extraElementsForVisitor = new HashSet<>();
    private Map<String, AttributeHierarchyItem> attributeGroupInterfaces = new HashMap<>();
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
    void generateInterfaces(List<String> createdAttributes, String apiName) {
        attributeGroupInterfaces.keySet().forEach(attributeGroupInterface -> generateAttributesGroupInterface(createdAttributes, attributeGroupInterface, attributeGroupInterfaces.get(attributeGroupInterface), apiName));
    }

    /**
     * This method obtains the element interfaces which his class will be implementing.
     * The interfaces are represented in XsdElements as XsdGroups, and their respective
     * methods as children of the XsdGroup.
     * @param element The element from which the interfaces will be obtained.
     * @return A string array containing the names of all the interfaces this method implements in
     * interface-like names, e.g. flowContent will be IFlowContent.
     */
    private String[] getElementGroupInterfaces(XsdElement element, String apiName){
        String[] groupInterfaces = new String[0];
        XsdComplexType complexType = element.getXsdComplexType();

        if (complexType != null){
            groupInterfaces = getElementGroupInterfaces(complexType, apiName);
        }

        if (groupInterfaces == null || groupInterfaces.length == 0){
            return new String[]{ITEXT};
        }

        return groupInterfaces;
    }

    /**
     * Generates a interface with all the required methods. It uses the information gathered about in attributeGroupInterfaces.
     * @param createdAttributes A list with the names of the attribute classes already created.
     * @param attributeGroupName The interface name.
     * @param attributeHierarchyItem An object containing information about the methods of this interface and which interface, if any,
     *                               this interface extends.
     * @param apiName The api this class will belong.
     */
    private void generateAttributesGroupInterface(List<String> createdAttributes, String attributeGroupName, AttributeHierarchyItem attributeHierarchyItem, String apiName){
        String baseClassNameCamelCase = toCamelCase(attributeGroupName);
        String[] interfaces = getAttributeGroupObjectInterfaces(attributeHierarchyItem.getParentsName());
        StringBuilder signature = getAttributeGroupSignature(interfaces, apiName);

        ClassWriter interfaceWriter = generateClass(baseClassNameCamelCase, JAVA_OBJECT, interfaces, signature.toString(), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        attributeHierarchyItem.getOwnElements().forEach(elementAttribute -> {
            if (createdAttributes.stream().anyMatch(createdAttributeName -> createdAttributeName.equalsIgnoreCase(elementAttribute.getName()))){
                elementAttribute.setName(elementAttribute.getName() + ATTRIBUTE_CASE_SENSITIVE_DIFERENCE);
            }

            generateMethodsAndCreateAttribute(createdAttributes, interfaceWriter, elementAttribute, IELEMENT_TYPE_DESC, apiName);
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

        if (complexType != null){
            attributeGroups.addAll(getTypeAttributeGroups(complexType));
        }

        if (!attributeGroups.isEmpty()){
            String[] attributeGroupsArr = new String[attributeGroups.size()];
            attributeGroups.toArray(attributeGroupsArr);
            return attributeGroupsArr;
        }

        return new String[0];
    }

    private Collection<String> getTypeAttributeGroups(XsdComplexType complexType) {
        List<XsdAttributeGroup> attributeGroups = complexType.getXsdAttributes()
                .filter(attribute -> attribute.getParent() instanceof XsdAttributeGroup)
                .map(attribute -> (XsdAttributeGroup) attribute.getParent())
                .distinct()
                .collect(Collectors.toList());

        attributeGroups.addAll(complexType.getXsdAttributeGroup().collect(Collectors.toList()));

        attributeGroups.stream().distinct().forEach(this::addAttributeGroup);

        if (!attributeGroups.isEmpty()){
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
            XsdAttributeGroup parent = (XsdAttributeGroup) attributeGroup.getParent();

            if (!parents.contains(parent) && parent != null){
                parents.add(parent);
            }
        });

        if (attributeGroups.size() == 1){
            List<String> interfaces = new ArrayList<>();
            interfaces.add(getInterfaceName(toCamelCase(attributeGroups.get(0).getName())));
            return interfaces;
        }

        if (parents.size() == 0){
            return attributeGroups.stream()
                    .map(baseClass -> getInterfaceName(toCamelCase(baseClass.getName())))
                    .collect(Collectors.toList());
        }

        return getBaseAttributeGroupInterface(parents);
    }

    /**
     * Adds information about the attribute group interface to the attributeGroupInterfaces variable.
     * @param attributeGroup The attributeGroup to add.
     */
    private void addAttributeGroup(XsdAttributeGroup attributeGroup) {
        String interfaceName = getInterfaceName(attributeGroup.getName());

        if (!attributeGroupInterfaces.containsKey(interfaceName)){
            List<XsdAttribute> ownElements = attributeGroup.getXsdElements()
                    .filter(attribute -> attribute.getParent().equals(attributeGroup))
                    .map(attribute -> (XsdAttribute) attribute)
                    .collect(Collectors.toList());

            List<String> parentNames = attributeGroup.getAttributeGroups().stream().map(XsdReferenceElement::getName).collect(Collectors.toList());
            AttributeHierarchyItem attributeHierarchyItemItem = new AttributeHierarchyItem(attributeGroup.getName(), parentNames, ownElements);

            attributeGroupInterfaces.put(interfaceName, attributeHierarchyItemItem);
        }
    }

    /** Obtains the signature for the attribute group interfaces based on the implemented interfaces.
     * @param interfaces The implemented interfaces.
     * @return The signature of this interface.
     */
    private StringBuilder getAttributeGroupSignature(String[] interfaces, String apiName) {
        StringBuilder signature;

        if (interfaces.length == 0){
            signature = new StringBuilder("<T::L" + IELEMENT_TYPE + "<TT;>;>" + JAVA_OBJECT_DESC + "L" + IELEMENT_TYPE + "<TT;>;");
        } else {
            signature = new StringBuilder("<T::L" + IELEMENT_TYPE + "<TT;>;>" + JAVA_OBJECT_DESC);

            for (String anInterface : interfaces) {
                signature.append("L").append(getFullClassTypeName(anInterface, apiName)).append("<TT;>;");
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
        String[] interfaces;

        if (parentsName.size() == 0){
            interfaces = new String[]{IELEMENT};
        } else {
            interfaces = new String[parentsName.size()];

            parentsName.stream().map(parentName -> getInterfaceName(toCamelCase(parentName))).collect(Collectors.toList()).toArray(interfaces);
        }

        return interfaces;
    }

    /**
     * Obtains all the interfaces that a given element will implement.
     * @param element The element in which the class will be based.
     * @return A string array with all the interface names.
     */
    String[] getInterfaces(XsdElement element, String apiName) {
        String[] attributeGroupInterfaces =  getAttributeGroupInterfaces(element);
        String[] elementGroupInterfaces =  getElementGroupInterfaces(element, apiName);

        return ArrayUtils.addAll(attributeGroupInterfaces, elementGroupInterfaces);
    }





















    private String[] getElementGroupInterfaces(XsdComplexType complexType, String apiName) {
        XsdAbstractElement child = complexType.getXsdChildElement();

        if (child != null){
            int interfacesIndex = 0;

            Pair<String, Integer> interfaceInfo = iterativeCreation(child, toCamelCase(((XsdElement) complexType.getParent()).getName()), interfacesIndex, apiName, null);

            return new String[] {interfaceInfo.getKey()};
        }

        return null;
    }

    private Pair<String, Integer> groupMethod(String groupName, List<XsdChoice> choiceElements, List<XsdAll> allElements, List<XsdSequence> sequenceElements, String className, int interfaceIndex, String apiName){
        String interfaceName = getInterfaceName(className + "Group" + interfaceIndex);

        if (groupName != null){
            interfaceName = getInterfaceName(toCamelCase(groupName));
        }

        List<String> extendedInterfaces = new ArrayList<>();

        for (XsdAll allElement : allElements) {
            Pair<String, Integer> interfaceInfo = iterativeCreation(allElement, className, interfaceIndex + 1, apiName, groupName);

            interfaceIndex = interfaceInfo.getValue();
            extendedInterfaces.add(interfaceInfo.getKey());
        }

        for (XsdChoice choiceElement : choiceElements) {
            Pair<String, Integer> interfaceInfo = iterativeCreation(choiceElement, className, interfaceIndex + 1, apiName, groupName);

            interfaceIndex = interfaceInfo.getValue();
            extendedInterfaces.add(interfaceInfo.getKey());
        }

        for (int i = 0; i < sequenceElements.size(); i++) {
            Pair<String, Integer> interfaceInfo = iterativeCreation(sequenceElements.get(i), className, interfaceIndex + 1, apiName, groupName);

            interfaceIndex = interfaceInfo.getValue();

            if (i == 0){
                extendedInterfaces.add(interfaceInfo.getKey());
            }
        }

        String[] extendedInterfacesArr;

        if (extendedInterfaces.isEmpty()){
            extendedInterfacesArr = new String[]{ITEXT};
        } else {
            extendedInterfacesArr = new String[extendedInterfaces.size()];
            extendedInterfaces.toArray(extendedInterfacesArr);
        }

        ClassWriter classWriter = generateClass(interfaceName, JAVA_OBJECT, extendedInterfacesArr, getInterfaceSignature(extendedInterfacesArr, apiName), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        writeClassToFile(interfaceName, classWriter, apiName);

        //TODO Combinações de choices.

        return new Pair<>(interfaceName, interfaceIndex);
    }

    private Pair<String, Integer> sequenceMethod(Stream<XsdAbstractElement> xsdElements, String className, int interfaceIndex, String apiName, String groupName) {
        String interfaceNameBase = getInterfaceName(className + "Sequence");
        String interfaceName = interfaceNameBase + interfaceIndex;

        if (groupName != null){
            interfaceName = getInterfaceName(toCamelCase(groupName + "Sequence" + interfaceIndex));

            if (createdInterfaces.contains(interfaceName)){
                return new Pair<>(interfaceName, interfaceIndex);
            }
        }

        Pair<List<XsdAbstractElement>, List<String>> sequenceInfo = getSequenceInfo(xsdElements, className, interfaceIndex, 0, apiName, groupName).getKey();
        List<XsdAbstractElement> sequenceList = sequenceInfo.getKey();
        List<String> sequenceNames = sequenceInfo.getValue();

        for (int i = 0; i < sequenceList.size(); i++) {
            XsdAbstractElement sequenceElement = sequenceList.get(i);
            String sequenceName = sequenceNames.get(i);
            String currentInterfaceName = interfaceNameBase + interfaceIndex;
            String[] interfaces = new String[] {ITEXT};
            String prevName = i == 0 ? null : sequenceNames.get(i - 1);
            boolean isLast = i == sequenceList.size() - 1;

            ClassWriter classWriter = generateClass(currentInterfaceName, JAVA_OBJECT, interfaces, getClassSignature(interfaces, currentInterfaceName, apiName), ACC_PUBLIC + ACC_INTERFACE + ACC_ABSTRACT, apiName);

            if (sequenceElement instanceof XsdElement){
                String nextTypeName = className + toCamelCase(sequenceName);
                createElementsForSequence(classWriter, nextTypeName, sequenceName, apiName, className, prevName, currentInterfaceName, isLast, interfaceNameBase, interfaceIndex, (XsdElement) sequenceElement);
            }

            if (sequenceElement instanceof XsdGroup || sequenceElement instanceof XsdChoice || sequenceElement instanceof XsdAll){
                List<XsdElement> elements = getAllElementsRecursively(sequenceElement);

                List<String> elementsName = elements.stream().map(XsdReferenceElement::getName).collect(Collectors.toList());

                createElementsForSequence(classWriter, sequenceName, elementsName, apiName, className, prevName, currentInterfaceName, isLast, interfaceNameBase, interfaceIndex, elements);

                writeClassToFile(currentInterfaceName, classWriter, apiName);
            }

            ++interfaceIndex;
        }

        return new Pair<>(interfaceName, interfaceIndex);
    }

    private List<XsdElement> getAllElementsRecursively(XsdAbstractElement sequenceElement) {
        List<XsdElement> allGroupElements = new ArrayList<>();
        List<XsdAbstractElement> directElements = sequenceElement.getXsdElements().collect(Collectors.toList());

        allGroupElements.addAll(
                directElements.stream()
                              .filter(elem1 -> elem1 instanceof XsdElement)
                              .map(elem1 -> (XsdElement) elem1)
                              .collect(Collectors.toList()));

        for (XsdAbstractElement elem : directElements) {
            if (elem instanceof XsdMultipleElements && elem.getXsdElements() != null) {
                allGroupElements.addAll(getAllElementsRecursively(elem));
            }
        }

        return allGroupElements;
    }

    private void createElementsForSequence(ClassWriter classWriter, String nextTypeName, String sequenceName, String apiName, String className, String prevName,
                                           String currentInterfaceName, boolean isLast, String interfaceNameBase, int interfaceIndex, XsdElement sequenceElement) {
        List<XsdElement> elementsList = new ArrayList<>();
        List<String> elementsName = new ArrayList<>();

        elementsName.add(sequenceName);
        elementsList.add(sequenceElement);

        createElementsForSequence(classWriter, nextTypeName, elementsName, apiName, className, prevName, currentInterfaceName, isLast, interfaceNameBase, interfaceIndex, elementsList);
    }


    private void createElementsForSequence(ClassWriter classWriter, String nextTypeName, List<String> sequenceNames, String apiName, String className, String prevName,
                                           String currentInterfaceName, boolean isLast, String interfaceNameBase, int interfaceIndex, List<XsdElement> sequenceElement) {
        if (isLast){
            nextTypeName = className + "Complete";
        }

        classWriter.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC + ACC_FINAL + ACC_STATIC);

        sequenceNames.forEach(sequenceName -> generateSequenceMethod(classWriter, className, apiName, prevName, sequenceName, currentInterfaceName, isLast));

        writeClassToFile(currentInterfaceName, classWriter, apiName);

        generateSequenceInnerElement(nextTypeName, isLast, apiName, interfaceNameBase, interfaceIndex);
        //TODO ^?

        sequenceElement.forEach(element -> {
            xsdAsmInstance.generateClassFromElement(element, apiName);
            extraElementsForVisitor.add(element.getName());
        });

        extraElementsForVisitor.add(nextTypeName);
    }

    private void generateSequenceInnerElement(String nextTypeName, boolean isLast, String apiName, String interfaceNameBase, int interfaceIndex) {
        String[] nextTypeInterfaces = null;

        if (!isLast){
            nextTypeInterfaces = new String[]{ interfaceNameBase + (interfaceIndex + 1)};
        }

        ClassWriter classWriter = generateClass(nextTypeName, ABSTRACT_ELEMENT_TYPE, nextTypeInterfaces, getClassSignature(nextTypeInterfaces, nextTypeName, apiName), ACC_PUBLIC + ACC_SUPER, apiName);

        XsdAsmElements.generateClassSpecificMethods(classWriter, nextTypeName, apiName);

        writeClassToFile(nextTypeName, classWriter, apiName);
    }

    private void generateSequenceMethod(ClassWriter classWriter, String className, String apiName, String prevName, String sequenceName, String currentInterfaceName, boolean isLast) {
        String addingChildName = toCamelCase(sequenceName);
        String nextTypeName = className + toCamelCase(sequenceName);
        String addingType = getFullClassTypeName(addingChildName, apiName);
        String currentType = prevName == null ? getFullClassTypeName(className, apiName) : getFullClassTypeName(className + toCamelCase(prevName), apiName);
        String nextType = getFullClassTypeName(nextTypeName, apiName);
        String nextTypeDesc = getFullClassTypeNameDesc(nextTypeName, apiName);
        String interfaceType = getFullClassTypeName(currentInterfaceName, apiName);

        if (isLast){
            nextTypeName = className + "Complete";
            nextType = getFullClassTypeName(nextTypeName, apiName);
            nextTypeDesc = getFullClassTypeNameDesc(nextTypeName, apiName);
        }

        //TODO Receber o tipo do elemento, se o tiver.
        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, sequenceName, "(Ljava/lang/String;)" + nextTypeDesc, null, null);
        mVisitor.visitLocalVariable(sequenceName, JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, nextType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, nextType, "<init>", "()V", false);
        mVisitor.visitVarInsn(ASTORE, 2);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, interfaceType, "self", "()" + IELEMENT_TYPE_DESC, true);
        mVisitor.visitTypeInsn(CHECKCAST, currentType);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, currentType, "getChildren", "()Ljava/util/List;", false);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
        mVisitor.visitInsn(POP);
        mVisitor.visitInvokeDynamicInsn("accept", "(" + nextTypeDesc + ")Ljava/util/function/Consumer;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(Ljava/lang/Object;)V"), new Handle(Opcodes.H_INVOKEVIRTUAL, ABSTRACT_ELEMENT_TYPE, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", false), Type.getType("(" + IELEMENT_TYPE_DESC + ")V"));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "forEach", "(Ljava/util/function/Consumer;)V", true);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitTypeInsn(NEW, addingType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, addingType, "<init>", "()V", false);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, addingType, "text", "(Ljava/lang/String;)" + IELEMENT_TYPE_DESC, false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, nextType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(3, 3);
        mVisitor.visitEnd();

        extraElementsForVisitor.add(addingChildName);
    }

    private Pair<Pair<List<XsdAbstractElement>, List<String>>, Pair<Integer, Integer>> getSequenceInfo(Stream<XsdAbstractElement> xsdElements, String className, int interfaceIndex, int unnamedIndex, String apiName, String groupName){
        List<XsdAbstractElement> sequenceList = xsdElements.collect(Collectors.toList());
        List<XsdAbstractElement> allSequenceElements = new ArrayList<>();
        List<String> sequenceNames = new ArrayList<>();

        for (XsdAbstractElement element : sequenceList) {
            allSequenceElements.add(element);

            if (element instanceof XsdElement){
                if (((XsdElement) element).getName() != null){
                    sequenceNames.add(((XsdElement) element).getName());
                } else {
                    sequenceNames.add(className + "SequenceUnnamed" + unnamedIndex);
                    unnamedIndex = unnamedIndex + 1;
                }
            } else {
                if (element instanceof XsdSequence){
                    Pair<Pair<List<XsdAbstractElement>, List<String>>, Pair<Integer, Integer>> innerSequenceInfo = getSequenceInfo(element.getXsdElements(), className, interfaceIndex, unnamedIndex, apiName, groupName);

                    allSequenceElements.addAll(innerSequenceInfo.getKey().getKey());
                    sequenceNames.addAll(innerSequenceInfo.getKey().getValue());
                    interfaceIndex = innerSequenceInfo.getValue().getKey();
                    unnamedIndex = innerSequenceInfo.getValue().getValue();
                } else {
                    Pair<String, Integer> interfaceInfo = iterativeCreation(element, className, interfaceIndex + 1, apiName, groupName);

                    interfaceIndex = interfaceInfo.getValue();
                    sequenceNames.add(interfaceInfo.getKey());
                }
            }
        }

        return new Pair<>(new Pair<>(allSequenceElements, sequenceNames), new Pair<>(interfaceIndex, unnamedIndex));
    }

    private Pair<String, Integer> allMethod(List<XsdElement> directElements, String className, int interfaceIndex, String apiName, String groupName){
        String interfaceName;

        if (groupName != null){
            interfaceName = getInterfaceName(toCamelCase(groupName + "All" + interfaceIndex));

            if (createdInterfaces.contains(interfaceName)){
                return new Pair<>(interfaceName, interfaceIndex);
            }
        } else {
            interfaceName = getInterfaceName(className + "All" + interfaceIndex);
        }

        String[] extendedInterfacesArr = new String[]{ITEXT};

        ClassWriter classWriter = generateClass(interfaceName, JAVA_OBJECT, extendedInterfacesArr, getClassSignature(extendedInterfacesArr, className, apiName), ACC_PUBLIC + ACC_INTERFACE, apiName);

        directElements.forEach(child -> generateMethodsForElement(classWriter, child, getFullClassTypeName(interfaceName, apiName), IELEMENT_TYPE_DESC, apiName));

        writeClassToFile(interfaceName, classWriter, apiName);

        return new Pair<>(interfaceName, interfaceIndex);
    }

    @SuppressWarnings("unused")
    private Pair<String, Integer> choiceMethod(List<XsdChoice> choiceElements, List<XsdGroup> groupElements, List<XsdSequence> sequenceElements, List<XsdElement> directElements, String className, int interfaceIndex, String apiName, String groupName){
        String interfaceName;

        if (groupName != null){
            interfaceName = getInterfaceName(toCamelCase(groupName + "Choice" + interfaceIndex));

            if (createdInterfaces.contains(interfaceName)){
                return new Pair<>(interfaceName, interfaceIndex);
            }
        } else {
            interfaceName = getInterfaceName(className + "Choice" + interfaceIndex);
        }

        String[] extendedInterfacesArr = new String[]{ITEXT};
        List<String> extendedInterfaces = new ArrayList<>();

        for (XsdGroup groupElement : groupElements) {
            Pair<String, Integer> interfaceInfo = iterativeCreation(groupElement, className, interfaceIndex + 1, apiName, groupName);

            interfaceIndex = interfaceInfo.getValue();
            extendedInterfaces.add(interfaceInfo.getKey());
        }

        if (!extendedInterfaces.isEmpty()){
            extendedInterfacesArr = new String[extendedInterfaces.size()];
            extendedInterfaces.toArray(extendedInterfacesArr);
        }

        ClassWriter classWriter = generateClass(interfaceName, JAVA_OBJECT, extendedInterfacesArr, getInterfaceSignature(extendedInterfacesArr, apiName), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        directElements.forEach(child -> XsdAsmElements.generateMethodsForElement(classWriter, child, getFullClassTypeName(interfaceName, apiName), IELEMENT_TYPE_DESC, apiName));

        writeClassToFile(interfaceName, classWriter, apiName);
        /*
        List<String> extendedInterfaces = new ArrayList<>();

        groupElements.forEach(group -> extendedInterfaces.add(group.getName()));
        allElements.forEach(allElement -> extendedInterfaces.add(allElement.getName()));
        choiceElements.forEach(choice -> extendedInterfaces.add(choice.getName()));

        if (!sequenceElements.isEmpty()){
            extendedInterfaces.add(sequenceElements.get(0).getName());
        }
        */

        return new Pair<>(interfaceName, interfaceIndex);
    }

    private Pair<String, Integer> iterativeCreation(XsdAbstractElement element, String className, int interfaceIndex, String apiName, String groupName){
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

        Pair<String, Integer> pair = null;

        if (element instanceof XsdGroup){
            pair = groupMethod(((XsdGroup) element).getName(), choiceElements, allElements, sequenceElements, className, interfaceIndex, apiName);
        }

        if (element instanceof XsdAll){
            pair = allMethod(directElements, className, interfaceIndex, apiName, groupName);
        }

        if (element instanceof XsdChoice){
            pair = choiceMethod(choiceElements, groupElements, sequenceElements, directElements, className, interfaceIndex, apiName, groupName);
        }

        if (element instanceof  XsdSequence){
            pair = sequenceMethod(element.getXsdElements(), className, interfaceIndex, apiName, groupName);
        }

        if (pair == null){
            throw new RuntimeException("Invalid element interface type.");
        }

        createdInterfaces.add(pair.getKey());

        return pair;
    }

    Set<String> getExtraElementsForVisitor() {
        return extraElementsForVisitor;
    }
}
