package org.xmlet.xsdasmfaster.classes;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.xmlet.xsdparser.xsdelements.XsdAttribute;
import org.xmlet.xsdparser.xsdelements.XsdList;
import org.xmlet.xsdparser.xsdelements.XsdRestriction;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;
import static org.xmlet.xsdasmfaster.classes.XsdAsmEnum.*;
import static org.xmlet.xsdasmfaster.classes.XsdAsmUtils.*;
import static org.xmlet.xsdasmfaster.classes.XsdSupportingStructure.*;

class XsdAsmAttributes {

    private static Map<String, Consumer<MethodVisitor>> adjustmentsMapper;

    static{
        adjustmentsMapper = new HashMap<>();

        adjustmentsMapper.put("Ljava/lang/Integer;", XsdAsmAttributes::intAdjustment);
        adjustmentsMapper.put("Ljava/lang/Float;", XsdAsmAttributes::floatAdjustment);
        adjustmentsMapper.put("Ljava/lang/Long;", XsdAsmAttributes::longAdjustment);
        adjustmentsMapper.put("Ljava/lang/Short;", XsdAsmAttributes::shortAdjustment);
    }

    private XsdAsmAttributes(){}

    /**
     * Generates a method to add a given attribute.
     * @param classWriter The class where the fields will be added.
     * @param elementAttribute The attribute containing the information to create the method. (Only String fields are being supported)
     */
    static void generateMethodsForAttribute(ClassWriter classWriter, XsdAttribute elementAttribute, String returnType, String className, String apiName) {
        String attributeName = ATTRIBUTE_PREFIX + getCleanName(elementAttribute);
        String camelCaseName = attributeName.toLowerCase().charAt(0) + attributeName.substring(1);
        String attributeClassType = getFullClassTypeName(getAttributeName(elementAttribute), apiName);
        String attributeGroupInterfaceType = getFullClassTypeName(className, apiName);
        boolean isInterfaceMethod = isInterfaceMethod(returnType);
        String attrName = firstToLower(ATTRIBUTE_PREFIX + getCleanName(elementAttribute));
        String javaType = getFullJavaType(elementAttribute);
        String containingType;
        String signature;

        if (attributeHasEnum(elementAttribute)){
            javaType = getFullClassTypeNameDesc(getEnumName(elementAttribute), apiName);
        }

        containingType = javaType;

        if (isInterfaceMethod){
            signature = "(" + javaType + ")TT;";
        } else {
            signature = "(" + javaType + ")" + returnType.substring(0, returnType.length() - 1) + "<TZ;>;";
        }

        int access = isInterfaceMethod ? ACC_PUBLIC : ACC_PUBLIC + ACC_FINAL;

        MethodVisitor mVisitor = classWriter.visitMethod(access, camelCaseName, "(" + javaType + ")" + returnType, signature, null);
        mVisitor.visitLocalVariable(attrName, javaType, null, new Label(), new Label(),1);
        mVisitor.visitCode();

        if (attributeHasRestrictions(elementAttribute)){
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKESTATIC, attributeClassType, "validateRestrictions", "(" + javaType + ")V", false);
        }

        mVisitor.visitVarInsn(ALOAD, 0);

