package com.borjamoll.amazon.controller;


import com.borjamoll.amazon.repositories.ProductRepository;
import com.borjamoll.amazon.data.Search;
import com.borjamoll.amazon.services.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * GET ("/") Devuelve lista de los objetos trazados
 * POST("/") Busqueda de catalogo en amazon
 * POST("/search-tracer") Introducir palabra clave para ver la trazabilidad
 * POST("/trace") Introducir palabra clave y url para nueva trazabilidad
 * GET("/trace") Actualiza todos los productos
 * POST("/delete") Elimina la trazabilidad de cualquier producto
 */
@RestController
public class ProductController {
    @Autowired
    private ProductRepository repository;
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     *
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("/")
    String tracerListNames() throws JsonProcessingException {
        ProductService productService = new ProductService();
        return productService.TracerList(repository);
    }
    //BUSQUEDA DE CATALAGO
    @PostMapping("/")
    String Catalogue(@RequestBody Search key) throws JsonProcessingException, JAXBException {
        ProductService productService = new ProductService();
        return productService.run(key);
    }
    //BUSCA LA TRAZABILIDAD DE UN PRODUCTO
    @PostMapping("/search-tracer")
    String searchProduct(@RequestBody Search key) throws JsonProcessingException, JAXBException {
        ProductService productService = new ProductService();
        return productService.TracerProduct(key.getKey(),repository);

    }
    //Introduce name y url con la que guardar el tracer
    @PostMapping("/trace")
    String newTracer(@RequestBody Search key) throws IOException {
        ProductService productService = new ProductService();
        productService.addTracer(key.getKey(),key.getUrl(),repository);
        return "added";
    }
    //ACTUALIZA TODOS LOS TRACERS
    @GetMapping("/trace")
    String updateTracer() throws IOException {
        ProductService productService = new ProductService();
        productService.updateTracers(repository);
        return "updated";
    }
    //Elimina cualquier tracer introducido
    @PostMapping("/delete")
    int deleteTracer(@RequestBody Search key){
        ProductService productService = new ProductService();
        return productService.deleteTracer(key.getKey(),repository);
    }
}
