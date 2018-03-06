mkdir "./src/main/java/org/xmlet/htmlapi"
mvn exec:java -D"exec.mainClass"="org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler" -D"exec.args"="./target/classes/org/xmlet/htmlapi ./src/main/java/org/xmlet/htmlapi"
rmdir "./target/classes/org" /s /q