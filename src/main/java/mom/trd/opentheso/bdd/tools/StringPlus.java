package mom.trd.opentheso.bdd.tools;

import java.util.Vector;

public class StringPlus {

    public StringPlus() {

    }

    /**
     * Cette fonction permet de caster les cotes pour les faire passer par le
     * jdbc pour échanger avec les bases de données PostgreSQL exp : l'équipe ->
     * l''équipe
     *
     * @param ligne
     * @return String une phrase avec des cotes en plus
     */
    public String addQuotes(String ligne) {
        String ligne_prete = "";
        String ligne_temp = "";
        int index = -1;

        while (true) {
            index = ligne.indexOf("'");
            if (index == -1) {
                return (ligne_prete + ligne);
            }
            ligne_temp = ligne.substring(0, index + 1);
            ligne_prete = ligne_prete + ligne_temp.concat("'");
            ligne = ligne.substring(index + 1, ligne.length());
        }
    }

    public Vector<String> splitPlus(String ligne, String separateur) {

        Vector<String> v = new Vector<String>();

        while (ligne.indexOf(separateur) != -1) {
            v.addElement(ligne.substring(0, ligne.indexOf(separateur)).trim());
            ligne = ligne.substring(ligne.indexOf(separateur) + separateur.length());
        }
        if (ligne.length() > 0) {
            v.addElement(ligne.trim());
        }
        return v;
    }

    /**
     * Handle quotes and backslashes in string before database insertion.
     *
     * @param s The string to convert.
     * @return A safe string for database insertion.
     */
    public String convertString(String s) {
        if (s == null) {
            return null;
        }
        // Handle quotes
        s = s.replaceAll("\\'", "''");
        // Handle backslashes (You like the Java style...)
        s = s.replaceAll("\\\\", "\\\\\\\\");
        return s.trim();
    }
    
    /**
     * Fonction qui permet de normaliser les textes pour les documents XML
     * @param s
     * @return 
     */
    public String normalizeStringForXml(String s) {
        if (s == null) {
            return null;
        }
        // normalisation of words for XML
        s = s.replaceAll("&", "&amp;");
        s = s.replaceAll("\"", " ");
        s = s.replaceAll("\n", " ");
        s = s.replaceAll("\t", " ");
        return s;
    }
    
    /**
     * Fonction qui permet de normaliser les textes pour les documents XML
     * @param s
     * @return 
     */
    public String clearStringForSerach(String s) {
        if (s == null) {
            return null;
        }
        // normalisation of words for XML
        s = s.replaceAll("&", " ");
        s = s.replaceAll("-", " ");
        s = s.replaceAll("_", " ");
        s = s.replaceAll("/", " ");
        s = s.replaceAll("(", " ");
        s = s.replaceAll(")", " ");        
        
        return s;
    }    
    
    
}
