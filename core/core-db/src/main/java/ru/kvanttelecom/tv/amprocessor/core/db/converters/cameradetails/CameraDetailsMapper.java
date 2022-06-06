package ru.kvanttelecom.tv.amprocessor.core.db.converters.cameradetails;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.dreamworkerln.spring.db.converters.AbstractMapper;
import ru.kvanttelecom.tv.amprocessor.core.db.entities.CameraDetailsEntity;
import ru.kvanttelecom.tv.amprocessor.core.db.services.CameraDetailsEntityService;
import ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails.CameraDetails;

import javax.annotation.PostConstruct;

@Mapper(config = AbstractMapper.class)
public abstract class CameraDetailsMapper extends AbstractMapper<CameraDetailsEntity, CameraDetails> {


    @Autowired
    private CameraDetailsEntityService detailsService;

//    @Autowired
//    public CameraDetailsMapper(EntityFindById<CameraDetailsEntity> detailsService) {
//        super(findById);
//    }

    //@Mapping(target = "comment", ignore = true)
    //public abstract CameraDetailsEntity toEntity(CameraDetails details);

    

    // Mapstruct не осиливает кодобредогенерацию внедрения в конструктор -
    //  т.к. внедряемые типы разные, а привести к базовому внедряемый бин он не догадывается
    // поэтому присваиваем через init()
    @PostConstruct
    private void init() {
        super.baseRepoAccessService = detailsService;
    }


//    @Override
//    @AfterMapping
//    public CameraDetailsEntity afterMapping(CameraDetails source,
//                                            @MappingTarget CameraDetailsEntity target) {
//        return super.afetMapping(source, target);
//    }

}

