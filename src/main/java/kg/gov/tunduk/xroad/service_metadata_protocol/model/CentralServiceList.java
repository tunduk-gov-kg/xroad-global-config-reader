package kg.gov.tunduk.xroad.service_metadata_protocol.model;

import kg.gov.tunduk.xroad.soap.CentralServiceId;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "centralServiceList", namespace = "http://x-road.eu/xsd/xroad.xsd")
public class CentralServiceList {

    @XmlElement(name = "centralService", namespace = "http://x-road.eu/xsd/xroad.xsd")
    private CentralServiceId[] serviceIds;
}
