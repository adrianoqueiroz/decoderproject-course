package com.ead.course.controllers;

import com.ead.course.dto.ModuleDto;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
public class ModuleController {

    private final ModuleService moduleService;
    private final CourseService courseService;

    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<Object> create(@PathVariable(value = "courseId") UUID courseId,
                                         @RequestBody @Valid ModuleDto moduleDto) {

        Optional<CourseModel> optionalCourseModel = courseService.findById(courseId);

        if(optionalCourseModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }

        var moduleModel = new ModuleModel();
        BeanUtils.copyProperties(moduleDto, moduleModel);
        moduleModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        moduleModel.setCourse(optionalCourseModel.get());

        return ResponseEntity.status(HttpStatus.CREATED).body(moduleService.save(moduleModel));
    }

    @DeleteMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> delete(@PathVariable UUID courseId, @PathVariable UUID moduleId) {

        Optional<ModuleModel> optionalModuleModel = moduleService.findModuleIntoCourse(courseId, moduleId);

        if(optionalModuleModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found into course");
        }

        moduleService.delete(optionalModuleModel.get());
        return ResponseEntity.status(HttpStatus.OK).body("Module deleted successfully");
    }

    @PutMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> update(@PathVariable UUID courseId,
                                         @PathVariable UUID moduleId,
                                         @RequestBody @Valid ModuleDto moduleDto) {

        Optional<ModuleModel> optionalModuleModel = moduleService.findModuleIntoCourse(courseId, moduleId);

        if(optionalModuleModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found into course");
        }

        var moduleModel = optionalModuleModel.get();

        BeanUtils.copyProperties(moduleDto, moduleModel);

        return ResponseEntity.status(HttpStatus.OK).body(moduleService.save(moduleModel));
    }

    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<List<ModuleModel>> findModulesByCourse(@PathVariable UUID courseId,
                                                      SpecificationTemplate.ModuleSpec spec,
                                                      @PageableDefault(page = 0, size = 10, sort = "moduleId", direction = Sort.Direction.ASC) Pageable pageable) {
        List<ModuleModel> optionalModuleModel = moduleService.findAllByCourse(courseId);
        if(optionalModuleModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(optionalModuleModel);
        }

        return ResponseEntity.status(HttpStatus.OK).body(optionalModuleModel);
    }

    @GetMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> findModuleIntoCourse(@PathVariable UUID courseId, @PathVariable UUID moduleId) {
        Optional<ModuleModel> optionalModuleModel = moduleService.findModuleIntoCourse(courseId, moduleId);
        if(optionalModuleModel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(optionalModuleModel.get());
    }

}
