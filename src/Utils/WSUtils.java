/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Ulises Beltrán Gómez --- beltrangomezulises@gmail.com
 */
public class WSUtils {

    public static String consumeService(String methodName, LinkedHashMap<String, Object> params) {
        String stringResponse = "";
        
        try {        
        // Create SOAP Connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        // Send SOAP Message to SOAP Server
        String url = "YOUR ENDPOINT";
        
        SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(methodName, params), url);

        // print SOAP Response                        
        String response = WSUtils.SOAPResponseToString(soapResponse);
        System.out.println(response);
        System.out.println(response.length());

        //buscar el valor return
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource src = new InputSource();
        src.setCharacterStream(new StringReader(response));

        Document doc = builder.parse(src);
        stringResponse = doc.getElementsByTagName("tag name").item(0).getTextContent();
        //String name = doc.getElementsByTagName("name").item(0).getTextContent();
        System.out.println(stringResponse);

        soapConnection.close();
            
        } catch (SOAPException | UnsupportedOperationException | ParserConfigurationException | SAXException | IOException | DOMException e) {
            e.printStackTrace();
        }
        
        return stringResponse;

    }

    private static SOAPMessage createSOAPRequest(String methodName, LinkedHashMap<String, Object> params) {
        MessageFactory messageFactory;
        SOAPMessage soapMessage = null;

        try {
            messageFactory = MessageFactory.newInstance();
            soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();

            String serverURI = "ROOT URI (https://www.webservice.com)";

            // SOAP Envelope
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration("urn", serverURI);
            //SOAP Boyde
            SOAPBody soapBody = envelope.getBody();
            SOAPElement methodTag = soapBody.addChildElement(methodName, "urn");

            //Asigancion de parametros
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                System.out.println("clave=" + entry.getKey() + ", valor=" + entry.getValue());
                SOAPElement tag = methodTag.addChildElement(entry.getKey());
                tag.addTextNode((String) entry.getValue());
            }

//        MimeHeaders headers = soapMessage.getMimeHeaders();
//        headers.addHeader("urn:get_tipo_inmueblewsdl#get_tipo_inmueble", serverURI  + "VerifyEmail");
            soapMessage.saveChanges();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return soapMessage;
    }

    private static String SOAPResponseToString(SOAPMessage soapResponse) {
        String message = "";
        try {
            SOAPBody element = soapResponse.getSOAPBody();
            DOMSource source = new DOMSource(element);
            StringWriter stringResult = new StringWriter();
            TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
            message = stringResult.toString();

        } catch (SOAPException | TransformerException e) {
            e.printStackTrace();
        }
        return message;
    }
}
