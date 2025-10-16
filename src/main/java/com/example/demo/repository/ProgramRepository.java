package com.example.demo.repository;

import com.example.demo.entities.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProgramRepository extends JpaRepository<Program, String> {
    Program findByCode(String code);
    Program findByName(String name);
    List<Program> findAll();
}

