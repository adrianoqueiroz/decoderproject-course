package com.ead.course.controllers;


import com.ead.course.dto.LessonDto;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;
    private final ModuleService moduleService;

    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Object> createLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                               @RequestBody @Valid LessonDto lessonDto) {

        Optional<ModuleModel> moduleModel = moduleService.findById(moduleId);

        if(moduleModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found");
        }

        var lessonModel = new LessonModel();

        BeanUtils.copyProperties(lessonDto, lessonModel);
        lessonModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        lessonModel.setModule(moduleModel.get());

        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.save(lessonModel));
    }

    @DeleteMapping
    @RequestMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> deleteLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                               @PathVariable(value = "lessonId") UUID lessonId) {

        Optional<ModuleModel> moduleModel = moduleService.findById(moduleId);

        if(moduleModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found");
        }

        Optional<LessonModel> lessonModel = lessonService.findLessonIntoModule(moduleId, lessonId);

        if(lessonModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found");
        }

        lessonService.delete(lessonModel.get());

        return ResponseEntity.status(HttpStatus.OK).body("Lesson deleted successfully");
    }

    @PutMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> updateLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                               @PathVariable(value = "lessonId") UUID lessonId,
                                               @RequestBody @Valid LessonDto lessonDto) {

        Optional<LessonModel> lessonModel = lessonService.findLessonIntoModule(moduleId, lessonId);

        if(lessonModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found");
        }

        BeanUtils.copyProperties(lessonDto, lessonModel.get());

        return ResponseEntity.status(HttpStatus.OK).body(lessonService.save(lessonModel.get()));
    }

    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<List<LessonModel>> findAllLessonsIntoModule(@PathVariable(value = "moduleId") UUID moduleId) {
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.findAllLessonsIntoModule(moduleId));
    }

    @GetMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> findLessonIntoModule(@PathVariable(value = "moduleId") UUID moduleId,
                                                       @PathVariable(value = "lessonId") UUID lessonId) {

        Optional<LessonModel> lessonModel = lessonService.findLessonIntoModule(moduleId, lessonId);

        if(lessonModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found into module");
        }

        return ResponseEntity.status(HttpStatus.OK).body(lessonModel.get());
    }

}
