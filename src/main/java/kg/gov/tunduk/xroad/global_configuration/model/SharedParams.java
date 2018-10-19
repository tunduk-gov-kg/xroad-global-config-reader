package kg.gov.tunduk.xroad.global_configuration.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@XmlRootElement(name = "conf", namespace = "http://x-road.eu/xsd/xroad.xsd")
@XmlAccessorType(XmlAccessType.FIELD)
public class SharedParams {
    @XmlElement(name = "instanceIdentifier")
    private String instanceIdentifier;

    @XmlElement(name = "securityServer")
    private List<SecurityServer> securityServers;

    @XmlElement(name = "member")
    private List<XRoadMember> members;

}
