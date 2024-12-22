package config;
import java.io.*;
import java.util.*;

public class Conf {

    private static Map<String, String> users = new HashMap<>();  // Pour stocker les utilisateurs et mots de passe
    private static String host;
    private static int port;
    private static int portBackup;
    
    public static int getPortBackup() {
        return portBackup;
    }

    private static String filenamesauvegarde;

    // Charger les informations depuis le fichier de configuration
    public static void chargerConfiguration() {
        String fichierConf = "conf.txt";  // Assurez-vous que le fichier est dans le bon répertoire
        try (BufferedReader reader = new BufferedReader(new FileReader(fichierConf))) {
            String ligne;
            boolean isUserSection = false;

            while ((ligne = reader.readLine()) != null) {
                ligne = ligne.trim();

                // Ignorer les lignes vides ou celles qui commencent par un commentaire
                if (ligne.isEmpty() || ligne.startsWith("#")) {
                    continue;
                }

                // Lire l'hôte
                if (ligne.startsWith("host=")) {
                    host = ligne.split("=")[1].trim();
                }

                // Lire le port
                if (ligne.startsWith("port=")) {
                    port = Integer.parseInt(ligne.split("=")[1].trim());
                }

                // Lire le port backup
                if (ligne.startsWith("portbackup=")) {
                    portBackup = Integer.parseInt(ligne.split("=")[1].trim());
                }
                // Lire le fichier de sauvegarde
                if (ligne.startsWith("filenamesauvegarde=")) {
                    filenamesauvegarde = ligne.split("=")[1].trim();
                }

                // Gérer les utilisateurs et leurs mots de passe
                if (ligne.startsWith("user") || isUserSection) {
                    if (ligne.startsWith("user")) {
                        isUserSection = true;
                    }

                    if (isUserSection) {
                        String[] parts = ligne.split("=");
                        if (parts.length == 2) {
                            String user = parts[0].trim();
                            String password = parts[1].trim();
                            users.put(user, password);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier de configuration : " + e.getMessage());
        }
    }

    public static String getHost() {
        return host;
    }

    public static int getPort() {
        return port;
    }

    public static String getFilenamesauvegarde() {
        return filenamesauvegarde;
    }

    // Getter pour obtenir les utilisateurs et leurs mots de passe
    public static Map<String, String> getUsers() {
        return users;
    }

    public static boolean estUtilisateur(String username, String password) {
        // Vérifier si l'utilisateur existe dans la map
        if (users.containsKey(username)) {
            // Vérifier si le mot de passe correspond à celui stocké
            return users.get(username).equals(password);
        }
        // Si l'utilisateur n'existe pas dans la map
        return false;
        
    }

    public static void main(String[] args) {
        chargerConfiguration();
        System.out.println(getPort());
        System.out.println(getPortBackup());        
    }
}
