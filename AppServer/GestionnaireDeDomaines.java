package moteur;
import java.util.ArrayList;
import java.util.List;

public class GestionnaireDeDomaines {
    private List<Domaine> domaines;

    public GestionnaireDeDomaines() {
        domaines = new ArrayList<>();
    }

    public void ajouterDomaine(Domaine domaine) {
        domaines.add(domaine);
    }

    public void afficherTousLesDomaines() {
        for (int i = 0; i < domaines.size(); i++) {
            System.out.println("Domaine #" + (i + 1) + " : ");
            domaines.get(i).afficherDomaine();
            System.out.println();
        }
    }

    public boolean appartient(String nomDomaine, Object valeur) {
        for (Domaine domaine : domaines) {
            if (domaine.getNom().equals(nomDomaine)) {

                return domaine.appartient(valeur);  
            }
        }
        System.out.println("Domaine introuvable.");
        return false;  
    }

public static GestionnaireDeDomaines getDomaineBord()
{   
    
    GestionnaireDeDomaines gestionnaire = new GestionnaireDeDomaines();

    Domaine domaineTinyInt = new Domaine("TINYINT", Byte.class);
    gestionnaire.ajouterDomaine(domaineTinyInt);

    Domaine domaineSmallInt = new Domaine("SMALLINT", Short.class);
    gestionnaire.ajouterDomaine(domaineSmallInt);

    Domaine domaineMediumInt = new Domaine("MEDIUMINT", Integer.class);
    gestionnaire.ajouterDomaine(domaineMediumInt);

    Domaine domaineInt = new Domaine("INT", Integer.class);
    gestionnaire.ajouterDomaine(domaineInt);

    Domaine domaineBigInt = new Domaine("BIGINT", Long.class);
    gestionnaire.ajouterDomaine(domaineBigInt);

    Domaine domaineFloat = new Domaine("FLOAT", Float.class);
    gestionnaire.ajouterDomaine(domaineFloat);

    Domaine domaineDouble = new Domaine("DOUBLE", Double.class);
    gestionnaire.ajouterDomaine(domaineDouble);

    Domaine domaineDecimal = new Domaine("DECIMAL", java.math.BigDecimal.class);
    gestionnaire.ajouterDomaine(domaineDecimal);

    Domaine domaineChar = new Domaine("CHAR", String.class);
    gestionnaire.ajouterDomaine(domaineChar);

    Domaine domaineVarchar = new Domaine("VARCHAR", String.class);
    gestionnaire.ajouterDomaine(domaineVarchar);

    Domaine domaineText = new Domaine("TEXT", String.class);
    gestionnaire.ajouterDomaine(domaineText);

    Domaine domaineTinyText = new Domaine("TINYTEXT", String.class);
    gestionnaire.ajouterDomaine(domaineTinyText);

    Domaine domaineMediumText = new Domaine("MEDIUMTEXT", String.class);
    gestionnaire.ajouterDomaine(domaineMediumText);

    Domaine domaineLongText = new Domaine("LONGTEXT", String.class);
    gestionnaire.ajouterDomaine(domaineLongText);

    Domaine domaineDate = new Domaine("DATE", java.sql.Date.class);
    gestionnaire.ajouterDomaine(domaineDate);

    Domaine domaineDateTime = new Domaine("DATETIME", java.sql.Timestamp.class);
    gestionnaire.ajouterDomaine(domaineDateTime);

    Domaine domaineTimestamp = new Domaine("TIMESTAMP", java.sql.Timestamp.class);
    gestionnaire.ajouterDomaine(domaineTimestamp);

    Domaine domaineTime = new Domaine("TIME", java.sql.Time.class);
    gestionnaire.ajouterDomaine(domaineTime);

    Domaine domaineYear = new Domaine("YEAR", Integer.class);
    gestionnaire.ajouterDomaine(domaineYear);

    Domaine domaineBinary = new Domaine("BINARY", byte[].class);
    gestionnaire.ajouterDomaine(domaineBinary);

    Domaine domaineVarBinary = new Domaine("VARBINARY", byte[].class);
    gestionnaire.ajouterDomaine(domaineVarBinary);

    Domaine domaineBlob = new Domaine("BLOB", byte[].class);
    gestionnaire.ajouterDomaine(domaineBlob);

    Domaine domaineTinyBlob = new Domaine("TINYBLOB", byte[].class);
    gestionnaire.ajouterDomaine(domaineTinyBlob);

    Domaine domaineMediumBlob = new Domaine("MEDIUMBLOB", byte[].class);
    gestionnaire.ajouterDomaine(domaineMediumBlob);

    Domaine domaineLongBlob = new Domaine("LONGBLOB", byte[].class);
    gestionnaire.ajouterDomaine(domaineLongBlob);

    Domaine domaineEnum = new Domaine("ENUM", String.class);
    gestionnaire.ajouterDomaine(domaineEnum);

    Domaine domaineSet = new Domaine("SET", String.class); 
    gestionnaire.ajouterDomaine(domaineSet);

    // Créer un domaine avec des éléments définis manuellement
    List<Object> elements = new ArrayList<>();
    elements.add(42);
    elements.add("Bonjour");
    elements.add(3.14);
    elements.add(true);
    Domaine domaine1 = new Domaine("PERSONEL", elements);
    gestionnaire.ajouterDomaine(domaine1);

    return gestionnaire;
}

public boolean domaineExiste(String nomDomaine) {
    for (Domaine domaine : domaines) {
        if (domaine.getNom().equals(nomDomaine)) {
            return true; // Le domaine existe
        }
    }
    return false; // Le domaine n'existe pas
}

public static void main(String[] args) {
    GestionnaireDeDomaines a = getDomaineBord();
    System.out.println(a.appartient("DOUBLE", 10.0));
}
}
