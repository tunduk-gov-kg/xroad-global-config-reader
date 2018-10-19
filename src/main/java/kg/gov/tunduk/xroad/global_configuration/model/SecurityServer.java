package kg.gov.tunduk.xroad.global_configuration.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SecurityServer {
    @XmlElement(name = "owner")
    private String owner;

    @XmlElement(name = "serverCode")
    private String serverCode;

    @XmlElement(name = "address")
    private String address;

    @XmlElement(name = "client")
    private List<String> subSystemIdentifiers = new ArrayList<>();
}
