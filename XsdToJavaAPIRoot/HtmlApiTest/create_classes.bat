if exist "./src/main/java" rmdir "./src/main/java" /s /q
mkdir "./src/main/java/org/xmlet/htmlapi"
call mvn exec:java -D"exec.mainClass"="org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler" -D"exec.args"="-dgs=true ./../HtmlApi/target/classes/org/xmlet/htmlapi ./src/main/java/org/xmlet/htmlapi"
ECHO ON