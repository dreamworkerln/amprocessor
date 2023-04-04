package ru.kvanttelecom.tv.amprocessor.core.db.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.core.annotation.Order;
import ru.dreamworkerln.spring.db.entities.AbstractEntity;
import ru.dreamworkerln.spring.utils.common.configurations.annotations.Default;
import ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails.CameraGroup;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "camera_details"
//    indexes = { @Index(name = "name_index", columnList = "name", unique = true)}
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CameraDetailsEntity extends AbstractEntity {


    @Column(unique = true)
    @Order(1)
    private String name;           // camera name

    private String  addressExt;     // дополнительный адрес
    private String  odinAss;        // Qualifier в 1C
    private String  bugsUser;       // Qualifier в Bugs

    private String  owners;         // кому реально принадлежит камера
    private String  commentExt;     // дополнительный комментарий
    private String  accessDetails;  // как получить доступ к камере
    private String  networkDetails; // сетевые особенности
    private Integer vlan;          // vlan id

    //    // participans, include owner, executor and others teammates
//    @ManyToMany
//    @JoinTable(name = "member",
//        joinColumns = @JoinColumn(name = "task_id"),
//        inverseJoinColumns = @JoinColumn(name = "user_id"))
//    private Set<User> members = new HashSet<>();


//    @ElementCollection(targetClass = CameraGroup.class)
//    @JoinTable(name = "cam_groups", joinColumns = @JoinColumn(name = "cameraId"))
//    @Column(name = "group", nullable = false)
//    //@Enumerated(EnumType.STRING)
//    private Set<CameraGroup> types;

    @NotNull
    //@NotEmpty
    @ElementCollection(targetClass = CameraGroup.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "camera_group", joinColumns = @JoinColumn(name = "camera_id"))
    @Column(name = "groups")
    private Set<CameraGroup> groups = new HashSet<>();

//    public static CameraDetailsEntity createEmpty(String name) {
//        return new CameraDetailsEntity(name, null ,null, null, null, null, null);
//    }

//    public static CameraDetailsEntity createEmpty(String name, Set<CameraGroup> groups) {
//        CameraDetailsEntity result = new CameraDetailsEntity(name, null ,null, null, null, null, null);
//        result.setGroups(groups);
//        return result;
//    }

    public static CameraDetailsEntity createEmpty(String name) {
        return new CameraDetailsEntity(name, null ,null, null, null, null, null);
    }




    // This constructor is used by object mapper(jackson databind)
    protected CameraDetailsEntity() {}

    @Default // tell mapstruct to use this constructor
    public CameraDetailsEntity(String name, String addressExt, String odinAss, String bugsUser,
                               String commentExt, String accessDetails, String networkDetails) {
        this.name = name;
        this.addressExt = addressExt;
        this.odinAss = odinAss;
        this.bugsUser = bugsUser;
        this.commentExt = commentExt;
        this.accessDetails = accessDetails;
        this.networkDetails = networkDetails;
    }


}
