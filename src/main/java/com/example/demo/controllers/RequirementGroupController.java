package com.example.demo.controllers;

import com.example.demo.entities.RequirementGroup;
import com.example.demo.services.RequirementGroupService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/programs/{code}/versions/{year}/groups")
public class RequirementGroupController {
    private final RequirementGroupService svc;

    public RequirementGroupController(RequirementGroupService svc) { this.svc = svc; }

    @GetMapping
    public List<RequirementGroup> list(@PathVariable String code, @PathVariable int year) {
        return svc.list(code, year);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequirementGroup create(@PathVariable String code, @PathVariable int year,
                                   @RequestBody RequirementGroup group) {
        return svc.create(code, year, group);
    }

    @GetMapping("/{groupId}")
    public RequirementGroup get(@PathVariable String code, @PathVariable int year, @PathVariable Long groupId) {
        return svc.get(code, year, groupId);
    }

    @PutMapping("/{groupId}")
    public RequirementGroup update(@PathVariable String code, @PathVariable int year,
                                   @PathVariable Long groupId, @RequestBody RequirementGroup patch) {
        return svc.update(code, year, groupId, patch);
    }

    @DeleteMapping("/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String code, @PathVariable int year, @PathVariable Long groupId) {
        svc.delete(code, year, groupId);
    }
}

