package ru.kvanttelecom.tv.amprocessor.core.db.services;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dreamworkerln.spring.db.services.BaseRepoAccessService;
import ru.kvanttelecom.tv.amprocessor.core.db.entities.CameraDetailsEntity;
import ru.kvanttelecom.tv.amprocessor.core.db.repositories.CameraDetailsRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@Transactional
@Slf4j
public class CameraDetailsEntityService extends BaseRepoAccessService<CameraDetailsEntity> {

    private final CameraDetailsRepository detailsRepository;

    @Autowired
    protected CameraDetailsEntityService(CameraDetailsRepository detailsRepository) {
        super(detailsRepository);

        this.detailsRepository = detailsRepository;
    }

    // ------------------------------------------------------

    public Optional<CameraDetailsEntity> findByName(String name) {
        return detailsRepository.findByName(name); 
    }

//    public CameraDetailsEntity save(CameraDetailsEntity details) {
//        return detailsRepository.save(details);
//    }

//    public List<CameraDetailsEntity> saveList(Iterable<CameraDetailsEntity> list) {
//        return detailsRepository.saveAll(list);
//    }

    public void delete(String name) {
        detailsRepository.deleteByName(name);
    }

//    public void deleteList(Iterable<CameraDetailsEntity> list) {
//        List<Long> ids =  StreamSupport.stream(list.spliterator(),false)
//            .map(CameraDetailsEntity::getId).collect(Collectors.toList());
//        detailsRepository.deleteAllById(ids);
//    }

    public List<CameraDetailsEntity> getList() {
        return detailsRepository.findAll();
    }

    public Map<String, CameraDetailsEntity> getMap() {
        return Maps.uniqueIndex(getList(), CameraDetailsEntity::getName);
    }

//    public long count() {
//        return detailsRepository.count();
//    }

//    public boolean contains(String name) {
//        return detailsRepository.findByName(name).isPresent();
//    }

}

