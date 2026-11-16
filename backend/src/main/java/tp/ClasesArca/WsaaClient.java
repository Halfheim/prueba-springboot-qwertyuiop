package tp.ClasesArca;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;

import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

//  la clase es para Autenticación obligatoria con WSAA: 
@Component
public class WsaaClient {

    public record Ticket(String token, String sign, Instant expira) {
    }

    @Value("${arca.key-path}")
    private String keyPath;
    @Value("${arca.cert-path}")
    private String certPath;
    @Value("${arca.ta-path}")
    private String taPath;
    @Value("${arca.wsaa-url}")
    private String wsaaUrl;
    @Value("${arca.servicio:wsfe}")
    private String servicio;

    private Ticket cache;

    /*
     * obtiene Token + Sign. el ticket dura 12 horas, Si ya lo pidió hace un rato y
     * sigue vigente, lo devuelve al instante.
     * Si la aplicación se reinició, busca en un archivo guardado en el disco duro.
     * Si sigue vigente, lo lee y lo usa.
     * Si ninguno sirve, se conecta oficialmente a ARCA para generar uno nuevo.
     */
    public synchronized Ticket obtenerTicket() throws Exception {
        if (esValido(cache))
            return cache;

        Ticket enDisco = leerDeDisco();
        if (esValido(enDisco)) {
            cache = enDisco;
            return cache;
        }

        String loginTicketResponse = pedirNuevoTicket();
        Files.writeString(Path.of(taPath), loginTicketResponse, StandardCharsets.UTF_8);
        cache = parsearTicket(loginTicketResponse);
        return cache;
    }

    private boolean esValido(Ticket t) {
        return t != null && t.expira().isAfter(Instant.now().plusSeconds(300));
    }

    private Ticket leerDeDisco() {
        try {
            Path p = Path.of(taPath);
            if (!Files.exists(p))
                return null;
            return parsearTicket(Files.readString(p, StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }

    private Ticket parsearTicket(String xml) throws Exception {
        Document doc = SoapUtil.parse(xml);
        String token = SoapUtil.text(doc, "token");
        String sign = SoapUtil.text(doc, "sign");
        String exp = SoapUtil.text(doc, "expirationTime");
        if (token == null || sign == null || exp == null) {
            throw new IllegalStateException("Respuesta WSAA inesperada: " + xml);
        }
        return new Ticket(token, sign, OffsetDateTime.parse(exp).toInstant());
    }

    // Toma el TRA ya firmado lo envuelve en una petición SOAP , se lo manda al
    // servidor de WSAA de ARCA y recibe la respuesta, extrae el XML) y separar el
    // token y el sign.
    private String pedirNuevoTicket() throws Exception {
        String cmsBase64 = firmarTra(armarTra());

        String soap = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
                + " xmlns:wsaa=\"http://wsaa.view.sua.dvadac.desein.afip.gov\">"
                + "<soapenv:Header/><soapenv:Body>"
                + "<wsaa:loginCms><wsaa:in0>" + cmsBase64 + "</wsaa:in0></wsaa:loginCms>"
                + "</soapenv:Body></soapenv:Envelope>";

        String respuesta = SoapUtil.post(wsaaUrl, "\"\"", soap);
        String ticketXml = SoapUtil.text(SoapUtil.parse(respuesta), "loginCmsReturn");
        if (ticketXml == null) {
            throw new IllegalStateException("WSAA no devolvió loginCmsReturn: " + respuesta);
        }
        return ticketXml;
    }

    // Crea un pequeño documento XML llamado TRA con una clave unica
    private String armarTra() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx");
        ZonedDateTime ahora = ZonedDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"));
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<loginTicketRequest version=\"1.0\"><header>"
                + "<uniqueId>" + (System.currentTimeMillis() / 1000) + "</uniqueId>"
                + "<generationTime>" + ahora.minusMinutes(10).format(fmt) + "</generationTime>"
                + "<expirationTime>" + ahora.plusMinutes(10).format(fmt) + "</expirationTime>"
                + "</header><service>" + servicio + "</service></loginTicketRequest>";
    }

    // firma el tra de forma digital con nuestra clave privada
    private String firmarTra(String tra) throws Exception {
        PrivateKey clave = cargarClavePrivada();
        X509Certificate cert = cargarCertificado();

        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(clave);
        gen.addSignerInfoGenerator(
                new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().build())
                        .build(signer, cert));
        gen.addCertificates(new JcaCertStore(List.of(cert)));

        CMSSignedData firmado = gen.generate(
                new CMSProcessableByteArray(tra.getBytes(StandardCharsets.UTF_8)), true);
        return Base64.getEncoder().encodeToString(firmado.getEncoded());
    }

    private PrivateKey cargarClavePrivada() throws Exception {
        try (PEMParser parser = new PEMParser(new FileReader(keyPath))) {
            Object obj = parser.readObject();
            JcaPEMKeyConverter conv = new JcaPEMKeyConverter();
            if (obj instanceof PEMKeyPair kp)
                return conv.getKeyPair(kp).getPrivate();
            if (obj instanceof PrivateKeyInfo pki)
                return conv.getPrivateKey(pki);
            throw new IllegalStateException("Formato de clave privada no reconocido en " + keyPath);
        }
    }

    private X509Certificate cargarCertificado() throws Exception {
        try (FileInputStream in = new FileInputStream(certPath)) {
            return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(in);
        }
    }
}