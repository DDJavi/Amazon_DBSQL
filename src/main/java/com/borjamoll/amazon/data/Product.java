package com.borjamoll.amazon.data;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.*;


@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = {"productName", "price", "stars","url"})
public class Product {

    private String productName;
    private double price;
    private String stars;
    private String url;

    @XmlElement(name = "title")
    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public String getStars() {
        return stars;
    }

    public String getUrl(){return url;}

}