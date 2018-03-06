rmdir "./src/main/java" /s /q
mvn exec:java -D"exec.mainClass"="org.xmlet.xsdasm.main.XsdAsmMain" -D"exec.args"="./src/main/resources/html_5.xsd htmlapi"