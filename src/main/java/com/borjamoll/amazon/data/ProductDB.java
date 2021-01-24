package com.borjamoll.amazon.data;


import javax.persistence.*;
import java.util.Date;

@Entity
public class ProductDB{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    private Double price;
    private Date date;
    private String name;
    @Column(length = 800)
    private String url;

    public ProductDB() {
    }

    public ProductDB(String name, Double price, Date date, String url) {

        this.price = price;
        this.date = date;
        this.name = name;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @PrePersist
    public void onPrePersist() {
        date = new Date();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Date getDate(){return date;}
}
