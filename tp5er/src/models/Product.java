package models;

import java.util.Date;

public class Product {
    private static int globalId = 0;
    private final int id;
    private String name;
    private Double price;
    private Date expiration;

    public Product(String name, Double price, Date expiration) {
        this.name = name;
        this.price = price;
        this.expiration = expiration;
        this.id = globalId++;
    }

    public Product(int id,String name, Double price, Date expiration) {
        this.name = name;
        this.price = price;
        this.expiration = expiration;
        this.id = id;
    }

    @Override
    public String toString() {
        return "Produit : [" +
                "id = " + id +
                ", nom = '" + name + '\'' +
                ", prix = " + price +
                ", expiration = " + expiration +
                ']';
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
}
