package kg.gov.tunduk.xroad.global_configuration.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MemberClass {

    @XmlElement(name = "code")
    private String code;

    @XmlElement(name = "description")
    private String description;
}
