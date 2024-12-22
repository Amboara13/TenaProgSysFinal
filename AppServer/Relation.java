package moteur;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.print.DocFlavor.STRING;

import config.Conf;

public class Relation {


    static {
        Conf.chargerConfiguration();  // Appel de la méthode pour charger la configuration
    }
    private String nom;
    String[] attributs;
    String[] affichageAttribut = {"Noms de l'attribut", "types"};
    
    private static final String FILENAMESAUVEGARDE =  Conf.getFilenamesauvegarde();
    public static String getFilenamesauvegarde() {
        return FILENAMESAUVEGARDE;
    }
    private static final String FILENAME = "relations.txt"; 
    GestionnaireDeDomaines tableauDeBord = GestionnaireDeDomaines.getDomaineBord();
    private List<Object[]> tuples; 

    private String inscrire() {

        boolean relationExistante = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Relation: " + this.nom)) {
                    relationExistante = true;
                    break;
                }
            }
        } catch (IOException e) {
            return "Erreur lors de la lecture du fichier pour vérifier l'existence de la relation.";
        }
    
        if (relationExistante) {
            return "La relation " + this.nom + " existe déjà dans le fichier.";
        }
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME, true))) {
            writer.write("\n----\n");
            writer.write("Relation: " + this.nom + "\n");
            writer.write("Attributs: ");
            for (int i = 0; i < this.attributs.length; i++) {
                writer.write(this.attributs[i] + (i < this.attributs.length - 1 ? ", " : "\n"));
            }
            writer.write("----\n");
        } catch (IOException e) {
            return "Erreur lors de l'enregistrement de la relation.";
        }
    
        return "Relation inscrite avec succès.";
    }
    
    
    public static void inscrire(Relation a){
        Database.relations.add(a);
    }

    public Relation(String nom, String[] attributs) {
        this.tuples = new ArrayList<>();
    
        for (int i = 1; i < attributs.length; i += 2) {
            String typeAttribut = (String) attributs[i]; 
    
            if (!tableauDeBord.domaineExiste(typeAttribut)) {
                throw new IllegalArgumentException("Le type '" + typeAttribut + "' pour l'attribut " + attributs[i - 1] + " n'existe pas dans les domaines.");
            }
        }

        this.nom = nom;
        this.setAttributs(attributs);
        Relation.inscrire(this);
    } 
    
    
    public Relation(String nom, String[] attributs , String t) {
        this.tuples = new ArrayList<>();
        for (int i = 1; i < attributs.length; i += 2) {
            String typeAttribut = (String) attributs[i]; 
    
            if (!tableauDeBord.domaineExiste(typeAttribut)) {
                throw new IllegalArgumentException("Le type '" + typeAttribut + "' pour l'attribut " + attributs[i - 1] + " n'existe pas dans les domaines.");
            }
        }
        this.nom = nom;
        this.setAttributs(attributs);
        this.inscrire(this);
        this.inscrire();
    }
    
    public String getNom() {
        return nom;
    }
    
