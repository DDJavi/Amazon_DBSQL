package com.borjamoll.amazon.utils;

import com.borjamoll.amazon.data.Catalogue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;


public class JAXBxml {
    private Logger log = LoggerFactory.getLogger(getClass());

    public void convertObjecttoXML(Object object, String path) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Catalogue.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
        m.marshal(object, new File(path));
        log.info("created");
    }

    public Catalogue convertXMLtoObject(String path) throws JAXBException {
        File file = new File(path);
        if(!file.exists()) {
            log.error("No existe");
            return null;
        }else{
            JAXBContext context = JAXBContext.newInstance(Catalogue.class);
            Unmarshaller um = context.createUnmarshaller();
            Catalogue catalogue = (Catalogue) um.unmarshal(new File(path));
            return catalogue;
        }
    }

}
