package kg.gov.tunduk.xroad.service_metadata_protocol.model;

import kg.gov.tunduk.xroad.soap.XRoadClientId;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "member", namespace = "http://x-road.eu/xsd/xroad.xsd")
@XmlAccessorType(XmlAccessType.FIELD)
public class Client {

    @XmlElement(name = "id", namespace = "http://x-road.eu/xsd/xroad.xsd")
    private XRoadClientId id;

    @XmlElement(name = "name", namespace = "http://x-road.eu/xsd/xroad.xsd")
    private String name;
}
