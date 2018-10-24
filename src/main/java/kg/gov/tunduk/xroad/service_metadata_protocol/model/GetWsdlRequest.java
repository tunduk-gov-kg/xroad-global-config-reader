package kg.gov.tunduk.xroad.service_metadata_protocol.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "getWsdl", namespace = "http://x-road.eu/xsd/xroad.xsd")
public class GetWsdlRequest {

    @XmlElement(name = "serviceCode", namespace = "http://x-road.eu/xsd/xroad.xsd")
    private String serviceCode;

    @XmlElement(name = "serviceVersion", namespace = "http://x-road.eu/xsd/xroad.xsd")
    private String serviceVersion;
}