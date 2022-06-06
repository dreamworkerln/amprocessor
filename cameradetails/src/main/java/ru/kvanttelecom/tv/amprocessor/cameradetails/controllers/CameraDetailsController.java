package ru.kvanttelecom.tv.amprocessor.cameradetails.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;
import ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails.CameraDetails;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.cameras.CameraService;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@Slf4j
public class CameraDetailsController {


    @Autowired
    private CameraService cameraService;


    /**
     * Get Camera details by camera name
     * @param name - camera (and stream) name
     * @return
     */
    @GetMapping("/camera_details/{name}")
    public ResponseEntity<CameraDetails> get(@PathVariable String name) {

        log.debug("Camera name: {}", name);
        Camera camera = cameraService.get(name);
        if(camera == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Camera not found");
        }
        CameraDetails details = camera.getDetails();
        log.debug("CameraDetails: {}", details);

        return ResponseEntity.ok(details);
    }

    @GetMapping("/camera_details")
    public ResponseEntity<List<CameraDetails>> findAll() {
        List<CameraDetails> detList = cameraService.getList().stream().map(Camera::getDetails).collect(Collectors.toList());
        return ResponseEntity.ok(detList);
    }


    @PostMapping("/camera_details")
    public void post(@RequestBody CameraDetails details) {

        log.debug("post CameraDetails: {}", details);

        Camera camera = cameraService.get(details.getName());
        if(camera == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Camera not found");
        }

        camera.setDetails(details);

        // save camera and details to cache
        cameraService.save(camera);
        cameraService.flushCameras(Set.of(camera));
    }





//    /**
//     * Flush non persisted
//     * @param names
//     */
//    @PostMapping("/camera_details/flush")
//    public void flush(@RequestBody List<String> names) {
//
//        log.debug("post CameraDetails: {}", names);
//
//        names.forEach(name -> {
//            Camera camera = cameraService.get(name);
//
//            if(camera != null) {
//
//
//            }
//            else {
//                log.debug("Camera : {} not found", name);
//            }
//        });
//
//        Camera camera = cameraService.get(details.getName());
//        if(camera == null) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Camera not found");
//        }
//
//        camera.setDetails(details);
//        cameraService.save(camera);
//    }


}

//        }
//        catch(IllegalArgumentException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
//        }
//
//        catch (Exception skip) {
//            log.error("Internal server error:", skip);
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
//        }


//ResponseEntity<ApiError> result = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//StreamExtended ext2 = new StreamExtended("adress", "ODIN_ASS", "BUGS_USER");
//throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

//ResponseEntity<String> result = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();


//result =  ResponseEntity.ok(details);
//result = details != null ? ResponseEntity.ok(details) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();

//ResponseEntity<CameraDetails> result;