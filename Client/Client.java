// Client.java
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Entrez host : ");
        String host = scanner.nextLine();  
        
        System.out.print("Entrez port : ");
        int port = scanner.nextInt();  
        scanner.nextLine();

        try (Socket socket = new Socket(host, port)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            System.out.print(in.readLine() + " ");
            String username = scanner.nextLine();  
            out.println(username);

            System.out.print(in.readLine() + " ");
            String password = scanner.nextLine();  
            out.println(password);
            
            String authResponse = in.readLine();
            System.out.println(authResponse);
            if (authResponse.contains("échouée")) {
                return;
            }

            new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    if (!(e instanceof SocketException)) {
                        e.printStackTrace();
                    }
                }
            }).start();

            String message;
            while ((message = scanner.nextLine()) != null) {
                if ("exit".equalsIgnoreCase(message)) {
                    break;  
                }
                out.println(message);  
            }
            socket.close();
            System.out.println("Connexion fermée.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        scanner.close();  
    }
}
