package utilities;

import java.util.Random;

public class Utility {
    public static void separator(){
        System.out.println("<---><---><--->");
    }
    public static String generateRandomString(int len) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

    public static double generateRandomPrice(int precision){
        double random = Math.random() * 10000;
        long round = Math.round(random);
        return (double) round / precision;
    }
    public static double generateRandomPrice(){
        return generateRandomPrice(100);
    }
}
