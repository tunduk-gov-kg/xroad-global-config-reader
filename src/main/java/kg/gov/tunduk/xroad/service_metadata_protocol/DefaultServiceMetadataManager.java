package kg.gov.tunduk.xroad.service_metadata_protocol;

import kg.gov.tunduk.xroad.XRoadClientInterceptor;
import kg.gov.tunduk.xroad.XRoadHeader;
import kg.gov.tunduk.xroad.global_configuration.model.SharedParams;
import kg.gov.tunduk.xroad.service_metadata_protocol.model.*;
import kg.gov.tunduk.xroad.soap.XRoadClientId;
import kg.gov.tunduk.xroad.soap.XRoadServiceId;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.Assert;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.mime.Attachment;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DefaultServiceMetadataManager extends WebServiceGatewaySupport implements ServiceMetadataManager {

    private XRoadClientId currentClientId;

    private URL securityServerUrl;

    private final Unmarshaller unmarshaller;

    public DefaultServiceMetadataManager() throws JAXBException {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan(
                "kg.gov.tunduk.xroad.service_metadata_protocol.model",
                "kg.gov.tunduk.xroad.global_configuration.model"
        );
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
        Assert.isTrue(clientId != null, "clientId cannot be null");
        this.currentClientId = clientId;
    }

    @Override
    public void setSecurityServerUrl(URL url) {
        Assert.isTrue(url != null, "securityServerUrl cannot be null");
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
    public ClientList listClients() throws IOException, JAXBException {
        URL url = new URL(getSecurityServerUrl(), "/listClients");
        InputStream inputStream = getHttpContent(url);
        return (ClientList) unmarshaller.unmarshal(inputStream);
    }

    @Override
    public CentralServiceList listCentralServices() throws IOException, JAXBException {
        URL url = new URL(getSecurityServerUrl(), "/listCentralServices");
        InputStream inputStream = getHttpContent(url);
        return (CentralServiceList) unmarshaller.unmarshal(inputStream);
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

        /*
         * Handle soap fault
         * */
        setInterceptors(new ClientInterceptor[]{new XRoadClientInterceptor()});

        val response = (ListMethodsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(getSecurityServerUrl().toString(), new ListMethodsRequest(),
                        new XRoadHeader(getCurrentClientId(), listMethodsService, null, messageId, userId));
        return response.getServices();
    }

    @Override
    public String getWsdl(XRoadServiceId serviceId) throws IOException {
        val messageId = UUID.randomUUID().toString();
        val userId = "SERVICE_METADATA_MANAGER";

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

        XRoadClientInterceptor attachmentInterceptor = new XRoadClientInterceptor();
        setInterceptors(new ClientInterceptor[]{attachmentInterceptor});
        getWebServiceTemplate().marshalSendAndReceive(
                getSecurityServerUrl().toString(), getWsdlRequest,
                new XRoadHeader(getCurrentClientId(), getWsdlService, null, messageId, userId));
        Iterator<Attachment> attachments = attachmentInterceptor.getAttachments();

        Assert.isTrue(attachments.hasNext(), "no wsdl file found");

        InputStream inputStream = attachments.next().getInputStream();

        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, "UTF-8");
        return writer.toString();
    }

    @Override
    public SharedParams verificationConf() throws IOException, JAXBException {
        URL verificationConf = new URL(getSecurityServerUrl(), "verificationconf");
        InputStream httpContent = getHttpContent(verificationConf);
        try (ZipInputStream zipInputStream = new ZipInputStream(httpContent)) {
            ZipEntry zipEntry;
            Map<String, byte[]> zipContent = new HashMap<>();
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                for (int c = zipInputStream.read(); c != -1; c = zipInputStream.read()) {
                    outputStream.write(c);
                }
                zipInputStream.closeEntry();
                zipContent.put(zipEntry.getName(), outputStream.toByteArray());
                outputStream.close();
            }
            byte[] bytes = zipContent.get("verificationconf/instance-identifier");
            String instanceIdentifier = new String(bytes);
            byte[] sharedParamsBytes = zipContent.get(String.format("verificationconf/%s/shared-params.xml", instanceIdentifier));
            return (SharedParams) unmarshaller.unmarshal(new ByteArrayInputStream(sharedParamsBytes));
        }
    }

}
