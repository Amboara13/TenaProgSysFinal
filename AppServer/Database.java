package moteur;
import java.io.*;
import java.util.*;

public class Database {

    public static List<Relation> relations = new ArrayList<>();

    public static List<Relation> getTable() {
        return relations;
    }

    public static String afficherTable() {
        StringBuilder resultat = new StringBuilder();
    
        resultat.append("----------------------------\n");
        List<Relation> table = getTable();
    
        for (Relation relation : table) {
            if (!relation.getNom().equals("a")) {
                resultat.append(" | ").append(relation.getNom()).append("\n");
            }
        }
        resultat.append("----------------------------\n");
    
        return resultat.toString();
    }
    

    public static void setRelations(List<Relation> relations) {
        Database.relations = relations;
    }

    public static boolean estdejaRelation(String nom) {
        boolean present = false;
        List<Relation> result = getTable();
        for (Relation s : result) {
            if (s.getNom().equals(nom)) {
                present=true;
            }
        }
        return present;
    }
    
    public static Relation getRelation(String nom) {
        Relation a = new Relation("a", new String[]{"a", "INT"});
        List<Relation> result = getTable();
        for (Relation s : result) {
            if (s.getNom().equals(nom)) {
                return s;
            }
        }
        return a;
    }

    public static String coupemot(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("La chaîne d'entrée ne peut pas être null ou vide.");
        }
    
        // Enlever les espaces inutiles autour de la chaîne
        input = input.trim();
    
        // Séparer la chaîne par des espaces et récupérer le dernier élément
        String[] mots = input.split("\\s+");  // Utilisation de \\s+ pour gérer les espaces multiples
        return mots[mots.length - 1];  // Retourne le dernier mot
    }
    
    public static Object[] formatTuple(String[] tupleData, Relation relation) {
        // Créer un tableau pour le tuple formaté
        Object[] tuple = new Object[tupleData.length];

        // Traiter chaque élément du tuple et effectuer les conversions nécessaires
        for (int i = 0; i < tupleData.length; i++) {
            String value = tupleData[i].trim(); // Enlever les espaces autour de chaque élément
            String typeAttribut = relation.getAttributs()[i + 1]; // Récupérer le type de l'attribut à partir de la relation

            // Extraire la partie pertinente de la valeur (dernier mot)
            String dernierMot = coupemot(value);

            // Conversion selon le type d'attribut
            if (typeAttribut.equals("INT")) {
                try {
                    tuple[i] = Integer.parseInt(dernierMot); // Convertir en Integer
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("La valeur '" + dernierMot + "' ne peut pas être convertie en INT.");
                }
            } else if (typeAttribut.equals("DOUBLE")) {
                try {
                    tuple[i] = Double.parseDouble(dernierMot); // Convertir en Double
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("La valeur '" + dernierMot + "' ne peut pas être convertie en DOUBLE.");
                }
            } else if (typeAttribut.equals("CHAR")) {
                // Vérifier si la valeur est null ou vide pour les types CHAR
                if (dernierMot == null || dernierMot.isEmpty()) {
                    throw new IllegalArgumentException("La valeur pour l'attribut CHAR ne peut pas être vide ou null.");
                }
                tuple[i] = dernierMot;  // Affecter la première lettre comme CHAR
            } else {
                tuple[i] = dernierMot;  // Par défaut, considérer la valeur comme une chaîne
            }
        }

        return tuple;  // Retourner le tuple formaté
    }

    public static List<Relation> chargerTableau(String filename) 
    {
        List<Relation> relationsChargees = new ArrayList<>();
        String nomRelation = null;
        String[] attributs = null;
    
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim(); // Enlever les espaces autour de la ligne
    
                // Si la ligne commence par "Relation: ", on crée une nouvelle relation
                if (line.startsWith("Relation: ")) {
                    // Si une relation précédente est déjà en cours, on crée la relation
                    if (nomRelation != null && attributs != null) {
                        // Vérification des attributs avant la création de la relation
                        if (attributs == null || containsNull(attributs)) {
                            throw new IllegalArgumentException("Les attributs de la relation '" + nomRelation + "' sont invalides (null détecté).");
                        }
    
                        // Créer la relation avec son nom et ses attributs
                        Relation relation = new Relation(nomRelation, attributs);
                        relationsChargees.add(relation); // Ajouter la relation à la liste
                    }
    
                    // Nouvelle relation : on initialise les variables
                    nomRelation = line.substring(10).trim(); // Extraire le nom de la relation et enlever les espaces
                    attributs = null; // Initialiser les attributs pour la nouvelle relation
                } else if (line.startsWith("Attributs: ")) {
                    // Extraire les attributs de la ligne
                    String[] parts = line.substring(11).split(",");
                    // Nettoyer et enlever les espaces avant de les ajouter
                    attributs = new String[parts.length];
                    for (int i = 0; i < parts.length; i++) {
                        attributs[i] = parts[i].trim(); // Enlever les espaces autour de chaque attribut
                    }
    
                    // Vérifier que les attributs ne sont pas null et qu'ils sont valides
                    if (attributs == null || attributs.length == 0 || containsNull(attributs)) {
                        throw new IllegalArgumentException("Les attributs de la relation '" + nomRelation + "' sont invalides (null ou vide détecté).");
                    }
                }
            }
    
            // Ajouter la dernière relation si elle existe
            if (nomRelation != null && attributs != null) {
                // Vérification des attributs avant la création de la relation
                if (attributs == null || containsNull(attributs)) {
                    throw new IllegalArgumentException("Les attributs de la relation '" + nomRelation + "' sont invalides (null détecté).");
                }
    
                // Créer la dernière relation et l'ajouter à la liste
                Relation relation = new Relation(nomRelation, attributs);
                relationsChargees.add(relation);
            }
    
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la lecture du fichier.");
        }
    
        // Retourner la liste des relations créées
        return relationsChargees;
    }
    
    public static void chargerTuple(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim(); // Enlever les espaces autour de la ligne
    
                // Vérifier si la ligne commence par "Tuple pour la relation"
                if (line.startsWith("Tuple pour la relation")) {
                    // Extraire le nom de la relation (par exemple "Cours")
                    String relationName = line.substring(23, line.indexOf(":")).trim();
    
                    // Trouver la relation correspondante via la fonction getRelation
                    Relation relation = getRelation(relationName);
               
    
                    // Extraire les données du tuple après les ":"
                    String tupleDataString = line.substring(line.indexOf(":") + 1).trim();
                    String[] tupleData = tupleDataString.split(",");
    
                    // Préparer le tableau d'objets pour le tuple
                    Object[] tuple = new Object[tupleData.length];
    
                    tuple= formatTuple(tupleData, relation);
                    relation.ajouterTuple(tuple);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la lecture du fichier.");
        }
    }
    
    private static boolean containsNull(String[] array) {
        for (String str : array) {
            if (str == null) {
                return true;
            }
        }
        return false;
    }
    
    public static void chargerRelationsDepuisFichier()
    {
        String fichier = Relation.getFilenamesauvegarde();
        Database.relations= Database.chargerTableau(fichier);
        Database.chargerTuple(fichier);
    }
}
