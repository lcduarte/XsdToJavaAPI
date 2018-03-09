rmdir "./src/main/java" /s /q
mkdir "./target/classes/org/xmlet/htmlapi"
mvn exec:java -D"exec.mainClass"="org.xmlet.xsdasm.main.XsdAsmMain" -D"exec.args"="./src/main/resources/html_5.xsd htmlapi"