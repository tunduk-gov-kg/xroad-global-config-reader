package kg.gov.tunduk.xroad.service_metadata_protocol;

import kg.gov.tunduk.xroad.XRoadClientInterceptor;
import kg.gov.tunduk.xroad.XRoadHeader;
import kg.gov.tunduk.xroad.service_metadata_protocol.model.*;
import kg.gov.tunduk.xroad.soap.XRoadCentralServiceId;
import kg.gov.tunduk.xroad.soap.XRoadClientId;
import kg.gov.tunduk.xroad.soap.XRoadServiceId;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.mime.Attachment;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.UUID;

public class DefaultServiceMetadataManager extends WebServiceGatewaySupport implements ServiceMetadataManager {

    private XRoadClientId currentClientId;

    private URL securityServerUrl;

    private final Unmarshaller unmarshaller;

    public DefaultServiceMetadataManager() throws JAXBException {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("kg.gov.tunduk.xroad.service_metadata_protocol.model");
        this.setUnmarshaller(marshaller);
        this.setMarshaller(marshaller);
        this.unmarshaller = marshaller.getJaxbContext().createUnmarshaller();
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
            emptyList.setServiceIds(new XRoadCentralServiceId[0]);
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
    public String getWsdl(XRoadServiceId serviceId) throws IOException {
        val messageId = UUID.randomUUID().toString();
        val userId = "DefaultServiceMetadataManager";

        val getWsdlService = new XRoadServiceId();
        getWsdlService.setInstance(serviceId.getInstance());
        getWsdlService.setMemberClass(serviceId.getMemberClass());
        getWsdlService.setMemberCode(serviceId.getMemberCode());
        getWsdlService.setSubSystemCode(serviceId.getSubSystemCode());
        getWsdlService.setServiceCode("getWsdl");
        getWsdlService.setServiceVersion("v1");

        GetWsdlRequest getWsdlRequest = new GetWsdlRequest();
        getWsdlRequest.setServiceCode(serviceId.getServiceCode());
        getWsdlRequest.setServiceVersion(serviceId.getServiceVersion());

        try {
            XRoadClientInterceptor attachmentInterceptor = new XRoadClientInterceptor();
            setInterceptors(new ClientInterceptor[]{attachmentInterceptor});
            getWebServiceTemplate().marshalSendAndReceive(
                    getSecurityServerUrl().toString(), getWsdlRequest,
                    new XRoadHeader(getCurrentClientId(), getWsdlService, messageId, userId));
            Iterator<Attachment> attachments = attachmentInterceptor.getAttachments();
            InputStream inputStream = attachments.next().getInputStream();

            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer, "UTF-8");
            return writer.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

}
