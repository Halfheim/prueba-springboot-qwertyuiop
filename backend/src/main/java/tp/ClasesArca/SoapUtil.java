package tp.ClasesArca;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

final class SoapUtil {

    // Prepara un cliente HTTP para conectarse a los servidores externos
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    private SoapUtil() {
    }

    // Toma el texto XML crudo que devuelve ARCA y lo convierte en un objeto
    // manipulable en Java
    static Document parse(String xml) throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(true);
        f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return f.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
    }

    // buscador rapido para leer la respuesta de ARCA, lo usamos para ver el cae
    static String text(org.w3c.dom.Node contexto, String nombreLocal) {
        NodeList nl = (contexto instanceof Document d)
                ? d.getElementsByTagNameNS("*", nombreLocal)
                : ((org.w3c.dom.Element) contexto).getElementsByTagNameNS("*", nombreLocal);
        return nl.getLength() == 0 ? null : nl.item(0).getTextContent().trim();
    }

    // Arma la petición HTTP POST y envia a arca y se hay error responde con
    // expection
    static String post(String url, String soapAction, String xml) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(60))
                .header("Content-Type", "text/xml; charset=utf-8")
                .header("SOAPAction", soapAction)
                .POST(HttpRequest.BodyPublishers.ofString(xml, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() != 200) {
            String fault = null;
            try {
                fault = text(parse(resp.body()), "faultstring");
            } catch (Exception ignorada) {
            }
            throw new IllegalStateException("ARCA respondió HTTP " + resp.statusCode()
                    + (fault != null ? ": " + fault : ""));
        }
        return resp.body();
    }
}