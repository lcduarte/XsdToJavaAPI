package org.xmlet.xsdasm.main;

import org.xmlet.xsdasm.classes.XsdAsm;
import org.xmlet.xsdparser.core.XsdParser;

public class XsdAsmMain {

    //TODO Ver se o facto das cenas n aparecerem clicable na target significa alguma coisa.
    //TODO Resolver a cena das interfaces que derivam de coisas que tem o mesmo método.
    //TODO Tentar remover interfaces que estejam a mais.

    public static void main(String[] args){
        if (args.length == 2){
            new XsdAsm().generateClassFromElements(new XsdParser().parse(args[0]), args[1]);
        }
    }
}
