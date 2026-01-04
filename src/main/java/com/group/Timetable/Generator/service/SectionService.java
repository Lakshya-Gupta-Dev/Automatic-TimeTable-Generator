package com.group.Timetable.Generator.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group.Timetable.Generator.Repository.CoursesRepository;
import com.group.Timetable.Generator.Repository.SectionRepository;
import com.group.Timetable.Generator.Repository.UserRepository;
import com.group.Timetable.Generator.dto.SectionDTO;
import com.group.Timetable.Generator.entities.Courses;
import com.group.Timetable.Generator.entities.Section;
import com.group.Timetable.Generator.entities.User;
import com.group.Timetable.Generator.mapper.SectionMapper;

@Service
@Transactional
public class SectionService {

    private final SectionRepository repo;
    private final CoursesRepository courseRepo;
    private final UserRepository userRepo;

    public SectionService(SectionRepository repo,
                          CoursesRepository courseRepo,
                          UserRepository userRepo) {
        this.repo = repo;
        this.courseRepo = courseRepo;
        this.userRepo = userRepo;
    }

    private User getAuthUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public SectionDTO save(SectionDTO dto) {
        User user = getAuthUser();

        Courses course = courseRepo.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to course");
        }

        repo.findBySectionNameIgnoreCaseAndSemesterAndCourseAndBranchIgnoreCaseAndUser(
        	    dto.getSectionName(),
        	    dto.getSemester(),
        	    course,
        	    dto.getBranch(),
        	    user
        	).ifPresent(x -> {
        	    throw new RuntimeException("Section already exists");
        	});


        Section s = SectionMapper.toEntity(dto, course, user);

        return SectionMapper.toDTO(repo.save(s));
    }

    public List<SectionDTO> getAll() {
        User user = getAuthUser();
        return repo.findByUser(user).stream()
                .map(SectionMapper::toDTO)
                .toList();
    }

    public SectionDTO getById(Long id) {
        User user = getAuthUser();

        Section s = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        if (!s.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        return SectionMapper.toDTO(s);
    }

    
    public SectionDTO update(Long id, SectionDTO dto) {
        User user = getAuthUser();

        Section existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized update attempt");
        }

        Courses course = courseRepo.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized course access");
        }

        boolean changed = !existing.getCourse().getCourseId().equals(dto.getCourseId())
                || !existing.getSemester().equals(dto.getSemester())
                || !existing.getSectionName().equalsIgnoreCase(dto.getSectionName())
                || !existing.getBranch().equalsIgnoreCase(dto.getBranch());

        if (changed) {
        	repo.findBySectionNameIgnoreCaseAndSemesterAndCourseAndBranchIgnoreCaseAndUser(
        	        dto.getSectionName(),
        	        dto.getSemester(),
        	        course,
        	        dto.getBranch(),
        	        user
        	).ifPresent(x -> {
                if (!x.getSectionId().equals(id)) {
                    throw new RuntimeException("Duplicate section exists");
                }
            });
        }

        existing.setSectionName(dto.getSectionName());
        existing.setSemester(dto.getSemester());
        existing.setBranch(dto.getBranch());  // ✅ Branch update added
        existing.setCourse(course);

        return SectionMapper.toDTO(repo.save(existing));
    }

    public void delete(Long id) {
        User user = getAuthUser();

        Section s = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        if (!s.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized delete attempt");
        }

        repo.delete(s);
    }
}
