import repositories.Shop;
import models.*;
import utilities.Utility;

import java.util.Date;
import java.util.logging.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // Initialisation
        Shop shop = Shop.getInstance();
        for (int i = 0; i < 5; i++){
            shop.addProduct(
                    new Product(
                            Utility.generateRandomString(5),
                            Utility.generateRandomPrice(100),
                            new Date())
            );
        }
        shop.display();
        Utility.separator();
        // Ajout de produit
        shop.addProduct(
                new Product(
                        "New Product",
                        Utility.generateRandomPrice(100),
                        new Date()
                )
        );
        shop.display();
        Utility.separator();
        // Suppression de produit
        shop.deleteProduct(3);
        shop.display();
        Utility.separator();
        //Update de produit
        shop.updateProduct(
                new Product(
                        4,
                        "Updated Product",
                        Utility.generateRandomPrice(),
                        new Date()
                )
        );
        shop.display();
        Utility.separator();
        // Fetch de produit
        System.out.println(shop.fetchProduct(4));
        Utility.separator();
        // Erreur
        shop.deleteProduct(10);
    }

}
