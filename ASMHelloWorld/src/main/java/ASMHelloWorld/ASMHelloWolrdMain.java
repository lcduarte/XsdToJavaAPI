package ASMHelloWorld;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.ASMifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

public class ASMHelloWolrdMain {

    private static DynamicClassLoader loader = new DynamicClassLoader();

    public static void main(String[] args) throws Exception {
        //constructInterfaceWImplementingClass();
        //constructClassWFieldsNMethods();

        System.out.println("Done");

        ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\ASMHelloWorld\\target\\classes\\ASMHelloWorld\\Examples\\InterfaceA.class"});
        //Textifier.main(new String[]{"C:\\Users\\Luis Duarte\\IdeaProjects\\ASMHelloWorld\\target\\classes\\ASMHelloWorld\\ASMHelloWolrdMain.class"});
    }

    private static void constructInterfaceWImplementingClass() {
        ClassWriter writerIHelloAdd = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        writerIHelloAdd.visit(V1_8, ACC_INTERFACE | ACC_PUBLIC | ACC_ABSTRACT, "ASMHelloWorld/Examples/IHelloAddGenerated",
                null,"java/lang/Object", new String[]{});

        writerIHelloAdd.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "add","(II)I",null,null);
        writerIHelloAdd.visitEnd();

        writeClassToFile("\\ASMHelloWorld\\Examples\\IHelloAddGenerated.class", writerIHelloAdd.toByteArray());

        /*
         * ************************************************************************************************
         * */

        ClassWriter writerHelloAdd = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        writerHelloAdd.visit(V1_8, ACC_PUBLIC, "ASMHelloWorld/Examples/HelloAddGenerated", null,
                "java/lang/Object", new String[]{"ASMHelloWorld/Examples/IHelloAddGenerated"});

        MethodVisitor constructor = writerHelloAdd.visitMethod(ACC_PUBLIC, "<init>","()V",null,null);

        constructor.visitCode();
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object","<init>","()V",false);

        constructor.visitInsn(RETURN);
        constructor.visitMaxs(1, 1);

        MethodVisitor mv = writerHelloAdd.visitMethod(ACC_PUBLIC,"add","(II)I",null,null);

        mv.visitCode();
        mv.visitVarInsn(ILOAD, 1);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitInsn(IADD);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(2, 3);
        writerHelloAdd.visitEnd();

        writeClassToFile("\\ASMHelloWorld\\Examples\\HelloAddGenerated.class", writerHelloAdd.toByteArray());

        Class<?> clazz = loader.defineClass("ASMHelloWorld.Examples.HelloAddGenerated", writerHelloAdd.toByteArray());

        try {
            Object inst = clazz.newInstance();
            System.out.println(clazz.getMethod("add", int.class, int.class).invoke(inst, 2, 5));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static void constructClassWFieldsNMethods(){
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, "ASMHelloWorld/Examples/ClassWFieldsNMethodsGenerated", null, "java/lang/Object", null);

        {
            fv = cw.visitField(ACC_PRIVATE, "name", "Ljava/lang/String;", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE, "age", "I", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE, "married", "Z", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/String;IZ)V", null, null);
            mv.visitCode();
            //Carregar o this
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, "ASMHelloWorld/Examples/ClassWFieldsNMethodsGenerated", "name", "Ljava/lang/String;");

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitFieldInsn(PUTFIELD, "ASMHelloWorld/Examples/ClassWFieldsNMethodsGenerated", "age", "I");

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitFieldInsn(PUTFIELD, "ASMHelloWorld/Examples/ClassWFieldsNMethodsGenerated", "married", "Z");

            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 4);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getName", "()Ljava/lang/String;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "ASMHelloWorld/Examples/ClassWFieldsNMethodsGenerated", "name", "Ljava/lang/String;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getAge", "()I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "ASMHelloWorld/Examples/ClassWFieldsNMethodsGenerated", "age", "I");
            mv.visitInsn(IRETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "isMarried", "()Z", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "ASMHelloWorld/Examples/ClassWFieldsNMethodsGenerated", "married", "Z");
            mv.visitInsn(IRETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "setName", "(Ljava/lang/String;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, "ASMHelloWorld/Examples/ClassWFieldsNMethodsGenerated", "name", "Ljava/lang/String;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "setAge", "(I)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitFieldInsn(PUTFIELD, "ASMHelloWorld/Examples/ClassWFieldsNMethodsGenerated", "age", "I");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "setMarried", "(Z)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitFieldInsn(PUTFIELD, "ASMHelloWorld/Examples/ClassWFieldsNMethodsGenerated", "married", "Z");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        cw.visitEnd();

        writeClassToFile("\\ASMHelloWorld\\Examples\\ClassWFieldsNMethodsGenerated.class", cw.toByteArray());
    }

    private static void writeClassToFile(String path, byte[] constructedClass){
        try {
            FileOutputStream os = new FileOutputStream(new File(ASMHelloWolrdMain.class.getClassLoader().getResource("").getPath() + path));
            os.write(constructedClass);
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class DynamicClassLoader extends ClassLoader {
    Class<?> defineClass(String name, byte[] b) {
        return defineClass(name, b, 0, b.length);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }
}