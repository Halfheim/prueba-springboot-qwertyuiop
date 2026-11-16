package tp.ClasesArca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Cliente de WSFEv1: último comprobante autorizado y solicitud de CAE. */
@Component
public class WsfeClient {

    /** Datos que hacen falta para pedir el CAE de un comprobante. */
    public record Solicitud(int ptoVta, int cbteTipo, long nro, int docTipo, long docNro,
            int condicionIvaReceptor, String fecha,
            BigDecimal total, BigDecimal neto, BigDecimal iva) {
    }

    /** resultado: "A" = aprobado, "R" = rechazado. */
    public record Resultado(String resultado, String cae, Date caeVto, String observaciones) {
    }

    private static final String NS = "http://ar.gov.afip.dif.FEV1/";

    @Value("${arca.cuit}")
    private long cuit;
    @Value("${arca.wsfe-url}")
    private String url;
    @Value("${arca.cbte-tipo:11}")
    private int cbteTipo;

    @Autowired
    private WsaaClient wsaa;

    public int getCbteTipo() {
        return cbteTipo;
    }

    // pregunta cual fue el numero de la ultima factura, de esta forma sabemos cual
    // sera el siguiente
    public long ultimoAutorizado(int ptoVta, int tipo) throws Exception {
        String cuerpo = "<ar:FECompUltimoAutorizado>" + auth()
                + "<ar:PtoVta>" + ptoVta + "</ar:PtoVta>"
                + "<ar:CbteTipo>" + tipo + "</ar:CbteTipo>"
                + "</ar:FECompUltimoAutorizado>";
        String resp = SoapUtil.post(url, "\"" + NS + "FECompUltimoAutorizado\"", envelope(cuerpo));
        Document doc = SoapUtil.parse(resp);

        String nro = SoapUtil.text(doc, "CbteNro");
        if (nro == null) {
            throw new IllegalStateException("FECompUltimoAutorizado sin CbteNro: " + erroresOResp(doc, resp));
        }
        return Long.parseLong(nro);
    }

    // Toma todos los datos de la factura que armó el usuario (cliente, tipo de
    // documento, importe total, neto, IVA, etc.), los empaqueta en un XML con la
    // estructura estricta que exige ARCA y se los manda para pedir el CAE
    public Resultado solicitarCae(Solicitud s) throws Exception {
        boolean tipoC = s.cbteTipo() == 11 || s.cbteTipo() == 12 || s.cbteTipo() == 13;

        StringBuilder det = new StringBuilder();
        det.append("<ar:FECAEDetRequest>")
                .append("<ar:Concepto>1</ar:Concepto>") // 1 = Productos
                .append("<ar:DocTipo>").append(s.docTipo()).append("</ar:DocTipo>")
                .append("<ar:DocNro>").append(s.docNro()).append("</ar:DocNro>")
                .append("<ar:CbteDesde>").append(s.nro()).append("</ar:CbteDesde>")
                .append("<ar:CbteHasta>").append(s.nro()).append("</ar:CbteHasta>")
                .append("<ar:CbteFch>").append(s.fecha()).append("</ar:CbteFch>")
                .append("<ar:ImpTotal>").append(imp(s.total())).append("</ar:ImpTotal>")
                .append("<ar:ImpTotConc>0.00</ar:ImpTotConc>")
                .append("<ar:ImpNeto>").append(imp(s.neto())).append("</ar:ImpNeto>")
                .append("<ar:ImpOpEx>0.00</ar:ImpOpEx>")
                .append("<ar:ImpTrib>0.00</ar:ImpTrib>")
                .append("<ar:ImpIVA>").append(imp(s.iva())).append("</ar:ImpIVA>")
                .append("<ar:MonId>PES</ar:MonId>")
                .append("<ar:MonCotiz>1</ar:MonCotiz>")
                .append("<ar:CondicionIVAReceptorId>").append(s.condicionIvaReceptor())
                .append("</ar:CondicionIVAReceptorId>");

        if (!tipoC) { // Facturas A/B discriminan IVA (alícuota 21% = Id 5)
            det.append("<ar:Iva><ar:AlicIva><ar:Id>5</ar:Id>")
                    .append("<ar:BaseImp>").append(imp(s.neto())).append("</ar:BaseImp>")
                    .append("<ar:Importe>").append(imp(s.iva())).append("</ar:Importe>")
                    .append("</ar:AlicIva></ar:Iva>");
        }
        det.append("</ar:FECAEDetRequest>");

        String cuerpo = "<ar:FECAESolicitar>" + auth()
                + "<ar:FeCAEReq><ar:FeCabReq>"
                + "<ar:CantReg>1</ar:CantReg>"
                + "<ar:PtoVta>" + s.ptoVta() + "</ar:PtoVta>"
                + "<ar:CbteTipo>" + s.cbteTipo() + "</ar:CbteTipo>"
                + "</ar:FeCabReq><ar:FeDetReq>" + det + "</ar:FeDetReq></ar:FeCAEReq>"
                + "</ar:FECAESolicitar>";

        String resp = SoapUtil.post(url, "\"" + NS + "FECAESolicitar\"", envelope(cuerpo));
        Document doc = SoapUtil.parse(resp);

        NodeList dets = doc.getElementsByTagNameNS("*", "FECAEDetResponse");
        if (dets.getLength() == 0) {
            throw new IllegalStateException("FECAESolicitar sin detalle: " + erroresOResp(doc, resp));
        }
        Element d = (Element) dets.item(0);

        String resultado = SoapUtil.text(d, "Resultado");
        String cae = vacioANull(SoapUtil.text(d, "CAE"));
        String vto = vacioANull(SoapUtil.text(d, "CAEFchVto"));

        StringBuilder obs = new StringBuilder();
        NodeList obsList = d.getElementsByTagNameNS("*", "Obs");
        for (int i = 0; i < obsList.getLength(); i++) {
            Element o = (Element) obsList.item(i);
            obs.append(SoapUtil.text(o, "Code")).append(": ").append(SoapUtil.text(o, "Msg")).append(" | ");
        }
        String errores = errores(doc);
        if (errores != null)
            obs.append(errores);

        Date caeVto = vto != null ? new SimpleDateFormat("yyyyMMdd").parse(vto) : null;
        return new Resultado(resultado, cae, caeVto, obs.length() == 0 ? null : obs.toString());
    }

    // metodos que aseguran que los datos esten correctos y asegurar el formato

    private String auth() throws Exception {
        WsaaClient.Ticket t = wsaa.obtenerTicket();
        return "<ar:Auth><ar:Token>" + t.token() + "</ar:Token><ar:Sign>" + t.sign()
                + "</ar:Sign><ar:Cuit>" + cuit + "</ar:Cuit></ar:Auth>";
    }

    private String envelope(String cuerpo) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ar=\"" + NS
                + "\">"
                + "<soapenv:Header/><soapenv:Body>" + cuerpo + "</soapenv:Body></soapenv:Envelope>";
    }

    private String imp(BigDecimal v) {
        return v.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String vacioANull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private String errores(Document doc) {
        NodeList errs = doc.getElementsByTagNameNS("*", "Err");
        if (errs.getLength() == 0)
            return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < errs.getLength(); i++) {
            Element e = (Element) errs.item(i);
            sb.append(SoapUtil.text(e, "Code")).append(": ").append(SoapUtil.text(e, "Msg")).append(" | ");
        }
        return sb.toString();
    }

    private String erroresOResp(Document doc, String resp) {
        String e = errores(doc);
        return e != null ? e : resp;
    }
}