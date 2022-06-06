package ru.kvanttelecom.tv.amprocessor.core.db.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.kvanttelecom.tv.amprocessor.core.db.entities.CameraDetailsEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Slf4j
class CameraDetailsServiceTest {

    @Autowired
    private CameraDetailsEntityService detailsService;


    @BeforeAll
    static void beforeAll(@Autowired CameraDetailsEntityService detailsService) {

        CameraDetailsEntity cp = new CameraDetailsEntity(
            "Вася",
            "Воронежб 57б 64, 3 угол, 2 этаж",
            "ОДИНН АССССС",
            "ST ST ST ST 003", "CommentExt",
             "там-там-там взять ключи-ключи-ключи",
            "vlan 2");

        detailsService.save(cp);
    }


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }



    @Test
    void findByName() {                    //new ArrayList<>(cameraCache.getMap().values());

        Optional<CameraDetailsEntity> det = detailsService.findByName("вася");
        assertThat(det).isEmpty();

        det = detailsService.findByName("Вася");

        assertThat(det).isPresent();

        assertThat(det).get().extracting(CameraDetailsEntity::getName, CameraDetailsEntity::getOdinAss)
            .containsExactly("Вася", "ОДИНН АССССС");

    }
}