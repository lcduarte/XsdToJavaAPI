import XsdClassGenerator.XsdClassGenerator;
import XsdParser.XsdParser;

public class XsdClassGeneratorMain {

    public static void main(String[] args){
        if (args.length == 2){
            System.out.println("arg[0] = " + args[0]);
            System.out.println("arg[1] = " + args[1]);
            new XsdClassGenerator().generateClassFromElements(new XsdParser().parse(args[0]), args[1]);
        }
    }
}
