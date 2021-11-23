package repositories;

import models.Product;

import java.util.ArrayList;
import java.util.Optional;

// Singleton
public class Shop {
    private ArrayList<Product> products;
    private static Shop instance = new Shop();

    private Shop() {
        products = new ArrayList<>();
    }

    public static Shop getInstance() {
        return instance;
    }

    public void display(){
        for (Product p : products) {
            System.out.println(p);
        }
    }

    @Override
    public String toString() {
        return products.toString();
    }

    public void addProduct(Product newProduct) throws Exception {
        Optional<Product> product = products.stream()
                .filter(p -> p.getId() == newProduct.getId())
                .findAny();
        if(product.isPresent()){
            throw new Exception("Product : " + newProduct + " already exists in the Shop");
        } else {
            this.products.add(newProduct);
        }
    }

    public void deleteProduct(int deleteId) throws Exception {
        Optional<Product> product = products.stream()
                .filter(p -> p.getId() == deleteId)
                .findAny();
        if(product.isEmpty()){
            throw new Exception("Product with id : " + deleteId + " does not exists in the Shop");
        } else {
            products.remove(product.get());
        }
    }

    public void updateProduct(Product updatedProduct) throws Exception {
        Optional<Product> product = products.stream()
                .filter(p -> p.getId() == updatedProduct.getId())
                .findAny();
        if(product.isEmpty()){
            throw new Exception("Product : " + updatedProduct + " does not exists in the Shop");
        } else {
            // On peut juste modifier le Product dans l'ArrayList mais la sa peut créer des opportunités pour les logs
            deleteProduct(updatedProduct.getId());
            addProduct(updatedProduct);
        }
    }

    public Product fetchProduct(int fetchId) throws Exception {
        Optional<Product> product = products.stream()
                .filter(p -> p.getId() == fetchId)
                .findAny();
        if(product.isPresent()){
            return product.get();
        } else {
            throw new Exception("Cannot fetch product with id : " + fetchId);
        }
    }
}
