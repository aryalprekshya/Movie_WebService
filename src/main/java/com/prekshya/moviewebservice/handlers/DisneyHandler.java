/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prekshya.moviewebservice.handlers;


/**
 *
 * @author Prekshya
 */

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.w3c.dom.NodeList;


public class DisneyHandler implements SOAPHandler<SOAPMessageContext>{

   
    
    @Override
    public Set<QName> getHeaders() {
        return new HashSet<QName>();
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        
       Boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
       if(!outbound){
            SOAPMessage message = context.getMessage();
              
                  NodeList titleElements;
           try {
               titleElements = message.getSOAPBody().getElementsByTagName("name");
                 if(titleElements.item(0) != null){
                      String title = titleElements.item(0).getTextContent();
                        
                      if(title.toLowerCase().contains("disney")){
                          return false;
                      }
                  }
                   } catch (SOAPException ex) {
               Logger.getLogger(DisneyHandler.class.getName()).log(Level.SEVERE, null, ex);
           
           
           } 
           
              
       }
       return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return false;    
    }

    @Override
    public void close(MessageContext context) {
    }
    
}