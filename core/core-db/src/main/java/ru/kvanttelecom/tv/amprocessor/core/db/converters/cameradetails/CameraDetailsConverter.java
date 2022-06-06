package ru.kvanttelecom.tv.amprocessor.core.db.converters.cameradetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.dreamworkerln.spring.db.converters.AbstractConverter;
import ru.kvanttelecom.tv.amprocessor.core.db.entities.CameraDetailsEntity;
import ru.kvanttelecom.tv.amprocessor.core.db.specifications.cameradetails.CameraDetailsSpecBuilder;
import ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails.CameraDetails;
import ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails.CameraDetailsSpec;

import java.util.List;

@Component
public class CameraDetailsConverter extends AbstractConverter<CameraDetailsEntity, CameraDetails, CameraDetailsSpec> {



    @Autowired
    public CameraDetailsConverter(CameraDetailsMapper propMapper,
                                  CameraDetailsSpecBuilder propSpecBuilder) {

        this.entityMapper = propMapper;
        this.specBuilder = propSpecBuilder;

        //this.entityClass = CameraPropertiesEntity.class;
        //this.dtoClass = CameraProperties.class;
        //this.specClass = CameraPropertiesSpec.class;
    }


    @Override
    protected void validate(CameraDetailsEntity entity) {
        super.validate(entity);

        // ... custom validation
    }

// ------------------------------------------------

    @Override
    public CameraDetails toDto(CameraDetailsEntity entity) {
        return super.toDto(entity);
    }

    @Override
    public List<CameraDetails> toDtoList(Iterable<CameraDetailsEntity> entityList) {
        return super.toDtoList(entityList);
    }
}

