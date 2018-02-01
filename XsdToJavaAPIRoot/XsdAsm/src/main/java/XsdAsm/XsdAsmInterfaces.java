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
    private List<String> extraElementsForVisitor = new ArrayList<>();
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
        String[] typeInterfaces = new String[0], groupInterfaces = new String[0];
        XsdAbstractElement elementType = element.getXsdType();
        XsdComplexType complexType = element.getXsdComplexType();

        if (elementType != null && elementType instanceof XsdComplexType){
            typeInterfaces = getElementGroupInterfaces((XsdComplexType) elementType, apiName);
        }

        if (complexType != null){
            groupInterfaces = getElementGroupInterfaces(complexType, apiName);
        }

        String[] interfaces = ArrayUtils.addAll(typeInterfaces, groupInterfaces);

        if (interfaces.length == 0){
            return new String[]{ITEXT};
        }

        return interfaces;
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
        XsdAbstractElement elementType = element.getXsdType();
        XsdComplexType complexType = element.getXsdComplexType();

        if (elementType != null && elementType instanceof XsdComplexType){
            attributeGroups.addAll(getTypeAttributeGroups((XsdComplexType) elementType));
        }

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

        attributeGroups.addAll(complexType.getXsdAttributeGroup());

        attributeGroups.stream().distinct().forEach(this::addAttributeGroup);

        if (!attributeGroups.isEmpty()){
            return getBaseAttributeGroupInterface(complexType.getXsdAttributeGroup());
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

        if (((XsdElement) complexType.getParent()).getName().equals("div")){
            int a  = 5;
        }

        if (child != null){
            int interfacesIndex = 0;

            Pair<String, Integer> interfaceInfo = iterativeCreation(child, toCamelCase(((XsdElement) complexType.getParent()).getName()), interfacesIndex, apiName, this);

            return new String[] {interfaceInfo.getKey()};
        }

        return null;
    }

    //TODO Passar a aceitar o name do group, ex. FlowContent.
    @SuppressWarnings("unused")
    private static Pair<String, Integer> groupMethod(List<XsdChoice> choiceElements, List<XsdGroup> _1, List<XsdAll> allElements, List<XsdSequence> sequenceElements, List<XsdElement> _2, String className, int interfaceIndex, String apiName, XsdAsmInterfaces instance){
        String interfaceName = className + "Group" + interfaceIndex;
        List<String> extendedInterfaces = new ArrayList<>();

        for (XsdAll allElement : allElements) {
            Pair<String, Integer> interfaceInfo = iterativeCreation(allElement, className, interfaceIndex + 1, apiName, instance);

            interfaceIndex = interfaceInfo.getValue();
            extendedInterfaces.add(interfaceInfo.getKey());
        }

        for (XsdChoice choiceElement : choiceElements) {
            Pair<String, Integer> interfaceInfo = iterativeCreation(choiceElement, className, interfaceIndex + 1, apiName, instance);

            interfaceIndex = interfaceInfo.getValue();
            extendedInterfaces.add(interfaceInfo.getKey());
        }

        for (int i = 0; i < sequenceElements.size(); i++) {
            Pair<String, Integer> interfaceInfo = iterativeCreation(sequenceElements.get(i), className, interfaceIndex + 1, apiName, instance);

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

        ClassWriter classWriter = generateClass(interfaceName, JAVA_OBJECT, extendedInterfacesArr, getClassSignature(extendedInterfacesArr, className, apiName), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        writeClassToFile(interfaceName, classWriter, apiName);

        //TODO Combinações de choices.

        return new Pair<>(interfaceName, interfaceIndex);
    }

    private static Pair<String, Integer> sequenceMethod(Stream<XsdAbstractElement> xsdElements, String className, int interfaceIndex, String apiName, XsdAsmInterfaces instance) {
        String interfaceNameBase = className + "Sequence";
        String interfaceName = className + "Sequence" + interfaceIndex;
        List<XsdAbstractElement> sequenceList = xsdElements.collect(Collectors.toList());
        List<String> sequenceNames = new ArrayList<>();
        int unnamedIndex = 0;


        for (XsdAbstractElement element : sequenceList) {
            if (element instanceof XsdElement){
                if (((XsdElement) element).getName() != null){
                    sequenceNames.add(((XsdElement) element).getName());
                } else {
                    sequenceNames.add(className + "SequenceUnnamed" + unnamedIndex);
                    unnamedIndex = unnamedIndex + 1;
                }
            } else {
                Pair<String, Integer> interfaceInfo = iterativeCreation(element, className, interfaceIndex + 1, apiName, instance);

                interfaceIndex = interfaceInfo.getValue();
                sequenceNames.add(interfaceInfo.getKey());
            }
        }

        for (int i = 0; i < sequenceList.size(); i++) {
            XsdAbstractElement sequenceElement = sequenceList.get(i);
            String sequenceName = sequenceNames.get(i);
            String currentInterfaceName = interfaceNameBase + interfaceIndex;

            String[] interfaces = new String[] {ITEXT};
            boolean isElement = sequenceElement instanceof XsdElement;

            if (!isElement){
                interfaces = new String[]{sequenceName};
            }

            ClassWriter classWriter = generateClass(currentInterfaceName, JAVA_OBJECT, interfaces, getClassSignature(interfaces, currentInterfaceName, apiName), ACC_PUBLIC + ACC_INTERFACE + ACC_ABSTRACT, apiName);

            //classWriter.visit(V1_8, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, "Samples/Sequence/Interfaces/PersonalInfoSequence1", "Ljava/lang/Object;LSamples/HTML/IElement<LSamples/Sequence/Classes/PersonalInfo;>;", "java/lang/Object", new String[] { "Samples/HTML/IElement" });

            if (isElement){
                String addingChildName = toCamelCase(sequenceName);
                String addingType = getFullClassTypeName(addingChildName, apiName);
                String currentType = i == 0 ? getFullClassTypeName(className, apiName) : getFullClassTypeName(className + toCamelCase(sequenceNames.get(i - 1)), apiName);
                String nextTypeName = className + addingChildName;
                String nextType = getFullClassTypeName(nextTypeName, apiName);
                String nextTypeDesc = getFullClassTypeNameDesc(nextTypeName, apiName);
                String interfaceType = getFullClassTypeName(currentInterfaceName, apiName);

                if (i == sequenceList.size() - 1){
                    nextTypeName = className + "Complete";
                    nextType = getFullClassTypeName(nextTypeName, apiName);
                    nextTypeDesc = getFullClassTypeNameDesc(nextTypeName, apiName);
                }

                classWriter.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC + ACC_FINAL + ACC_STATIC);

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

                writeClassToFile(currentInterfaceName, classWriter, apiName);

                String[] nextTypeInterfaces = null;

                if (i != sequenceList.size() - 1){
                    nextTypeInterfaces = new String[]{ interfaceNameBase + (interfaceIndex + 1)};
                }

                classWriter = generateClass(nextTypeName, ABSTRACT_ELEMENT_TYPE, nextTypeInterfaces, getClassSignature(nextTypeInterfaces, nextTypeName, apiName), ACC_PUBLIC + ACC_SUPER, apiName);
                
                XsdAsmElements.generateClassSpecificMethods(classWriter, nextTypeName, apiName);

                writeClassToFile(nextTypeName, classWriter, apiName);

                instance.xsdAsmInstance.generateClassFromElement((XsdElement) sequenceElement, apiName);

                instance.extraElementsForVisitor.add(addingChildName);
                instance.extraElementsForVisitor.add(nextTypeName);

                ++interfaceIndex;
            }

        }

        return new Pair<>(interfaceName, interfaceIndex);
    }

    @SuppressWarnings("unused")
    private static Pair<String, Integer> allMethod(List<XsdChoice> _1, List<XsdGroup> _2, List<XsdAll> _3, List<XsdSequence> _4, List<XsdElement> directElements, String className, int interfaceIndex, String apiName, XsdAsmInterfaces _5){
        String interfaceName = className + "All" + interfaceIndex;
        String[] extendedInterfacesArr = new String[]{IELEMENT};

        ClassWriter classWriter = generateClass(interfaceName, JAVA_OBJECT_DESC, extendedInterfacesArr, getClassSignature(extendedInterfacesArr, className, apiName), ACC_PUBLIC + ACC_INTERFACE, apiName);

        directElements.forEach(child -> generateMethodsForElement(classWriter, child, getFullClassTypeName(interfaceName, apiName), IELEMENT_TYPE_DESC, apiName));

        writeClassToFile(interfaceName, classWriter, apiName);

        return new Pair<>(interfaceName, interfaceIndex);
    }

    @SuppressWarnings("unused")
    private static Pair<String, Integer> choiceMethod(List<XsdChoice> choiceElements, List<XsdGroup> groupElements, List<XsdAll> _1, List<XsdSequence> sequenceElements, List<XsdElement> directElements, String className, int interfaceIndex, String apiName, XsdAsmInterfaces instance){
        String interfaceName = className + "Choice" + interfaceIndex;
        String[] extendedInterfacesArr = new String[]{ITEXT};
        List<String> extendedInterfaces = new ArrayList<>();

        for (XsdGroup groupElement : groupElements) {
            Pair<String, Integer> interfaceInfo = iterativeCreation(groupElement, className, interfaceIndex + 1, apiName, instance);

            interfaceIndex = interfaceInfo.getValue();
            extendedInterfaces.add(interfaceInfo.getKey());
        }

        if (!extendedInterfaces.isEmpty()){
            extendedInterfacesArr = new String[extendedInterfaces.size()];
            extendedInterfaces.toArray(extendedInterfacesArr);
        }

        ClassWriter classWriter = generateClass(interfaceName, JAVA_OBJECT, extendedInterfacesArr, getInterfaceSignature(extendedInterfacesArr, className, apiName), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

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

    private static String getInterfaceSignature(String[] interfaces, String className, String apiName) {
        StringBuilder signature = new StringBuilder("<T::" + IELEMENT_TYPE_DESC + ">Ljava/lang/Object;");

        if (interfaces != null){
            for (String anInterface : interfaces) {
                signature.append("L")
                        .append(getFullClassTypeName(anInterface, apiName))
                        .append("<TT;>;");
            }
        }

        return signature.toString();
    }

    //TODO Repensar esta coisa de métodos
    private static Pair<String, Integer> iterativeCreation(XsdAbstractElement element, String className, int interfaceIndex, String apiName, XsdAsmInterfaces instance){
        List<XsdChoice> choiceElements = new ArrayList<>();
        List<XsdGroup> groupElements = new ArrayList<>();
        List<XsdAll> allElements = new ArrayList<>();
        List<XsdSequence> sequenceElements = new ArrayList<>();
        List<XsdElement> directElements = new ArrayList<>();

        Map<Class, FourParameterConsumer<List<XsdChoice>, List<XsdGroup>, List<XsdAll>, List<XsdSequence>, List<XsdElement>>> mapper1 = new HashMap<Class, FourParameterConsumer<List<XsdChoice>, List<XsdGroup>, List<XsdAll>, List<XsdSequence>, List<XsdElement>>>();
        Map<Class, List> mapper2 = new HashMap<>();

        mapper1.put(XsdGroup.class, XsdAsmInterfaces::groupMethod);
        mapper1.put(XsdChoice.class, XsdAsmInterfaces::choiceMethod);
        mapper1.put(XsdAll.class, XsdAsmInterfaces::allMethod);

        mapper2.put(XsdGroup.class, groupElements);
        mapper2.put(XsdChoice.class, choiceElements);
        mapper2.put(XsdAll.class, allElements);
        mapper2.put(XsdSequence.class, sequenceElements);
        mapper2.put(XsdElement.class, directElements);

        if (className.equals("Div") || className.equals("div")){
            int a  = 5;
        }

        element.getXsdElements()
                .forEach(elementChild ->
                        mapper2.get(elementChild.getClass()).add(elementChild)
                );

        Pair<String, Integer> pair = null;

        if (mapper1.containsKey(element.getClass())){

            pair = mapper1.get(element.getClass()).apply(choiceElements, groupElements, allElements, sequenceElements, directElements, className, interfaceIndex, apiName, instance);
        }

        if (element.getClass().equals(XsdSequence.class)){
            pair = sequenceMethod(element.getXsdElements(), className, interfaceIndex, apiName, instance);
        }

        return pair;
    }

    @FunctionalInterface
    interface FourParameterConsumer<T, U, V, W, Z> {
        Pair<String, Integer> apply(T t, U u, V v, W w, Z z, String className, int interfaceIndex, String apiName, XsdAsmInterfaces instance);
    }

    public List<String> getExtraElementsForVisitor() {
        return extraElementsForVisitor;
    }
}
