package org.xmlet.xsdasm.main;

import org.xmlet.xsdasm.classes.XsdAsm;
import org.xmlet.xsdparser.core.XsdParser;

public class XsdAsmMain {

    //TODO Tentar remover interfaces que estejam a mais.

    public static void main(String[] args){
        if (args.length == 2){
            new XsdAsm().generateClassFromElements(new XsdParser().parse(args[0]), args[1]);
        }
    }
}
