package com.group.Timetable.Generator.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group.Timetable.Generator.Repository.*;
import com.group.Timetable.Generator.dto.TeacherSubjectMappingDTO;
import com.group.Timetable.Generator.entities.*;
import com.group.Timetable.Generator.mapper.TeacherSubjectMappingMapper;

@Service
@Transactional
public class TeacherSubjectMappingService {

    private final TeacherSubjectMappingRepository repo;
    private final TeacherRepository teacherRepo;
    private final SubjectRepository subjectRepo;
    private final SectionRepository sectionRepo;
    private final UserRepository userRepo;

    public TeacherSubjectMappingService(TeacherSubjectMappingRepository repo,
                                        TeacherRepository teacherRepo,
                                        SubjectRepository subjectRepo,
                                        SectionRepository sectionRepo,
                                        UserRepository userRepo) {
        this.repo = repo;
        this.teacherRepo = teacherRepo;
        this.subjectRepo = subjectRepo;
        this.sectionRepo = sectionRepo;
        this.userRepo = userRepo;
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    // Create mapping
    public TeacherSubjectMappingDTO save(TeacherSubjectMappingDTO dto) {
        User user = getAuthenticatedUser();

        var teacher = teacherRepo.findById(dto.getTeacherId()).orElseThrow(() -> new RuntimeException("Teacher not found"));
        var subject = subjectRepo.findById(dto.getSubjectId()).orElseThrow(() -> new RuntimeException("Subject not found"));
        var section = sectionRepo.findById(dto.getSectionId()).orElseThrow(() -> new RuntimeException("Section not found"));

        if (!teacher.getUser().getId().equals(user.getId()) ||
            !subject.getUser().getId().equals(user.getId()) ||
            !section.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        if (subject.getSemester() != section.getSemester()) {
            throw new RuntimeException("Subject semester and Section semester must match");
        }

        repo.findByTeacherAndSubjectAndSectionAndUser(teacher, subject, section, user)
                .ifPresent(m -> { throw new RuntimeException("This teacher is already mapped to this subject for the selected section"); });

        var saved = repo.save(TeacherSubjectMappingMapper.toEntity(dto, teacher, subject, section, user));
        return TeacherSubjectMappingMapper.toDTO(saved);
    }

    public List<TeacherSubjectMappingDTO> listAll() {
        User user = getAuthenticatedUser();
        return repo.findByUser(user).stream().map(TeacherSubjectMappingMapper::toDTO).toList();
    }

    public TeacherSubjectMappingDTO getById(Long id) {
        User user = getAuthenticatedUser();
        var m = repo.findById(id).orElseThrow(() -> new RuntimeException("Mapping not found"));
        if (!m.getUser().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");
        return TeacherSubjectMappingMapper.toDTO(m);
    }

    public TeacherSubjectMappingDTO update(Long id, TeacherSubjectMappingDTO dto) {
        User user = getAuthenticatedUser();
        var existing = repo.findById(id).orElseThrow(() -> new RuntimeException("Mapping not found"));

        if (!existing.getUser().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");

        var teacher = teacherRepo.findById(dto.getTeacherId()).orElseThrow(() -> new RuntimeException("Teacher not found"));
        var subject = subjectRepo.findById(dto.getSubjectId()).orElseThrow(() -> new RuntimeException("Subject not found"));
        var section = sectionRepo.findById(dto.getSectionId()).orElseThrow(() -> new RuntimeException("Section not found"));

        if (!teacher.getUser().getId().equals(user.getId()) ||
            !subject.getUser().getId().equals(user.getId()) ||
            !section.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

      if (subject.getSemester() != section.getSemester()) {
      throw new RuntimeException("Subject semester and Section semester must match");
  }

        boolean changed = !existing.getTeacher().getTeacherId().equals(teacher.getTeacherId())
                || !existing.getSubject().getSubId().equals(subject.getSubId())
                || !existing.getSection().getSectionId().equals(section.getSectionId());

        if (changed) {
            repo.findByTeacherAndSubjectAndSectionAndUser(teacher, subject, section, user)
                    .ifPresent(m -> { if (!m.getId().equals(id)) throw new RuntimeException("Another mapping exists for same teacher, subject & section"); });
        }

        existing.setTeacher(teacher);
        existing.setSubject(subject);
        existing.setSection(section);

        return TeacherSubjectMappingMapper.toDTO(repo.save(existing));
    }

    public void delete(Long id) {
        User user = getAuthenticatedUser();
        var m = repo.findById(id).orElseThrow(() -> new RuntimeException("Mapping not found"));
        if (!m.getUser().getId().equals(user.getId())) throw new RuntimeException("Unauthorized delete attempt");
        repo.delete(m);
    }

    public List<TeacherSubjectMappingDTO> searchByTeacherName(String name) {
        User user = getAuthenticatedUser();
        return repo.findByTeacherTeacherNameContainingIgnoreCaseAndUser(name, user)
                .stream().map(TeacherSubjectMappingMapper::toDTO).toList();
    }
}
