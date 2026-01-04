package com.group.Timetable.Generator.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Courses {


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long courseId;


@Column(name = "courseName", nullable = false, length = 30)
private String courseName;


@Column(name = "semester", nullable = false)
private int semester;



@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "dept_id", nullable = false)
private Department department;


@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;
}