        if (isInterfaceMethod){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, attributeGroupInterfaceType, "getVisitor", "()" + elementVisitorTypeDesc, true);
        } else {
            mVisitor.visitFieldInsn(GETFIELD, attributeGroupInterfaceType, "visitor", elementVisitorTypeDesc);
        }

        //mVisitor.visitLdcInsn(elementAttribute.getRawName());
        mVisitor.visitVarInsn(ALOAD, 1);

        if (attributeHasEnum(elementAttribute)){
            containingType = getEnumContainingType(elementAttribute);

            mVisitor.visitMethodInsn(INVOKEVIRTUAL, javaType.substring(1, javaType.length() - 1), "getValue", "()" + containingType, false);
        }

        if (containingType != null && !containingType.equals(JAVA_STRING_DESC)){
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, JAVA_OBJECT, "toString", "()" + JAVA_STRING_DESC, false);
        }

        mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, "visitAttribute" + getCleanName(elementAttribute.getName()), "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);

        if (isInterfaceMethod){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, attributeGroupInterfaceType, "self", "()" + elementTypeDesc, true);
        }

        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();
    }

    /**
     * Creates a class which represents an attribute.
     * @param attribute The XsdAttribute type that contains the required information.
     * @param apiName The api this class will belong.
     */
    static void generateAttribute(XsdAttribute attribute, String apiName){
        String camelAttributeName = getAttributeName(attribute);
        XsdList list = getAttributeList(attribute);
        String javaType = list != null ? JAVA_LIST_DESC : getFullJavaType(attribute);

        boolean hasEnum = attributeHasEnum(attribute);

        if (hasEnum){
            getAttributeRestrictions(attribute)
                    .stream()
                    .filter(restriction -> restriction.getEnumeration() != null)
                    .forEach(restriction -> createEnum(attribute, restriction.getEnumeration(), apiName));
        }

        if (attributeHasRestrictions(attribute)){
            ClassWriter attributeWriter = generateClass(camelAttributeName, JAVA_OBJECT, null, null, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, apiName);

            MethodVisitor mVisitor = attributeWriter.visitMethod(ACC_PRIVATE, CONSTRUCTOR, "()V", null, null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(1, 1);
            mVisitor.visitEnd();

            if (hasEnum){
                String enumTypeDesc = getFullClassTypeNameDesc(getEnumName(attribute), apiName);

                mVisitor = attributeWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "validateRestrictions", "(" + enumTypeDesc + ")V", null, null);
            } else {
                String signature = list != null ? "(L" + JAVA_LIST + "<" + getFullJavaType(list.getItemType()) + ">;)V" : null;

                mVisitor = attributeWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "validateRestrictions", "(" + javaType + ")V", signature, null);
            }

            mVisitor.visitCode();

            if (hasEnum){
                String enumName = getEnumName(attribute);
                String enumType = getFullClassTypeName(enumName, apiName);

                mVisitor.visitVarInsn(ALOAD, 0);
                mVisitor.visitMethodInsn(INVOKEVIRTUAL, enumType, "getValue", "()" + getEnumContainingType(attribute), false);
                mVisitor.visitVarInsn(ASTORE, 1);
            }

            loadRestrictionsToAttribute(attribute, mVisitor, javaType, hasEnum);

            writeClassToFile(camelAttributeName, attributeWriter, apiName);
        }
    }

    /**
     * Loads all the existing restrictions to the attribute class. It inserts entries in a list on the static
     * constructor of the class. The maxStack value is three due to the way maps work, the put action always
     * require three values, the map object, the key and the value.
     */
    private static void loadRestrictionsToAttribute(XsdAttribute attribute, MethodVisitor mVisitor, String javaType, boolean hasEnum) {
        getAttributeRestrictions(attribute).forEach(restriction -> loadRestrictionToAttribute(mVisitor, restriction, javaType, hasEnum ? 1 : 0));

        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, hasEnum ? 2 : 1);
        mVisitor.visitEnd();
    }

    /**
     * Loads the XsdRestriction object information to the static constructor.
     * @param mVisitor The static constructor method visitor.
     * @param restriction The current restriction to add.
     * @param index The current index of the stack.
     */
    private static void loadRestrictionToAttribute(MethodVisitor mVisitor, XsdRestriction restriction, String javaType, int index) {
        XsdLength length = restriction.getLength();
        XsdMaxLength maxLength = restriction.getMaxLength();
        XsdMinLength minLength = restriction.getMinLength();
        XsdFractionDigits fractionDigits = restriction.getFractionDigits();
        XsdMaxExclusive maxExclusive = restriction.getMaxExclusive();
        XsdMaxInclusive maxInclusive = restriction.getMaxInclusive();
        XsdMinExclusive minExclusive = restriction.getMinExclusive();
        XsdMinInclusive minInclusive = restriction.getMinInclusive();
        XsdPattern pattern = restriction.getPattern();
        XsdTotalDigits totalDigits = restriction.getTotalDigits();

        if (length != null){
            mVisitor.visitIntInsn(BIPUSH, length.getValue());
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateLength", "(I" + javaType + ")V", false);
        }

        if (maxLength != null){
            mVisitor.visitLdcInsn(maxLength.getValue());
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateMaxLength", "(I" + javaType + ")V", false);
        }

        if (minLength != null){
            mVisitor.visitLdcInsn(minLength.getValue());
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateMinLength", "(I" + javaType + ")V", false);
        }

        if (maxExclusive != null){
            mVisitor.visitLdcInsn(maxExclusive.getValue());
            mVisitor.visitVarInsn(ALOAD, index);
            numericAdjustment(mVisitor, javaType);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateMaxExclusive", "(DD)V", false);
        }

        if (maxInclusive != null){
            mVisitor.visitLdcInsn(maxInclusive.getValue());
            mVisitor.visitVarInsn(ALOAD, index);
            numericAdjustment(mVisitor, javaType);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateMaxInclusive", "(DD)V", false);
        }

        if (minExclusive != null){
            mVisitor.visitLdcInsn(minExclusive.getValue());
            mVisitor.visitVarInsn(ALOAD, index);
            numericAdjustment(mVisitor, javaType);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateMinExclusive", "(DD)V", false);
        }

        if (minInclusive != null){
            mVisitor.visitLdcInsn(minInclusive.getValue());
            mVisitor.visitVarInsn(ALOAD, index);
            numericAdjustment(mVisitor, javaType);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateMinInclusive", "(DD)V", false);
        }

        if (fractionDigits != null){
            mVisitor.visitIntInsn(BIPUSH, fractionDigits.getValue());
            mVisitor.visitVarInsn(ALOAD, index);
            numericAdjustment(mVisitor, javaType);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateFractionDigits", "(ID)V", false);
        }

        if (totalDigits != null){
            mVisitor.visitIntInsn(BIPUSH, totalDigits.getValue());
            mVisitor.visitVarInsn(ALOAD, index);
            numericAdjustment(mVisitor, javaType);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateTotalDigits", "(ID)V", false);
        }

        if (pattern != null){
            mVisitor.visitLdcInsn(pattern.getValue());
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validatePattern", "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")V", false);
        }
    }

    private static void numericAdjustment(MethodVisitor mVisitor, String javaType) {
        adjustmentsMapper.getOrDefault(javaType, XsdAsmAttributes::doubleAdjustment).accept(mVisitor);
    }

    private static void intAdjustment(MethodVisitor mVisitor) {
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "doubleValue", "()D", false);
    }

    private static void floatAdjustment(MethodVisitor mVisitor) {
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "doubleValue", "()D", false);
    }

    private static void shortAdjustment(MethodVisitor mVisitor) {
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "doubleValue", "()D", false);
    }

    private static void longAdjustment(MethodVisitor mVisitor) {
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "doubleValue", "()D", false);
    }

    private static void doubleAdjustment(MethodVisitor mVisitor) {
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
    }

    private static boolean attributeHasRestrictions(XsdAttribute attribute){
        List<XsdRestriction> restrictions = getAttributeRestrictions(attribute);

        //noinspection SimplifiableIfStatement
        if (restrictions.isEmpty()){
            return false;
        }

        return restrictions.stream().anyMatch(XsdAsmAttributes::hasRestrictionsOtherThanEnumeration);
    }

    private static boolean hasRestrictionsOtherThanEnumeration(XsdRestriction restriction){
        return restriction.getMinExclusive() != null || restriction.getMinInclusive() != null ||
                restriction.getMaxExclusive() != null || restriction.getMaxInclusive() != null ||
                restriction.getMaxLength() != null || restriction.getMinLength() != null ||
                restriction.getLength() != null || restriction.getPattern() != null ||
                restriction.getTotalDigits() != null || restriction.getFractionDigits() != null;
    }

    /**
     * AttrType (Object/String/Integer)
     * AttrTypeContentType(EnumTypeContentType) NAMED
     * AttrTypeStyle(EnumTypeStyle)             NO NAME
     */
    private static String getAttributeName(XsdAttribute attribute) {
        String name = ATTRIBUTE_PREFIX + getCleanName(attribute);

        if (attributeHasEnum(attribute)) {
            return name + getEnumName(attribute).replaceAll(name, "");
        }

        String javaType = getFullJavaType(attribute);

        return name + javaType.substring(javaType.lastIndexOf('/') + 1, javaType.length() - 1);
    }

    private static XsdList getAttributeList(XsdAttribute attribute) {
        return attribute.getXsdSimpleType() != null ? attribute.getXsdSimpleType().getList() : null;
    }

    private static String getEnumContainingType(XsdAttribute attribute){
        if (attributeHasEnum(attribute)){
            List<XsdRestriction> restrictions = getAttributeRestrictions(attribute);

            return getJavaType(restrictions.get(0).getBase());
        }

        return null;
    }
}
