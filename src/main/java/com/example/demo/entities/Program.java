package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.*;

@Entity
public class Program {
    @Id
    private String code;

    private String name;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("program-versions")
    private List<ProgramVersion> versions = new ArrayList<>();

    public Program() {}
    public Program(String code, String name) { this.code = code; this.name = name; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public List<ProgramVersion> getVersions() { return versions; }
    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setVersions(List<ProgramVersion> versions) {
        this.versions.clear();
        if (versions != null) {
            for (ProgramVersion pv : versions) addVersion(pv);
        }
    }
    public void addVersion(ProgramVersion version) {
        if (version == null) return;
        if (!this.versions.contains(version)) this.versions.add(version);
        version.setProgram(this);
    }
    public void removeVersion(ProgramVersion version) {
        if (version == null) return;
        this.versions.remove(version);
        if (version.getProgram() == this) version.setProgram(null);
    }
}
