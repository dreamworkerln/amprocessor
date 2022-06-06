package ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import ru.dreamworkerln.spring.utils.common.annotations.Default;
import ru.dreamworkerln.spring.utils.common.dto.AbstractDto;

import java.util.HashSet;
import java.util.Set;

/**
 * Additional "stream" properties about camera details:
 * camera detailed location, real camera owner, owner id in billing system, odin-ass e.t.c.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CameraDetails extends AbstractDto {

    //public static CameraDetails NotInitializedCameraDetails = new CameraDetails("NOT_INITIALIZED_CAMERA_DETAILS");

    @JsonProperty(index = 0)
    private String  name;           // имя камеры

    private String  addressExt;     // дополнительный адрес
    private String  odinAss;        // Qualifier в 1C
    private String  bugsUser;       // Qualifier в Bugs

    private String  owners;         // кому реально принадлежит камера
    private String  accessDetails;  // как получить доступ к камере
    private String  commentExt;     // дополнительный комментарий
    private String  networkDetails; // сетевые особенности
    private Integer vlan;           // vlan id

    @Setter(AccessLevel.PROTECTED)
    private Set<CameraGroup> groups = new HashSet<>(); // группы, в которые входит камера

//    public static CameraDetails createEmpty(String name) {
//        return new CameraDetails(name, new HashSet<>());
//    }

    public void addGroup(CameraGroup group) {
        groups.add(group);
    }
    public void removeGroup(CameraGroup group) {
        groups.remove(group);
    }

    //Objectmapper require this
    protected CameraDetails(){ }

    /**
     * Create CameraDetails
     * @param name camera name
     */
    public CameraDetails(String name) {
        this.name = name;
        this.groups = new HashSet<>();
    }

    /**
     * Create CameraDetails
     * @param name camera name
     * @param groups assignable group set
     */
    @Default
    public CameraDetails(String name, Set<CameraGroup> groups) {
        this.name = name;
        this.groups.addAll(groups);

    }
}




