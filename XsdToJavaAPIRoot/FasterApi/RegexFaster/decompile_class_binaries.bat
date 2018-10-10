if not exist "./src/main/java/org/xmlet/regexfaster" mkdir "./src/main/java/org/xmlet/regexfaster"
call mvn exec:java -D"exec.mainClass"="org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler" -D"exec.args"="-dgs=true -log=WARN ./target/classes/org/xmlet/regexfaster ./src/main/java/org/xmlet/regexfaster"
if exist "./target/classes/org" rmdir "./target/classes/org" /s /q