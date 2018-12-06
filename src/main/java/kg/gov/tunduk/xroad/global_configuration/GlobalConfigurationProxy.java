package kg.gov.tunduk.xroad.global_configuration;

import kg.gov.tunduk.xroad.global_configuration.model.SharedParams;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URL;

public interface GlobalConfigurationProxy {
    SharedParams getSharedParams(URL centralServerUrl) throws IOException, JAXBException;
}
