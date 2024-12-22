package moteur;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileCopy {

    public static void copyFile(String sourceFilePath, String destinationFilePath) throws IOException {
        // Flux d'entrée et de sortie pour la lecture et l'écriture du fichier
        try (InputStream in = new FileInputStream(sourceFilePath);
             OutputStream out = new FileOutputStream(destinationFilePath)) {
             
            byte[] buffer = new byte[1024]; // Taille du tampon pour la copie
            int length;
            
            // Lire et écrire le fichier en blocs
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }
}
