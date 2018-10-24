package kg.gov.tunduk.xroad.service_metadata_protocol.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "clientList", namespace = "http://x-road.eu/xsd/xroad.xsd")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClientList {
    @XmlElement(name = "member", namespace = "http://x-road.eu/xsd/xroad.xsd")
    private Client[] members;
}
