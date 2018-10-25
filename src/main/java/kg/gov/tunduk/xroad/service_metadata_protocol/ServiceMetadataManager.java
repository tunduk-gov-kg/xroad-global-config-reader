package kg.gov.tunduk.xroad.service_metadata_protocol;

import kg.gov.tunduk.xroad.service_metadata_protocol.model.CentralServiceList;
import kg.gov.tunduk.xroad.service_metadata_protocol.model.ClientList;
import kg.gov.tunduk.xroad.soap.XRoadClientId;
import kg.gov.tunduk.xroad.soap.XRoadServiceId;

import java.io.IOException;
import java.net.URL;

public interface ServiceMetadataManager {

    void setCurrentClientId(XRoadClientId clientId);

    void setSecurityServerUrl(URL url);

    ClientList listClients();

    CentralServiceList listCentralServices();

    XRoadServiceId[] listMethods(XRoadClientId clientId);

    String getWsdl(XRoadServiceId serviceId) throws IOException;
}
