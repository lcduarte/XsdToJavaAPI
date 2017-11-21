package ASMHelloWorld.Examples;

public interface InterfaceA {

    default public String getHi(){
        return new String("Hi");
    }

}
