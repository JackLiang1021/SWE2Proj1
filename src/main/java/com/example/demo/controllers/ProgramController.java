// src/main/java/com/example/demo/controllers/ProgramController.java
package com.example.demo.controllers;

import com.example.demo.entities.Program;
import com.example.demo.entities.ProgramVersion;
import com.example.demo.services.ProgramService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/programs")
@CrossOrigin(origins = "*")
public class ProgramController {

    private final ProgramService programService;

    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    @GetMapping
    public List<Program> getAllPrograms() {
        return programService.getAll();
    }

    @GetMapping("/{code}")
    public Program getProgramByCode(@PathVariable String code) {
        return programService.getByCode(code);
    }

    @GetMapping("/name/{name}")
    public Program getProgramByName(@PathVariable String name) {
        return programService.getByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Program createProgram(@RequestBody Program program) {
        return programService.create(program);
    }

    @PutMapping("/{code}")
    public Program updateProgram(@PathVariable String code, @RequestBody Program patch) {
        return programService.update(code, patch);
    }

    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProgram(@PathVariable String code) {
        programService.delete(code);
    }

    @GetMapping("/{code}/versions")
    public List<ProgramVersion> getVersions(@PathVariable String code) {
        return programService.getVersions(code);
    }

    @GetMapping("/{code}/versions/{year}")
    public ProgramVersion getVersion(@PathVariable String code, @PathVariable int year) {
        return programService.getVersion(code, year);
    }

    @PostMapping("/{code}/versions")
    @ResponseStatus(HttpStatus.CREATED)
    public ProgramVersion addVersion(@PathVariable String code, @RequestBody ProgramVersion version) {
        return programService.addVersion(code, version);
    }

    @PutMapping("/{code}/versions/{year}")
    public ProgramVersion updateVersion(@PathVariable String code,
                                        @PathVariable int year,
                                        @RequestBody ProgramVersion patch) {
        return programService.updateVersion(code, year, patch);
    }

    @DeleteMapping("/{code}/versions/{year}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVersion(@PathVariable String code, @PathVariable int year) {
        programService.removeVersion(code, year);
    }
}
