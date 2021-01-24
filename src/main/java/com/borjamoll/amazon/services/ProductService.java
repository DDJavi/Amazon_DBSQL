package com.borjamoll.amazon.services;

import com.borjamoll.amazon.data.*;
import com.borjamoll.amazon.repositories.ProductRepository;
import com.borjamoll.amazon.utils.JAXBxml;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.*;


/**
 * private final String ->atajos de CSS para .select
 */
@Service
public class ProductService {

    private final String AMAZON_URL = "https://www.amazon.es/s?k=";
    private final String normalSearch = "&dc&__mk_es_ES=ÅMÅŽÕÑ&qid=1606578057&rnid=831276031&ref=sr_nr_p_85_1";
    private final String primeSearch = "&rh=p_85%3A831314031&dc&__mk_es_ES=ÅMÅŽÕÑ&qid=1606578115&rnid=831276031&ref=sr_nr_p_85_1";
    private final String priceCSS = "span#price_inside_buybox";
    private final String priceNewCSS = "span#newBuyBoxPrice";
    private final String urlSectionCSS = "a.a-link-normal";
    private final String productTitle = "span#productTitle";
    private final String href = "abs:href";
    private final String starsCSS = "span#acrPopover";
    private final String sectionCSS = "h2.a-size-mini";
    private final String primeW = "Prime";
    private final String xmlW = ".xml";

    JAXBxml _jaxb = new JAXBxml();
    Catalogue catalogue = new Catalogue();
    private Document doc;
    private ArrayList<Product> products = new ArrayList<>();

    private Elements sections = null;
    private Document link = null;
    private String urlSection;
    private String stars;
    private Double price;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProductRepository repository;

    /**
     *
     * @param key todos los valores de busqueda
     * @return jsonToString con objectmapper
     * @throws JsonProcessingException
     * @throws JAXBException
     * convertXMLtoObject recibe el path formado por la busqueda y devuelve un objeto que se mapea y termina el servicio
     * convertObjectToXML crea el fichero xml y no devuelve nada
     * productList() rellena la arraylist de productos
     */
    public String run(Search key) throws JsonProcessingException, JAXBException {

        if(key.isRead()) {
            if (key.isPrime()) {
                catalogue = _jaxb.convertXMLtoObject(key.getKey()+primeW+xmlW);
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(catalogue);
            }else{
                catalogue = _jaxb.convertXMLtoObject(key.getKey()+xmlW);
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(catalogue);
            }
        }

        if(key.getKey().equals("") || key.getKey()==null){
            return "Bad request";
        }
        try {
            if(key.isPrime()){
                doc = Jsoup.connect(AMAZON_URL + key.getKey().replace(" ", "+") + primeSearch).get();
            }else{
                doc = Jsoup.connect(AMAZON_URL + key.getKey() + normalSearch).get();
            }

        } catch (Exception e) {
            return "Search not found";
        }
        sections = doc.select(sectionCSS);
        if(key.getTotal()>1) products = productList(sections, key.getTotal());
        else products = productList(sections,6);

        catalogue.setListName(key.getKey() + "Catalogue");
        catalogue.setProduct(products);
        if(key.isSave()) {
            if(key.isPrime()) {
                _jaxb.convertObjecttoXML(catalogue, key.getKey() + "Prime.xml");
            }else{
                _jaxb.convertObjecttoXML(catalogue, key.getKey() + ".xml");
            }
        }
        /**
        ParserXML parser = new ParserXML();
        try {
            parser.createXML(products, key.getKey());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        **/

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(catalogue);
    }

    public ArrayList<Product> productList(Elements list, int total){
        int i=0;
        for (Element section : list) {
            if(i==total) return products;
            i++;
            urlSection = section.select(urlSectionCSS).attr(href);
            log.info(urlSection);
            try {
                link = Jsoup.connect(urlSection).get();
                stars = link.select(starsCSS).attr("title");
                if(stars.length()<1){
                    stars="No stars";
                }
                price = Double.parseDouble(link.select(priceCSS).text().split(" ")[0].replace(".","").replace(",","."));
                if (link.select(priceCSS).text().length() < link.select(priceNewCSS).text().length()) {
                    price = Double.parseDouble(link.select(priceNewCSS).text().split(" ")[0].replace(".","").replace(",","."));
                }
                if (price > 1) {
                    products.add(new Product(link.select(productTitle).text(), price, stars, urlSection));
                }
            } catch (Exception e) {
                log.error("Lost product",e);
                i--;
            }
        }
        return products;
    }

    public String newTracer(Search key,String url ,ProductRepository repository){

        return key.getKey() + " TRACER ADDED";
    }

    public void addTracer (String key, String urlProduct, ProductRepository repository) throws IOException {
        link = Jsoup.connect(urlProduct).get();
        price = Double.parseDouble(link.select(priceCSS).text().split(" ")[0].replace(".","").replace(",","."));
        if (link.select(priceCSS).text().length() < link.select(priceNewCSS).text().length()) {
            price = Double.parseDouble(link.select(priceNewCSS).text().split(" ")[0].replace(".","").replace(",","."));
        }
        if (price > 0) {
            repository.save(new ProductDB(key, price,new Date(), urlProduct));
            log.info(key + " ADDED");
        }else{
            log.warn("Producto no trazable");
        }
    }
    public void updateTracers(ProductRepository repository) throws IOException {
        List<ProductDB> total = repository.findAll();
        ArrayList<String> result = new ArrayList<>();
        for(ProductDB one : total){
            if(!result.contains(one.getName()) && one.getName()!=null){
                addTracer(one.getName(),one.getUrl(),repository);
                result.add(one.getName());
            }
        }
    }

    public String TracerList(ProductRepository repository) throws JsonProcessingException {
        List<ProductDB> total = repository.findAll();
        ArrayList<String> result = new ArrayList<>();
        for(ProductDB one : total){
            result.add(one.getName() + " --> " + one.getUrl());
        }
        Set<String> hashSet = new HashSet<String>(result);
        result.clear();
        result.addAll(hashSet);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(result);
    }


    public String TracerProduct(String key, ProductRepository repository) throws JsonProcessingException {
        List<String> total = repository.findAllTracers(key);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(total);
    }
    @Transactional
    public int deleteTracer(String key, ProductRepository repository){
        int total = repository.deleteProductDBByName(key);
        return total;
    }
    }