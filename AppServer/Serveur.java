package serveur;

import config.Conf;
import java.io.*;
import java.net.*;
import java.util.*;
import moteur.*;

public class Serveur {

    private static Set<Socket> clientSockets = new HashSet<>();  // Tableau pour stocker les sockets client

    public static void main(String[] args) {
        // Charger la configuration avant d'ouvrir le serveur
        Conf.chargerConfiguration();
        Database.chargerRelationsDepuisFichier();

        String host = Conf.getHost();
        int port = Conf.getPort();

        if (port == 0) {
            System.err.println("Erreur : le port est invalide (0). Veuillez vérifier la configuration.");
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serveur prêt sur " + host + ":" + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Erreur serveur : " + e.getMessage());
        }
    }

    private static void handleClient(Socket socketClient) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            PrintWriter out = new PrintWriter(socketClient.getOutputStream(), true)
        ) {
            out.println("Entrez votre nom d'utilisateur :");
            String username = in.readLine();

            out.println("Entrez votre mot de passe :");
            String password = in.readLine();

            // Vérifier l'authentification
            if (!Conf.estUtilisateur(username, password)) {
                out.println("Authentification échouée. Connexion fermée.");
                socketClient.close();
                return;
            }

            out.println("Bienvenue " + username + ", vous êtes connecté à MiniSQL.");

            // Ajouter le socket client à la liste
            clientSockets.add(socketClient);

            // Récupérer et traiter les messages du client
            recupMessClient(socketClient, in, out);
        } catch (IOException e) {
            System.err.println("Erreur de communication avec le client : " + e.getMessage());
        } finally {
            try {
                clientSockets.remove(socketClient);
                socketClient.close();
            } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture du socket : " + e.getMessage());
            }
        }
    }

    public static void recupMessClient(Socket socketClient, BufferedReader in, PrintWriter out) {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Message reçu : " + message);
                try {
                    String mesrtr = Relation.requete(message);
                    out.println(mesrtr);  // Réponse normale
                } catch (Exception e) {  // Capturer toutes les exceptions
                    // Enregistrer l'erreur dans un fichier erreur.txt
                    try (FileWriter fw = new FileWriter("erreur.txt", true);
                         BufferedWriter bw = new BufferedWriter(fw)) {
                        bw.write("Erreur dans la commande : " + message + " | Erreur : " + e.toString());
                        bw.newLine();
                    } catch (IOException fileErr) {
                        System.err.println("Erreur lors de l'écriture dans le fichier erreur.txt : " + fileErr.getMessage());
                    }
                    out.println("Erreur de syntaxe. Commande non exécutée.");
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur d'entrée/sortie avec le client : " + e.getMessage());
        }
    }
}
