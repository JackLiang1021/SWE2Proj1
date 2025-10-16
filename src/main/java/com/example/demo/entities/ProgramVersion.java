package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"program_code","catalogYear"}))
public class ProgramVersion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "program_code")
    @JsonBackReference("program-versions")
    private Program program;

    private int catalogYear;
    private int requiredCredits;

    @OneToMany(mappedBy = "programVersion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("version-groups")
    private List<RequirementGroup> groups = new ArrayList<>();

    public Long getId() { return id; }
    public Program getProgram() { return program; }
    public int getCatalogYear() { return catalogYear; }
    public int getRequiredCredits() { return requiredCredits; }
    public List<RequirementGroup> getGroups() { return groups; }
    public void setId(Long id) { this.id = id; }
    public void setProgram(Program program) {
        if (this.program != null && this.program != program) {
            this.program.getVersions().remove(this);
        }
        this.program = program;
        if (program != null && !program.getVersions().contains(this)) {
            program.getVersions().add(this);
        }
    }
    public void setCatalogYear(int catalogYear) { this.catalogYear = catalogYear; }
    public void setRequiredCredits(int requiredCredits) { this.requiredCredits = requiredCredits; }
    public void setGroups(List<RequirementGroup> groups) {
        this.groups.clear();
        if (groups != null) {
            for (RequirementGroup g : groups) addGroup(g);
        }
    }
    public void addGroup(RequirementGroup group) {
        if (group == null) return;
        if (!this.groups.contains(group)) this.groups.add(group);
        group.setProgramVersion(this);
    }
    public void removeGroup(RequirementGroup group) {
        if (group == null) return;
        this.groups.remove(group);
        if (group.getProgramVersion() == this) group.setProgramVersion(null);
    }
}