public String ajouterTuple(Object[] tuple) {
    if (tuple.length != attributs.length / 2) {
        return "Erreur : Le nombre d'éléments dans le tuple ne correspond pas au nombre d'attributs.";
    }

    for (int i = 0; i < tuple.length; i++) {
        String typeAttribut = (String) attributs[i * 2 + 1];
        String nomDomaine = typeAttribut;
        if (!tableauDeBord.appartient(nomDomaine, tuple[i])) {
            return "Erreur : La valeur " + tuple[i] + " ne correspond pas au domaine " + nomDomaine + ".";
        }
    }

    tuples.add(tuple);
    return "Tuple ajouté avec succès.";
}
  
    public void ajouterTuple(Object[] tuple , String t) {
        // Verifier que le tuple a le bon nombre d'elements
        if (tuple.length != attributs.length / 2) {
            throw new IllegalArgumentException("Le nombre d'elements dans le tuple ne correspond pas au nombre d'attributs.");
        }
    
        // Verification de la validite des valeurs dans le tuple
        for (int i = 0; i < tuple.length; i++) {
            String typeAttribut = (String) attributs[i * 2 + 1]; // Le type de l'attribut
            String nomDomaine = typeAttribut; // Domaine = type de l'attribut
            if (!tableauDeBord.appartient(nomDomaine, tuple[i])) {
                throw new IllegalArgumentException("La valeur " + tuple[i] + " ne correspond pas au domaine " + nomDomaine);
            }
        }

        tuples.add(tuple);
    
        String enre = "Tuple pour la relation "  + this.nom + ": ";
        for (int i = 0; i < tuple.length; i++) {
            enre = enre + tuple[i]+",";
        }
        boolean relationExistante = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Relation: " + this.nom)) {
                    relationExistante = true;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la lecture du fichier pour verifier l'existence de la relation.");
        }

        if(relationExistante==true)
        {
            try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(enre)) {
                        relationExistante = false;
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Erreur lors de la lecture du fichier pour verifier l'existence du tuple.");
            }
        }
        if(relationExistante==true)
        {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME, true))) {              
                writer.write(enre + "\n");

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Erreur lors de l'enregistrement du tuple dans le fichier.");
            }
        }
    }

    public String afficher() {
        StringBuilder resultat = new StringBuilder();
    
        resultat.append("\nRelation : ").append(nom).append("\n");
        Object[] attr = this.getAttributs();
    
        if (tuples.size() == 0) {
            resultat.append("Pas de resultat\n");
        } else {
            StringBuilder a = new StringBuilder();
    
            // Ajoute les noms des attributs
            for (int i = 0; i < attr.length; i++) {
                if (i % 2 == 0) {
                    a.append(String.format("%-15s | ", attr[i])); // Ajuste la largeur si necessaire
                }
            }
            resultat.append(a.toString().trim()).append("\n");
    
            // Ajouter une ligne de separation
            int lineLength = a.toString().length();
            String lineSeparator = "-".repeat(lineLength);
            resultat.append(lineSeparator).append("\n");
    
            // Ajoute les valeurs des tuples
            for (Object[] tuple : tuples) {
                StringBuilder tupleLine = new StringBuilder();
                for (Object o : tuple) {
                    tupleLine.append(String.format("%-15s | ", o)); // Ajuste la largeur si necessaire
                }
                resultat.append(tupleLine.toString().trim()).append("\n");
            }
        }
    
        return resultat.toString();
    }
    

    public boolean compatibilite(Relation autre) {
        if (this.getAttributs().length != autre.getAttributs().length) {
            System.out.println("Les relations n'ont pas le même nombre d'attributs.");
            return false;
        }
        return true;
    }   

    public Relation selectionner(String nomColonne, String relation, String valeur) {
        int indexColonne = getIndiceColonne(nomColonne);
        if (indexColonne == -1) {
            System.out.println("Erreur : Colonne " + nomColonne + " introuvable.");
            return null;
        }
    
        Relation resultat = new Relation(nom, this.getAttributs());
        for (Object[] tuple : tuples) {
            String champ = tuple[indexColonne].toString();
            boolean condition = false;
    
            switch (relation) {
                case "=":
                    condition = champ.equalsIgnoreCase(valeur);
                    break;
                case "!=":
                    condition = !champ.equalsIgnoreCase(valeur);
                    break;
                case ">":
                    condition = champ.compareTo(valeur) > 0;
                    break;
                case ">=":
                    condition = champ.compareTo(valeur) >= 0;
                    break;
                case "<":
                    condition = champ.compareTo(valeur) < 0;
                    break;
                case "<=":
                    condition = champ.compareTo(valeur) <= 0;
                    break;
                case "contient":
                    condition = champ.contains(valeur);
                    break;
                case "commence_par":
                    condition = champ.startsWith(valeur);
                    break;
                case "finit_par":
                    condition = champ.endsWith(valeur);
                    break;
                default:
                    System.out.println("Erreur : Relation non valide : " + relation);
                    return null;
            }
    
            if (condition) {
                resultat.ajouterTuple(tuple);
            }
        }
        return resultat;
    }

    public Relation projeter(int[] indexColonnes) {
        this.setAttributs(this.getAttributsFiltres(indexColonnes));
        Relation resultat = new Relation(nom, this.getAttributs());
        for (Object[] tuple : tuples) { 
            Object[] nouveauTuple = new Object[indexColonnes.length];
            for (int i = 0; i < indexColonnes.length; i++) {
                nouveauTuple[i] = tuple[indexColonnes[i]];
            }
            resultat.ajouterTuple(nouveauTuple);
        }
        return resultat;
    }
    

    public Relation projeter(String[] nomColonnes) {

        int[] indexColonnes= new int[nomColonnes.length];
        for(int i=0 ; i<nomColonnes.length ; i++)
        {
            indexColonnes[i]=getIndiceColonne(nomColonnes[i]);
        }
        this.setAttributs(this.getAttributsFiltres(indexColonnes));
        Relation resultat = new Relation(nom, this.getAttributs());
        for (Object[] tuple : tuples) { 
            Object[] nouveauTuple = new Object[indexColonnes.length];
            for (int i = 0; i < indexColonnes.length; i++) {
                nouveauTuple[i] = tuple[indexColonnes[i]];
            }
            resultat.ajouterTuple(nouveauTuple);
        }
        return resultat;
    }

    private boolean contientTuple(Object[] tuple, List<Object[]> tuples) { 
        for (Object[] t : tuples) {
            if (Arrays.equals(t, tuple)) {
                return true; 
            }
        }
        return false; 
    }

    public String describe() {
        StringBuilder resultat = new StringBuilder();
    
        // Ajoute les en-têtes d'affichage des attributs
        resultat.append(String.format("%-20s | %-20s%n", this.affichageAttribut[0], this.affichageAttribut[1]));
    
        // Ajoute une ligne de separation
        int lineLength = 20 + 3 + 20;
        String lineSeparator = "-".repeat(lineLength);
        resultat.append(lineSeparator).append("\n");
    
        // Parcourt les attributs et les ajoute au resultat
        for (int i = 0; i < this.getAttributs().length - 1; i += 2) {
            resultat.append(String.format("%-20s | %-20s%n", this.getAttributs()[i], this.getAttributs()[i + 1]));
        }
    
        return resultat.toString();
    }
    

    public Relation unir(Relation autre) {

        Relation resultat = new Relation(nom, this.getAttributs());
        
        if (this.compatibilite(autre)) {
            resultat.tuples.addAll(tuples); 

            for (Object[] tuple : autre.tuples) { 
                if (!contientTuple(tuple, tuples)) {
                    resultat.ajouterTuple(tuple);
                }
            }
         }
        return resultat; 
    }

    public Relation differencier(Relation autre) {
        Relation resultat = new Relation(nom, this.getAttributs());

        if (this.compatibilite(autre)) {
            for (Object[] tuple : tuples) { 
               
                if (!contientTuple(tuple, autre.tuples)) {
                    resultat.ajouterTuple(tuple);
                }
            }
        } else {
            System.out.println("Les relations ne sont pas compatibles pour la difference.");
        }

        return resultat; 
    }

    public Object[] combinaison(Object[] array1, Object[] array2) { 
        List<Object> combined = new ArrayList<>(Arrays.asList(array1));
        combined.addAll(Arrays.asList(array2));
        return combined.toArray(new Object[0]);
    }

    public String[] precisionAttribut(String[] attributs) {
        String[] result = new String[attributs.length];
    
        for (int i = 0; i < attributs.length; i++) {
            if(i%2==0)
            {
                result[i] = this.getNom() + "." + attributs[i];
            }  
            else
            {
                result[i] = attributs[i];
            }
        }
    
        return result; 
    }
       
    public Relation produitCartesien(Relation autre) {
        Object[] attributsComposes = combinaison(this.getAttributs(), autre.getAttributs());
        
        String[] attributs = Arrays.copyOf(attributsComposes, attributsComposes.length, String[].class);
        
        Relation resultat = new Relation("Produit cartesien de " + nom + " et " + autre.getNom(), attributs);
    
        for (Object[] tuple1 : tuples) {
            for (Object[] tuple2 : autre.tuples) {

                Object[] nouveauTuple = new Object[tuple1.length + tuple2.length];
                System.arraycopy(tuple1, 0, nouveauTuple, 0, tuple1.length); 
                System.arraycopy(tuple2, 0, nouveauTuple, tuple1.length, tuple2.length); 
    
                resultat.ajouterTuple(nouveauTuple);
            }
        }
        Object[] attrib = combinaison(this.precisionAttribut(this.getAttributs()), autre.precisionAttribut(autre.getAttributs()));
        String[] str = Arrays.copyOf(attrib, attrib.length, String[].class);
        
        resultat.setAttributs(str);
        return resultat; 
    } 

    public int getIndiceColonne(String nomColonne) {
        for (int i = 0; i < attributs.length; i += 2) {
            if (attributs[i].equalsIgnoreCase(nomColonne)) {
                return i / 2; 
            }
        }
        return 1;
    }
    
    public String[] getAttributsFiltres(int[] indexColonnes) {
        String[] nouveauxAttributs = new String[indexColonnes.length * 2]; 
    
        for (int i = 0; i < indexColonnes.length; i++) {
            int idx = indexColonnes[i]; 
            nouveauxAttributs[i * 2] = (String) this.getAttributs()[idx * 2]; 
            nouveauxAttributs[i * 2 + 1] = (String) this.getAttributs()[idx * 2 + 1]; 
        }
    
        return nouveauxAttributs; 
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String[] getAttributs() { 
        return attributs;
    }

    public void setAttributs(String[] attributs) { 
        this.attributs = attributs;
    }

    public List<Object[]> getTuples() { 
        return tuples;
    }

    public void setTuples(List<Object[]> tuples) { 
        this.tuples = tuples;
    }
    public static String[] formatRequete(String requete) {
        // Liste pour stocker les résultats
        List<String> result = new ArrayList<>();
        
        // Expression régulière pour les mots ou opérateurs spéciaux
        // Nous incluons le point dans l'expression régulière afin que 'a.b' soit traité comme un seul mot
        String regex = "(!=|<=|>=|%|[a-zA-Z0-9_]+(?:\\.[a-zA-Z0-9_]+)*|[\\(\\)\\+\\-\\*/=<>!]+)";
        
        // Trouver toutes les correspondances avec l'expression régulière
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(regex).matcher(requete);
        
        // Parcourir toutes les correspondances
        while (matcher.find()) {
            result.add(matcher.group());
        }
        
        // Retourner le tableau des résultats
        return result.toArray(new String[0]);
    }  
    public static String[] extrait(String[] array, String debut, String fin) {
        int startIndex = -1;
        int endIndex = -1;
    
        // Trouver l'index de debut
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(debut)) {
                startIndex = i;
                break;
            }
        }
    
        if (startIndex == -1) {
            throw new IllegalArgumentException("Le debut de la sous-liste (" + debut + ") n'a pas ete trouve.");
        }
    
        // Trouver l'index de fin
        for (int i = startIndex; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(fin)) {
                endIndex = i;
                break;
            }
        }
    
        // Si "fin" n'est pas trouve, on prend jusqu'à la fin du tableau
        if (endIndex == -1) {
            endIndex = array.length;
        }
    
        // Calcul de la taille de la sous-liste
        int length = endIndex - startIndex - 1;
    
        if (length <= 0) {
            return new String[0];  
        }
    
        // Creer et remplir le tableau de resultats
        String[] result = new String[length];
        System.arraycopy(array, startIndex + 1, result, 0, length);  
    
        return result;
    }
    
    public static boolean contient(String[] tableau , String a) {
        // Parcours du tableau pour vérifier si l'un des éléments est une parenthèse ouvrante "("
        for (String element : tableau) {
            if (element.equalsIgnoreCase(a)) {
                return true; // Si une parenthèse ouvrante est trouvée, renvoyer true
            }
        }
        return false; // Sinon, renvoyer false
    }
    
    public Relation intersection(Relation autre) {
        Relation resultat = new Relation(nom, this.getAttributs());

        if (this.compatibilite(autre)) {
            for (Object[] tuple : tuples) { 
               
                if (contientTuple(tuple, autre.tuples)) {
                    resultat.ajouterTuple(tuple);
                }
            }
        } else {
            System.out.println("Les relations ne sont pas compatibles pour l'intersection.");
        }

        return resultat; 
    }

    public static Relation requeteSelect(String requete)
    {
        String[] result = formatRequete(requete);

        Relation rel=new Relation("a", new String[]{"a","INT"});
        Relation rel_debut =rel;
        String[] condition = new String[]{"",""};
        
        if(contient(result, "WHERE"))
        {
            condition = extrait(result, "WHERE", "");
        }

        for(int i=0 ; i<result.length ; i++)
        {
            if(result[i].equalsIgnoreCase("FROM"))
            {
                rel =Database.getRelation(result[i+1]);
                rel_debut =rel;
                break;
            }
        }
        if(contient(condition,"("))
        {
            List<Relation> vita = new ArrayList<>();
            List<String[]> coup = extractWordsInParentheses(requete);
            List<String> liaison = motliaison(requete);
     
            coup=extractWordsInParentheses(requete);

            for(int j=0 ; j<coup.size() ; j++ )
            {
                Relation zao = rel;

                for(int i=0 ; i<coup.get(j).length ; i++)
                {
                    if(coup.get(j)[i].equalsIgnoreCase("OR"))
                    {
                        if(coup.get(j)[i+2].equalsIgnoreCase("LIKE"))
                        {
                            if(coup.get(j)[i+3].equals("%"))
                            {
                                if(i+5<coup.get(j).length && coup.get(j)[i+5].equals("%"))
                                {
                                    Relation or=rel.selectionner(coup.get(j)[i+1], "contient", coup.get(j)[i+4]);
                                    zao=zao.unir(or);
                                }
                                else{
                                    Relation or=rel.selectionner(coup.get(j)[i+1], "finit_par", coup.get(j)[i+4]);
                                    zao=zao.unir(or);
                                }

                            }
                            else if(coup.get(j)[i+4].equals("%"))
                            {
                                Relation or=rel.selectionner(coup.get(j)[i+1], "commence_par", coup.get(j)[i+3]);
                                zao=zao.unir(or);
                            }
                        }
                        else{
                            Relation or=rel.selectionner(coup.get(j)[i+1], coup.get(j)[i+2], coup.get(j)[i+3]);
                            zao=zao.unir(or);
                        }
                    
                    }
                    else if(i==0)
                    {

                        if(coup.get(j)[i+1].equalsIgnoreCase("LIKE"))
                        {
                            if(coup.get(j)[i+2].equals("%"))
                            {
                                if(i+4<coup.get(j).length && coup.get(j)[i+4].equals("%"))
                                {
                                    zao=rel.selectionner(coup.get(j)[i], "contient", coup.get(j)[i+3]);
                                }
                                else{
                                    zao=rel.selectionner(coup.get(j)[i], "finit_par", coup.get(j)[i+3]);
                                }

                            }
                            else if(coup.get(j)[i+4].equals("%"))
                            {
                                zao=rel.selectionner(coup.get(j)[i], "commence_par", coup.get(j)[i+2]);
                            }
                        }
                        else{
                        zao=rel.selectionner(coup.get(j)[i], coup.get(j)[i+1], coup.get(j)[i+2]);
                    }
                    }
                    else if(coup.get(j)[i].equalsIgnoreCase("AND"))
                    {
                         if(coup.get(j)[i+2].equalsIgnoreCase("LIKE"))
                        {
                            if(coup.get(j)[i+3].equals("%"))
                            {
                                if(i+5<coup.get(j).length && coup.get(j)[i+5].equals("%"))
                                {
                                    zao=rel.selectionner(coup.get(j)[i+1], "contient", coup.get(j)[i+4]);
                                }
                                else{
                                    zao=rel.selectionner(coup.get(j)[i+1], "finit_par", coup.get(j)[i+4]);
                                }

                            }
                            else if(coup.get(j)[i+4].equals("%"))
                            {
                                zao=rel.selectionner(coup.get(j)[i+1], "commence_par", coup.get(j)[i+3]);
                            }
                        }
                        
                        else{
                            zao=rel.selectionner(coup.get(j)[i+1], coup.get(j)[i+2], coup.get(j)[i+3]);
                       } 
                    
                    }
                    

                }
                vita.add(zao);
            }

            rel=vita.get(0);
            for (Relation string : vita) {
                System.out.println("uneeeee relation enregistre");
                string.afficher();
            }
            for (String string : liaison) {
                System.out.println(liaison);
            }
            if(liaison.size()==0)
            {
                rel=vita.get(1);
            }
            else{
                for(int i=0 ; i<liaison.size() ;i++)
                {
                    if(liaison.get(i).equalsIgnoreCase("AND"))
                    {
                        rel=rel.intersection(vita.get(i+1));
                    }
                    else{
                        rel=rel.unir(vita.get(i+1));
                    }
                }
            }
            for(int i =0 ; i<result.length ; i++)
            {
                if(i>0 && (!result[i-1].equalsIgnoreCase("LEFT")&&!result[i-1].equalsIgnoreCase("RIGHT")) && result[i].equalsIgnoreCase("JOIN") && result[i+2].equalsIgnoreCase("ON"))
                {
                    rel=Relation.join(rel, Database.getRelation(result[i+1]), result[i+3], result[i+4] , result[i+5]);
                }
                else if(i>0 && result[i-1].equalsIgnoreCase("LEFT") && result[i].equalsIgnoreCase("JOIN") && result[i+2].equalsIgnoreCase("ON"))
                {
                    rel=Relation.joinExterneLeft(rel, Database.getRelation(result[i+1]), result[i+3], result[i+4] , result[i+5]);
                }
                else if(i>0 && result[i-1].equalsIgnoreCase("RIGHT") && result[i].equalsIgnoreCase("JOIN") && result[i+2].equalsIgnoreCase("ON"))
                {
                    rel=Relation.joinExterneRight(rel, Database.getRelation(result[i+1]), result[i+3], result[i+4] , result[i+5]);
                }
                if(result[i].equalsIgnoreCase("ORDER") && result[i+1].equalsIgnoreCase("BY"))
                {
                    int sens = 0;
                    String[] colonnes =result;
                    if(contient(result, "desc"))
                    {
                        colonnes = extrait(result, "by", "desc");
                        sens=1;
                    }
                    else{
                        colonnes = extrait(result, "by", "asc");
                    }
                    rel.trier(sens,colonnes);

                }
            }       
}
    
        else{
            for(int i=0 ; i<result.length ; i++)
            {
                if(result[i].equalsIgnoreCase("WHERE") || result[i].equalsIgnoreCase("AND"))
                {
                    if(result[i+2].equalsIgnoreCase("LIKE"))
                    {
                        if(result[i+3].equals("%"))
                        {
                            if(i+5<result.length && result[i+5].equals("%"))
                            {
                                rel=rel.selectionner(result[i+1], "contient", result[i+4]);
                            }
                            else{
                                rel=rel.selectionner(result[i+1], "finit_par", result[i+4]);
                            }
                        }
                        else if(result[i+4].equals("%"))
                        {
                            rel=rel.selectionner(result[i+1], "commence_par", result[i+3]);
                        }
                    }
                    else{
                        rel=rel.selectionner(result[i+1], result[i+2], result[i+3]);
                    }
                    i=i+3;
                }
                if(result[i].equalsIgnoreCase("OR"))
                {
                    if(result[i+2].equalsIgnoreCase("LIKE"))
                    {
                        if(result[i+3].equalsIgnoreCase("%"))
                        {
                            if(i+5<result.length && result[i+5].equals("%"))
                            {
                                Relation or=rel_debut.selectionner(result[i+1], "contient", result[i+4]);
                                rel=rel.unir(or);
                            }
                            else{
                                Relation or=rel_debut.selectionner(result[i+1], "finit_par", result[i+4]);
                                rel=rel.unir(or);
                            }
                        }
                        else if(result[i+4].equals("%"))
                        {
                            Relation or=rel_debut.selectionner(result[i+1], "commence_par", result[i+3]);
                        
                            rel=rel.unir(or);
                        }
                    }
                    else{
                        Relation or=rel_debut.selectionner(result[i+1], result[i+2], result[i+3]);
                        rel=rel.unir(or);
                    }
                
                }
                if(i>0 && (!result[i-1].equalsIgnoreCase("LEFT")&&!result[i-1].equalsIgnoreCase("RIGHT")) && result[i].equalsIgnoreCase("JOIN") && result[i+2].equalsIgnoreCase("ON"))
                {
                    rel=Relation.join(rel, Database.getRelation(result[i+1]), result[i+3], result[i+4] , result[i+5]);
                }
                else if(i>0 && result[i-1].equalsIgnoreCase("LEFT") && result[i].equalsIgnoreCase("JOIN") && result[i+2].equalsIgnoreCase("ON"))
                {
                    rel=Relation.joinExterneLeft(rel, Database.getRelation(result[i+1]), result[i+3], result[i+4] , result[i+5]);
                }
                else if(i>0 && result[i-1].equalsIgnoreCase("RIGHT") && result[i].equalsIgnoreCase("JOIN") && result[i+2].equalsIgnoreCase("ON"))
                {
                    rel=Relation.joinExterneRight(rel, Database.getRelation(result[i+1]), result[i+3], result[i+4] , result[i+5]);
                }
                if(result[i].equalsIgnoreCase("ORDER") && result[i+1].equalsIgnoreCase("BY"))
                {
                    int sens = 0;
                    String[] colonnes =result;
                    if(contient(result, "desc"))
                    {
                        colonnes = extrait(result, "by", "desc");
                        sens=1;
                    }
                    else{
                        colonnes = extrait(result, "by", "asc");
                    }
                    rel.trier(sens,colonnes);

                }
            }
        }

        //projection
    
        String[] col = extrait(result, "SELECT", "FROM");
        if(!col[0].equals("*"))
        {
            rel=rel.projeter(col);
        }
    

        return rel;
    }
    

    public static void requeteCreateTable(String requete)
    {
        String req = requete;
        String[] result = formatRequete(req);
        String [] attribut = extrait(result, "(", ")"); 
        String nomTabble = extrait(result, "TABLE", "VALUES")[0];
        Relation rel = new Relation(nomTabble, attribut , "t");
    }

    public static void deleteLinesContaining(String filePath, String targetString, String attributeString) {
        File inputFile = new File(filePath);
        File tempFile = new File(inputFile.getAbsolutePath() + ".tmp");

        BufferedReader reader = null;
        BufferedWriter writer = null;
        List<String> lines = new ArrayList<>();
        Set<Integer> linesToDelete = new HashSet<>(); // Ensemble pour stocker les indices des lignes à supprimer

        try {
            // Creer les objets pour lire et ecrire dans les fichiers
            reader = new BufferedReader(new FileReader(inputFile));
            writer = new BufferedWriter(new FileWriter(tempFile));

            // Lire toutes les lignes du fichier
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                lines.add(currentLine);
            }

            // Identifier les lignes à supprimer
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                
                // Si la ligne contient le "targetString" (ici "etudiant")
                if (line.contains(targetString)) {
                    // Ajouter cette ligne à la liste des lignes à supprimer
                    linesToDelete.add(i);

                    // Verifier si la ligne suivante contient "attribut" et la supprimer si necessaire
                    if (i + 1 < lines.size() && lines.get(i + 1).contains(attributeString)) {
                        linesToDelete.add(i + 1);
                    }
                }
            }

            // Reecrire les lignes dans le fichier temporaire, en excluant les lignes à supprimer
            for (int i = 0; i < lines.size(); i++) {
                if (!linesToDelete.contains(i)) {
                    writer.write(lines.get(i));
                    writer.newLine();
                }
            }

            // Fermer les flux après utilisation
            reader.close();
            writer.close();

            // Supprimer le fichier original
            if (inputFile.delete()) {
                // Renommer le fichier temporaire pour qu'il prenne le nom du fichier original
                if (!tempFile.renameTo(inputFile)) {
                    System.out.println("Erreur lors du renommage du fichier temporaire.");
                }
            } else {
                System.out.println("Erreur lors de la suppression du fichier original.");
            }

        } catch (IOException e) {
            System.out.println("Une erreur est survenue lors de la lecture/ecriture du fichier.");
            e.printStackTrace();
        } finally {
            try {
                // Assurer la fermeture des flux même en cas d'erreur
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                System.out.println("Erreur lors de la fermeture des flux.");
                e.printStackTrace();
            }
        }
    }

    public static void requeteEffacerTable(String requete)
    {
        String req = requete;
        String[] result = formatRequete(req);

        String nomTabble = extrait(result, "TABLE", "\n")[0];
        deleteLinesContaining(FILENAME, nomTabble , "Attributs");
        Database.relations = new ArrayList<>();
        Database.chargerRelationsDepuisFichier();
        System.out.println("Les lignes contenant \"" + nomTabble + "\" ont ete supprimees.");
    }

    
    public static void requeteEffacerElementTableau(String requete)
    {
        String[] format = formatRequete(requete);
        String nomTabble =format[2];
        Relation aeffacer = Database.getRelation(nomTabble);   
         
        if(format.length==3)
        {
            deleteLinesContaining(FILENAME,"Tuple pour la relation " + nomTabble , "");
            aeffacer.setTuples(new ArrayList<>());
        }
        else{
            String[] condition = extrait(format, "WHERE", "");
            
            String phrase="";
            phrase = phrase +"SELECT"+" ";
            phrase = phrase +"*"+" ";
            phrase = phrase +"FROM"+" "; 
            phrase = phrase +nomTabble+" ";
            phrase = phrase +"WHERE"+" ";
            for (String string : condition) {
                phrase = phrase + string +" ";
            }
            Relation a = Relation.requeteSelect(phrase);
            aeffacer=aeffacer.differencier(a);
            deleteLinesContaining(FILENAME,"Tuple pour la relation " + nomTabble , "");
            Relation b = new Relation(nomTabble, aeffacer.getAttributs(), "t");
            for (Object[] t : aeffacer.getTuples()) {
                b.ajouterTuple(t, "t");
            }
        }



    }

    public static void requeteUpdate(String requete)
    {
        String[] format = formatRequete(requete);
        
        String nomTabble =format[1];
        Relation amodifier = Database.getRelation(nomTabble);
        int colonneamodifier = amodifier.getIndiceColonne(format[3]);
        String valeur = format[5];

        Relation avynamboarina = new Relation(nomTabble, amodifier.getAttributs());


        if(format.length==6)
        {
            for (Object[] tuple : amodifier.getTuples()) {

                
                    Object[] modifier = new Object[tuple.length];
                    for(int i=0 ; i<tuple.length ; i++)
                    {
                        if(i==colonneamodifier)
                        {
                            modifier[i]=valeur;
                        }
                        else
                        {
                            modifier[i]=tuple[i];
                        }
                    }
                    tuple=modifier;
                
                avynamboarina.ajouterTuple(tuple);
            } 
        }
        else{

        

        String[] condition = extrait(format, "WHERE", "");
            
            
        String phrase="";
            phrase = phrase +"SELECT"+" ";
            phrase = phrase +"*"+" ";
            phrase = phrase +"FROM"+" "; 
            phrase = phrase +nomTabble+" ";
            phrase = phrase +"WHERE"+" ";
            for (String string : condition) {
                phrase = phrase + string +" ";
            }
            Relation a = Relation.requeteSelect(phrase);

            
            
            for (Object[] tuple : amodifier.getTuples()) {

                if(a.contientTuple(tuple, a.getTuples()))
                {
                    Object[] modifier = new Object[tuple.length];
                    for(int i=0 ; i<tuple.length ; i++)
                    {
                        if(i==colonneamodifier)
                        {
                            modifier[i]=valeur;
                        }
                        else
                        {
                            modifier[i]=tuple[i];
                        }
                    }
                    tuple=modifier;
                }
                avynamboarina.ajouterTuple(tuple);
            }
        }                       
            deleteLinesContaining(FILENAME,"Tuple pour la relation " + nomTabble , "");
            Relation b = new Relation(nomTabble, amodifier.getAttributs(), "t");
            for (Object[] t : avynamboarina.getTuples()) {
                b.ajouterTuple(t, "t");
            }

    }

    public static void requeteCreateView(String requete)
    {
        String[] format = Relation.formatRequete(requete);
        String nomView = "";
     
       if(format[format.length-2].equalsIgnoreCase("as"))
       {
            nomView=format[format.length-1];
       }
       System.out.println(nomView);

       String select = "";
       String[] partselect = Relation.extrait(format, "VIEW", "as");
       for(int i=1 ; i<partselect.length-1 ; i++)
       {
        select+=partselect[i]+" ";
       }
       System.out.println(select +"c\'est select");
       Relation a = Relation.requeteSelect(select);
       
       Relation b = new Relation(nomView, a.getAttributs() , "t");

       for (Object[] tuples : a.getTuples()) {
            b.ajouterTuple(tuples, "t");
       }
    }
