// src/main/java/com/example/demo/services/ProgramService.java
package com.example.demo.services;

import com.example.demo.entities.Program;
import com.example.demo.entities.ProgramVersion;
import com.example.demo.repository.ProgramRepository;
import com.example.demo.repository.ProgramVersionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProgramService {

    private final ProgramRepository programRepository;
    private final ProgramVersionRepository programVersionRepository;

    public ProgramService(ProgramRepository programRepository,
                          ProgramVersionRepository programVersionRepository) {
        this.programRepository = programRepository;
        this.programVersionRepository = programVersionRepository;
    }

    public List<Program> getAll() {
        return programRepository.findAll();
    }

    public Program getByCode(String code) {
        return programRepository.findById(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Program not found"));
    }

    public Program getByName(String name) {
        Program p = programRepository.findByName(name);
        if (p == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Program not found");
        }
        return p;
    }

    @Transactional
    public Program create(Program program) {
        if (program == null || program.getCode() == null || program.getCode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Program code is required");
        }
        if (programRepository.existsById(program.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Program code already exists");
        }
        if (program.getVersions() != null) {
            program.getVersions().forEach(v -> v.setProgram(program));
        }
        return programRepository.save(program);
    }

    @Transactional
    public Program update(String code, Program patch) {
        Program existing = getByCode(code);
        if (patch.getName() != null) existing.setName(patch.getName());
        return programRepository.save(existing);
    }

    @Transactional
    public void delete(String code) {
        Program existing = getByCode(code);
        programRepository.delete(existing);
    }

    public List<ProgramVersion> getVersions(String programCode) {
        getByCode(programCode);
        return programVersionRepository.findByProgram_Code(programCode);
    }

    public ProgramVersion getVersion(String programCode, int catalogYear) {
        return programVersionRepository.findByProgram_CodeAndCatalogYear(programCode, catalogYear)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProgramVersion not found"));
    }

    @Transactional
    public ProgramVersion addVersion(String programCode, ProgramVersion version) {
        if (version == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Version payload is required");
        }
        if (version.getCatalogYear() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "catalogYear is required");
        }
        if (programVersionRepository.existsByProgram_CodeAndCatalogYear(programCode, version.getCatalogYear())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Version for that catalogYear already exists");
        }
        Program program = getByCode(programCode);
        version.setProgram(program);
        return programVersionRepository.save(version);
    }

    @Transactional
    public ProgramVersion updateVersion(String programCode, int catalogYear, ProgramVersion patch) {
        ProgramVersion existing = getVersion(programCode, catalogYear);

        if (patch.getCatalogYear() != 0 && patch.getCatalogYear() != existing.getCatalogYear()) {
            if (programVersionRepository.existsByProgram_CodeAndCatalogYear(programCode, patch.getCatalogYear())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Target catalogYear already exists");
            }
            existing.setCatalogYear(patch.getCatalogYear());
        }

        if (patch.getRequiredCredits() != 0) {
            existing.setRequiredCredits(patch.getRequiredCredits());
        }

        return programVersionRepository.save(existing);
    }

    @Transactional
    public void removeVersion(String programCode, int catalogYear) {
        ProgramVersion existing = getVersion(programCode, catalogYear);
        programVersionRepository.delete(existing);
    }
}
