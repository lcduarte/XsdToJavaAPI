package XsdClassGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

@Deprecated
public class JarUtils {

    public void createJar(String jarDestination, String jarName){

        File dest = new File(XsdClassGeneratorUtils.getDestinationDirectory());
        File folder = new File(jarDestination + jarName + ".jar");

        if (!folder.exists()){
            //noinspection ResultOfMethodCallIgnored
            folder.mkdirs();
        }

        File jar = new File(folder.getAbsolutePath() + "/" + jarName + ".jar");
        try {
            jar.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        createJarArchive(jar, dest.listFiles());
    }

    private static int BUFFER_SIZE = 10240;
    private static void createJarArchive(File archiveFile, File[] generatedClasses) {
        try {
            byte buffer[] = new byte[BUFFER_SIZE];
            // Open archive file
            FileOutputStream stream = new FileOutputStream(archiveFile);
            JarOutputStream out = new JarOutputStream(stream, new Manifest());

            for (File generatedClass : generatedClasses) {
                if (generatedClass == null || !generatedClass.exists()  || generatedClass.isDirectory())
                    continue; // Just in case...

                System.out.println("Adding " + generatedClass.getName());

                // Add archive entry
                JarEntry jarAdd = new JarEntry("XsdClassGenerator/ParsedObjects/" + generatedClass.getName());
                jarAdd.setTime(generatedClass.lastModified());
                out.putNextEntry(jarAdd);

                // Write file to archive
                FileInputStream in = new FileInputStream(generatedClass);
                while (true) {
                    int nRead = in.read(buffer, 0, buffer.length);
                    if (nRead <= 0)
                        break;
                    out.write(buffer, 0, nRead);
                }
                in.close();
            }

            out.close();
            stream.close();
            System.out.println("Adding completed OK");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