/////mofier hasina
    public static String requete(String requete) throws IOException {
        StringBuilder resultat = new StringBuilder(); // Pour accumuler les resultats
        String[] format = formatRequete(requete);
        String debut = format[0];
        String justeapres = "";
        if(format.length>1)
        {
            justeapres = format[1];
        }
        if (debut.equalsIgnoreCase("SELECT")) {
            Relation a = requeteSelect(requete);
            resultat.append(a.afficher()); 
        } else if (debut.equalsIgnoreCase("CREATE") && justeapres.equalsIgnoreCase("TABLE")) {
            requeteCreateTable(requete);
            resultat.append("Table creee avec succès !");
        } else if (debut.equalsIgnoreCase("CREATE") && justeapres.equalsIgnoreCase("VIEW")) {
            requeteCreateView(requete);
            resultat.append("View creee avec succès !");
        } else if (debut.equalsIgnoreCase("DROP")) {
            requeteEffacerTable(requete);
            resultat.append("Table supprimee avec succès !");
        } else if (debut.equalsIgnoreCase("DELETE") && justeapres.equalsIgnoreCase("FROM") ) {
            requeteEffacerElementTableau(requete);
            resultat.append("Element supprimee avec succès !");
        } else if (debut.equalsIgnoreCase("INSERT")) {
            requeteInserer(requete);
            resultat.append("Donnees inserees avec succès !");
        } else if (debut.equalsIgnoreCase("SHOW")) {
            resultat.append(Database.afficherTable()); 
        } else if (debut.equalsIgnoreCase("DESCRIBE")) {
            resultat.append(requeteDescribe(requete)); 
        } else if (debut.equalsIgnoreCase("DESCRIBE")){
            requeteDescribe(requete);
        } else if (debut.equalsIgnoreCase("ROLLBACK")){
            rollback();
            resultat.append("Rollback effectué avec succès !");
        } else if (debut.equalsIgnoreCase("COMMIT")){
            commit();
            resultat.append("Commit effectué avec succès !");
        } else if (debut.equalsIgnoreCase("BACKUP")){
            FileBackup.backupFile(FILENAMESAUVEGARDE);
            resultat.append("backup effectué avec succès !");
        }else if (debut.equalsIgnoreCase("UPDATE") ){
            requeteUpdate(requete);
            resultat.append("update effectué avec succès !");
        }else {
            resultat.append("Commande inconnue !");
        }
        return resultat.toString(); 
    }

    public static String requeteLecture(String requete) throws IOException {
        Database.chargerRelationsDepuisFichier();
        StringBuilder resultat = new StringBuilder(); 
        String[] format = formatRequete(requete);
        String debut = format[0];
        if (debut.equalsIgnoreCase("SELECT")) {
            Relation a = requeteSelect(requete);
            resultat.append(a.afficher()); 
        }else if (debut.equalsIgnoreCase("SHOW")) {
            resultat.append(Database.afficherTable()); 
        } else if (debut.equalsIgnoreCase("DESCRIBE")) {
            resultat.append(requeteDescribe(requete)); 
        } 
        else {
            resultat.append("Commande inconnue ou non autorisée!");
        }

        return resultat.toString(); 
    }

    public static void requeteInserer(String requete)
    {   
        String[] result = formatRequete(requete);
        String nomTabble = extrait(result, "INTO", "VALUES")[0];
        String tuple[] = extrait(result, "(", ")");
        Relation a = Database.getRelation(nomTabble);
        Object [] t = Database.formatTuple(tuple, a); 
        a.ajouterTuple(t, "t");
        System.out.println("Les lignes ajoutee");
    }

    public static String requeteDescribe(String requete) {
        StringBuilder resultat = new StringBuilder();
    
        try {
            String[] result = formatRequete(requete);
            String nomTable = extrait(result, "DESCRIBE", "")[0];
    
            Relation a = Database.getRelation(nomTable);
    
            if (a != null) {
                resultat.append(a.describe()); // Suppose que describe() retourne un String
            } else {
                resultat.append("La table ").append(nomTable).append(" n'existe pas.");
            }
        } catch (Exception e) {
            resultat.append("Erreur dans la requête DESCRIBE : ").append(e.getMessage());
        }
    
        return resultat.toString();
    }

        public static Relation joinExterneRight(Relation moi , Relation autre , String nom_colomne1 , String rel ,String nom_colomne2  )
    {
        Relation joinbase = moi.produitCartesien(autre);
        Relation joingauche = moi.produitCartesien(autre);
        Relation joindroite = moi.produitCartesien(autre);
  
        List<Integer> nombre = new ArrayList<>();
        int indice=0;
        while(indice<moi.getAttributs().length/2)
        {
            nombre.add(indice);
            indice++;
        }
        Relation gauche = joingauche.projeter(convertListToIntArray(nombre));

        nombre=new ArrayList<>();
        while(indice<joinbase.getAttributs().length/2 )
        {
            nombre.add(indice);
            indice++;
        }
  
        Relation droite = joindroite.projeter(convertListToIntArray(nombre));
        droite.afficher();
        
        int indice1 = joinbase.getIndiceColonne(nom_colomne1);
        int indice2 = joinbase.getIndiceColonne(nom_colomne2);  
    
        Relation a = new Relation("a", joinbase.getAttributs());
    
        List<Object[]> tuples = joinbase.getTuples();
        for (int i=0 ; i<tuples.size() ; i++) {
          
            // Comparaison dynamique selon la relation
            if(comparerjoin(tuples.get(i)[indice1], tuples.get(i)[indice2], rel)) {
                a.ajouterTuple(tuples.get(i));   
            }
            else{
                //externe droite 
                Object[] nouveauxTuples = new Object[joinbase.getTuples().get(0).length];
                indice=0;
                for(int j=0; indice < gauche.getTuples().get(i).length ;j++)
                {
                    nouveauxTuples[indice]=null;
                    indice++;
                }
                for(int j=0; indice < gauche.getTuples().get(i).length+droite.getTuples().get(i).length ;j++)
                {
                    nouveauxTuples[indice]=gauche.getTuples().get(i)[j];
                    indice++;
                }
                a.ajouterTuple(nouveauxTuples);
            }
        }    
        return a;
    }


    public static Relation join(Relation moi , Relation autre , String nom_colomne1 , String rel ,String nom_colomne2  )
    {
        Relation join = moi.produitCartesien(autre);
        int indice1 = join.getIndiceColonne(nom_colomne1);
        int indice2 = join.getIndiceColonne(nom_colomne2);  
    
        Relation a = new Relation("a", join.getAttributs());
    
        List<Object[]> tuples = join.getTuples();
        for (Object[] tuple : tuples) {
    
            // Comparaison dynamique selon la relation
            if (comparerjoin(tuple[indice1], tuple[indice2], rel)) {
                a.ajouterTuple(tuple);   
            }
        }    
        return a;
    }
    
    public static Relation joinExterneLeft(Relation moi , Relation autre , String nom_colomne1 , String rel ,String nom_colomne2  )
    {
        Relation joinbase = moi.produitCartesien(autre);
        Relation joingauche = moi.produitCartesien(autre);
        Relation joindroite = moi.produitCartesien(autre);
  
        List<Integer> nombre = new ArrayList<>();
        int indice=0;
        while(indice<moi.getAttributs().length/2)
        {
            nombre.add(indice);
            indice++;
        }
        Relation gauche = joingauche.projeter(convertListToIntArray(nombre));

        nombre=new ArrayList<>();
        while(indice<joinbase.getAttributs().length/2 )
        {
            nombre.add(indice);
            indice++;
        }
  
        Relation droite = joindroite.projeter(convertListToIntArray(nombre));
        droite.afficher();
        
        int indice1 = joinbase.getIndiceColonne(nom_colomne1);
        int indice2 = joinbase.getIndiceColonne(nom_colomne2);  
    
        Relation a = new Relation("a", joinbase.getAttributs());
    
        List<Object[]> tuples = joinbase.getTuples();
        for (int i=0 ; i<tuples.size() ; i++) {
            // Comparaison dynamique selon la relation
            if(comparerjoin(tuples.get(i)[indice1], tuples.get(i)[indice2], rel)) {
                a.ajouterTuple(tuples.get(i));   
            }
            else{

                //externe gauche
                Object[] nouveauxTuples = new Object[joinbase.getTuples().get(0).length];
                indice=0;
                for(int j=0; indice < gauche.getTuples().get(i).length ;j++)
                {
                    nouveauxTuples[indice]=gauche.getTuples().get(i)[j];
                    indice++;
                }
                for(int j=0; indice < gauche.getTuples().get(i).length+droite.getTuples().get(i).length ;j++)
                {
                    nouveauxTuples[indice]=null;
                    indice++;
                }
                a.ajouterTuple(nouveauxTuples);
            }
        }    
        return a;
    }

    public static Relation joinExterne(Relation moi , Relation autre , String nom_colomne1 , String rel ,String nom_colomne2  )
    {
        Relation joinbase = moi.produitCartesien(autre);
        Relation joingauche = moi.produitCartesien(autre);
        Relation joindroite = moi.produitCartesien(autre);
  
        List<Integer> nombre = new ArrayList<>();
        int indice=0;
        while(indice<moi.getAttributs().length/2)
        {
            nombre.add(indice);
            indice++;
        }
        Relation gauche = joingauche.projeter(convertListToIntArray(nombre));

        nombre=new ArrayList<>();
        while(indice<joinbase.getAttributs().length/2 )
        {
            nombre.add(indice);
            indice++;
        }
  
        Relation droite = joindroite.projeter(convertListToIntArray(nombre));
        droite.afficher();
        
        int indice1 = joinbase.getIndiceColonne(nom_colomne1);
        int indice2 = joinbase.getIndiceColonne(nom_colomne2);  
    
        Relation a = new Relation("a", joinbase.getAttributs());
    
        List<Object[]> tuples = joinbase.getTuples();
        for (int i=0 ; i<tuples.size() ; i++) {
    
            // Comparaison dynamique selon la relation
            if(comparerjoin(tuples.get(i)[indice1], tuples.get(i)[indice2], rel)) {
                a.ajouterTuple(tuples.get(i));   
            }
            else{

                //externe gauche
                Object[] nouveauxTuples = new Object[joinbase.getTuples().get(0).length];
                indice=0;
                for(int j=0; indice < gauche.getTuples().get(i).length ;j++)
                {
                    nouveauxTuples[indice]=null;
                    indice++;
                }
                for(int j=0; indice < gauche.getTuples().get(i).length+droite.getTuples().get(i).length ;j++)
                {
                    nouveauxTuples[indice]=gauche.getTuples().get(i)[j];
                    indice++;
                }
                a.ajouterTuple(nouveauxTuples);

                 //externe droite 
                 nouveauxTuples = new Object[joinbase.getTuples().get(0).length];
                 indice=0;
                 for(int j=0; indice < gauche.getTuples().get(i).length ;j++)
                 {
                     nouveauxTuples[indice]=null;
                     indice++;
                 }
                 for(int j=0; indice < gauche.getTuples().get(i).length+droite.getTuples().get(i).length ;j++)
                 {
                     nouveauxTuples[indice]=gauche.getTuples().get(i)[j];
                     indice++;
                 }
                 a.ajouterTuple(nouveauxTuples);
            }
        }    
        return a;
    }

    public static boolean comparerjoin(Object val1, Object val2, String relation) {
        if (val1 instanceof Comparable && val2 instanceof Comparable) {
            Comparable c1 = (Comparable) val1;
            Comparable c2 = (Comparable) val2;
    
            switch (relation) {
                case "=":
                    return c1.compareTo(c2) == 0;
                case ">":
                    return c1.compareTo(c2) > 0;
                case "<":
                    return c1.compareTo(c2) < 0;
                case "<=":
                    return c1.compareTo(c2) <= 0;
                case ">=":
                    return c1.compareTo(c2) >= 0;
                default:
                    throw new IllegalArgumentException("Relation non supportée: " + relation);
            }
        } else {
            throw new IllegalArgumentException("Les valeurs doivent implémenter Comparable.");
        }
        }
        
      
    
    public static int[] convertListToIntArray(List<Integer> list) {
        // Créer un tableau de int[] de la même taille que la liste
        int[] array = new int[list.size()];
        
        // Remplir le tableau avec les valeurs de la liste
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);  // Autoboxing de Integer à int
        }
        
        return array;
    }
    
    public  void trier( int sens, String[] nomColonnes) {
        // Vérifier que toutes les colonnes spécifiées sont valides
        int[] colonnes= new int[nomColonnes.length];
        for(int i=0 ; i<nomColonnes.length ; i++)
        {
            colonnes[i]=getIndiceColonne(nomColonnes[i]);
        }

        List<Object[]> liste = this.getTuples();
        
        if (colonnes == null || colonnes.length == 0 || colonnes[0] < 0 || colonnes[0] >= liste.get(0).length) {
            throw new IllegalArgumentException("Les indices de colonnes sont hors limite.");
        }

        // Tri de la liste en fonction des colonnes spécifiées et du sens
        Collections.sort(liste, new Comparator<Object[]>() {
            @Override
            public int compare(Object[] o1, Object[] o2) {
                for (int col : colonnes) {
                    // Vérifier que la colonne est valide dans les deux tableaux
                    if (col < 0 || col >= o1.length || col >= o2.length) {
                        throw new IllegalArgumentException("Indice de colonne invalide dans l'un des éléments.");
                    }

                    // Comparer les éléments de la colonne spécifiée
                    Comparable c1 = (Comparable) o1[col];
                    Comparable c2 = (Comparable) o2[col];
                    int comparison = c1.compareTo(c2);

                    // Si la comparaison n'est pas égale, on retourne le résultat du tri
                    if (comparison != 0) {
                        return sens == 0 ? comparison : -comparison;
                    }
                }
                return 0; // Si toutes les colonnes sont égales, les éléments sont considérés égaux
            }
        });

        this.setTuples( new ArrayList<>());
        this.setTuples(liste); 
    }
    
    public static void commit() throws IOException
    {
        FileCopy.copyFile(FILENAME, FILENAMESAUVEGARDE);
        Database.chargerRelationsDepuisFichier();
    }
    
    public static void rollback() throws IOException
    {
        FileCopy.copyFile(FILENAMESAUVEGARDE,FILENAME );
        Database.chargerRelationsDepuisFichier();
    }


    public static List<String[]> extractWordsInParentheses(String str) {
        List<String[]> wordsList = new ArrayList<>();
        int i=0;
        String[] formate = formatRequete(str);
        System.out.println(i);
        while(i<formate.length)
        {
            if(formate[i].equals("("))
            {
                List<String> ligne = new ArrayList<>();
                i++;
                while(!formate[i].equals(")"))
                {
                    ligne.add(formate[i]);
                    i++;
                }
                wordsList.add(ligne.toArray(new String[0]));
                
            }
            i++;
        }
        return wordsList;
    }
    public static List<String> motliaison(String str) {
        List<String> wordsList = new ArrayList<>();
        int i=0;
        String[] formate = formatRequete(str);
        System.out.println(i);
        while(i<formate.length-2)
        {
            if(formate[i].equals(")") && formate[i+2].equals("("))
            {
                    wordsList.add(formate[i+1]);
                    i=i+2;                
            }
            i++;
        }
        return wordsList;
    }


    
public static void main(String[] args) {
    System.out.println(Relation.getFilenamesauvegarde());
}

}