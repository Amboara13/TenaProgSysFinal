package moteur;
import java.util.ArrayList;
import java.util.List;

public class Domaine {
    // Nom du domaine
    private String nom;

    // Liste d'éléments dans le domaine (si le domaine est défini par une liste d'éléments)
    private List<Object> domaineElements;

    // Type de classe (si le domaine est défini par un type Java)
    private Class<?> typeClasse;

    // Constructeur pour un domaine avec des éléments définis manuellement
    public Domaine(String nom, List<Object> elements) {
        this.nom = nom;
        this.domaineElements = elements;
        this.typeClasse = null;  // Pas de type spécifique
    }

    // Constructeur pour un domaine avec un type Java spécifique
    public Domaine(String nom, Class<?> typeClasse) {
        this.nom = nom;
        this.domaineElements = new ArrayList<>();
        this.typeClasse = typeClasse;  // Le type est défini ici
    }

    // Méthode pour ajouter une valeur à ce domaine (fonctionne pour les deux cas)
    public boolean ajouterValeur(Object valeur) {
        if (typeClasse != null) {  // Si un type est spécifié, vérifier le type
            if (typeClasse.isInstance(valeur)) {
                domaineElements.add(valeur);
                return true;
            } else {
                System.out.println("Erreur : La valeur ne correspond pas au type du domaine.");
                return false;
            }
        } else {  // Si pas de type, on peut ajouter n'importe quoi
            domaineElements.add(valeur);
            return true;
        }
    }

// Méthode unique pour vérifier si une valeur appartient au domaine
public boolean appartient(Object valeur) {
    // Si le domaine est défini par un type de classe
    if (typeClasse != null) {
        // Vérification si la valeur est de type compatible avec le domaine
        if (typeClasse.isInstance(valeur)) {
            return true; // La valeur appartient si elle est du bon type
        }
        return false; // Si la valeur n'est pas du bon type, elle n'appartient pas
    } else {
        // Si le domaine est défini par une liste d'éléments
        return domaineElements.contains(valeur); // Vérification dans la liste d'éléments
    }
}


    // Méthode pour afficher les éléments du domaine
    public void afficherDomaine() {
        System.out.println("Nom du domaine : " + nom);
        System.out.println("Éléments du domaine : " + domaineElements);
    }

    public String getNom() {
        return nom;
    }

    public List<Object> getDomaineElements() {
        return domaineElements;
    }
}