package kg.gov.tunduk.xroad.global_configuration.model;

import lombok.Data;

import javax.xml.bind.annotation.*;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SubSystem {
    @XmlAttribute(name = "id")
    private String id;

    @XmlElement(name = "subsystemCode")
    private String subSystemCode;
}

