package kg.gov.tunduk.xroad.service_metadata_protocol;

import kg.gov.tunduk.xroad.XRoadHeader;
import kg.gov.tunduk.xroad.service_metadata_protocol.model.*;
import kg.gov.tunduk.xroad.soap.CentralServiceId;
import kg.gov.tunduk.xroad.soap.XRoadClientId;
import kg.gov.tunduk.xroad.soap.XRoadServiceId;
import lombok.val;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class DefaultServiceMetadataManager extends WebServiceGatewaySupport implements ServiceMetadataManager {

    private XRoadClientId currentClientId;

    private URL securityServerUrl;

    private final Unmarshaller unmarshaller;

    {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(
                ListMethodsRequest.class,
                ListMethodsResponse.class,
                XRoadServiceId.class
        );
        this.setUnmarshaller(marshaller);
        this.setMarshaller(marshaller);
    }

    public DefaultServiceMetadataManager() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(
                Client.class, XRoadClientId.class, ClientList.class,
                CentralServiceList.class, CentralServiceId.class
        );
        unmarshaller = jaxbContext.createUnmarshaller();
    }

    private InputStream getHttpContent(URL url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        return httpURLConnection.getInputStream();
    }

    @Override
    public void setCurrentClientId(XRoadClientId clientId) {
        this.currentClientId = clientId;
    }

    @Override
    public void setSecurityServerUrl(URL url) {
        this.securityServerUrl = url;
    }

    private XRoadClientId getCurrentClientId() {
        if (this.currentClientId == null) {
            throw new IllegalStateException("Current client id is not set yet");
        }
        return currentClientId;
    }

    private URL getSecurityServerUrl() {
        if (this.securityServerUrl == null) {
            throw new IllegalStateException("Security server url is not set yet");
        }
        return securityServerUrl;
    }

    @Override
    public ClientList listClients() {
        try {
            URL url = new URL(getSecurityServerUrl(), "/listClients");

            InputStream inputStream = getHttpContent(url);
            return (ClientList) unmarshaller.unmarshal(inputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
            ClientList emptyList = new ClientList();
            emptyList.setMembers(new Client[0]);
            return emptyList;
        }
    }

    @Override
    public CentralServiceList listCentralServices() {
        try {
            URL url = new URL(getSecurityServerUrl(), "/listCentralServices");
            InputStream inputStream = getHttpContent(url);
            return (CentralServiceList) unmarshaller.unmarshal(inputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
            CentralServiceList emptyList = new CentralServiceList();
            emptyList.setServiceIds(new CentralServiceId[0]);
            return emptyList;
        }
    }

    @Override
    public XRoadServiceId[] listMethods(XRoadClientId clientId) {
        val messageId = UUID.randomUUID().toString();
        val userId = "DefaultServiceMetadataManager";

        val listMethodsService = new XRoadServiceId();
        listMethodsService.setInstance(clientId.getInstance());
        listMethodsService.setMemberClass(clientId.getMemberClass());
        listMethodsService.setMemberCode(clientId.getMemberCode());
        listMethodsService.setSubSystemCode(clientId.getSubSystemCode());
        listMethodsService.setServiceCode("listMethods");

        try {
            val response = (ListMethodsResponse) getWebServiceTemplate()
                    .marshalSendAndReceive(getSecurityServerUrl().toString(), new ListMethodsRequest(),
                            new XRoadHeader(getCurrentClientId(), listMethodsService, messageId, userId));

            return response.getServices();
        } catch (Exception ex) {
            ex.printStackTrace();
            return new XRoadServiceId[0];
        }
    }

    @Override
    public String getWsdl(XRoadServiceId serviceId) {
        throw new NotImplementedException();
    }
}
