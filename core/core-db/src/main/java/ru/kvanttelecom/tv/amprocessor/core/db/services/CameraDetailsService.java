package ru.kvanttelecom.tv.amprocessor.core.db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kvanttelecom.tv.amprocessor.core.db.converters.cameradetails.CameraDetailsConverter;
import ru.kvanttelecom.tv.amprocessor.core.db.entities.CameraDetailsEntity;
import ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails.CameraDetails;

import java.util.Optional;

@Service
public class CameraDetailsService {

    @Autowired
    CameraDetailsEntityService entityService;

    @Autowired
    CameraDetailsConverter converter;

    public CameraDetails get(String name) {
        return converter.toDto(entityService.findByName(name).orElse(null));
    }


    public CameraDetails save(CameraDetails details) {
        // assign id (if exists)
        entityService.findByName(details.getName()).ifPresent(e -> details.setId(e.getId()));
        return converter.toDto(entityService.save(converter.toEntity(details)));
    }



}
