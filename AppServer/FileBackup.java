package moteur;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileBackup {

    public static void backupFile(String sourceFilePath) {
        // Obtenir la date et l'heure actuelles pour générer un nom unique, y compris la seconde
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String destinationFileName = "backup/backup_" + timestamp + ".txt";  // Le nom du fichier de sauvegarde

        // Création du fichier de destination
        File sourceFile = new File(sourceFilePath);
        File destinationFile = new File(destinationFileName);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destinationFile))) {

            // Lire le contenu du fichier source et l'écrire dans le fichier de destination
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }

            System.out.println("Backup effectué avec succès ! Le fichier de sauvegarde est : " + destinationFileName);
        } catch (IOException e) {
            System.err.println("Une erreur est survenue lors de la création du backup : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Exemple d'utilisation : sauvegarder un fichier spécifique
        String sourceFilePath = "sauvegardes.txt";  // Remplacez par le chemin réel du fichier
        backupFile(sourceFilePath);
    }
}
