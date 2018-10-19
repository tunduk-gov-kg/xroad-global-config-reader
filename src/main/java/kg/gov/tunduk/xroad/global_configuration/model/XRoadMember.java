package kg.gov.tunduk.xroad.global_configuration.model;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class XRoadMember {
    @XmlAttribute(name = "id")
    private String id;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "memberCode")
    private String memberCode;

    @XmlElement(name = "memberClass")
    private MemberClass memberClass;

    @XmlElement(name = "subsystem")
    private List<SubSystem> subSystems = new ArrayList<>();
}
