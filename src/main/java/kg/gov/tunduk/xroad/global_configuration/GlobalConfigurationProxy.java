package kg.gov.tunduk.xroad.global_configuration;

import kg.gov.tunduk.xroad.global_configuration.model.SharedParams;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public interface GlobalConfigurationProxy {
    SharedParams getSharedParams(String centralServerIpAddress) throws IOException, JAXBException;
}
