package kg.gov.tunduk.xroad.service_metadata_protocol.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "listMethods", namespace = "http://x-road.eu/xsd/xroad.xsd")
public class ListMethodsRequest {
}
