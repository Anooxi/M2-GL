
public class Main {
    public static void main(String[] args) {
        String input = "JeMeTriple"; // Input pour la méthode
        method(input); // Appel de la méthode
    }
    // Méthode principale
    public static void method(String str){
        System.out.println(aString(str)); // Utilisation de la nouvelle méthode directement dans le champ nécessaire
    }
    // Nouvelle méthode qui remplace la variable
    private static String aString(String str) {
        return method2(str).concat(str).toLowerCase();
    }

    // Méthode secondaire
    public static String method2(String str){
        return str + str;
    }
}


