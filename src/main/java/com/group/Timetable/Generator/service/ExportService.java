package com.group.Timetable.Generator.service;
import com.group.Timetable.Generator.dto.ScheduleDTO;
import com.group.Timetable.Generator.entities.Schedule;
import com.group.Timetable.Generator.entities.User;
import com.group.Timetable.Generator.Repository.UserRepository;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;

import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ExportService {

    private final ScheduleService scheduleService;
    private final UserRepository userRepository;

    public ExportService(ScheduleService scheduleService, UserRepository userRepository) {
        this.scheduleService = scheduleService;
        this.userRepository = userRepository;
    }

    public ResponseEntity<byte[]> exportTimetable(String type, Authentication auth) throws Exception {

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Schedule> schedules = scheduleService.getUserSchedule();
        if (schedules.isEmpty())
            throw new RuntimeException("No schedules found");

        byte[] data;

        if (type.equalsIgnoreCase("pdf")) {
            data = exportPDF(schedules, user);
        } else {
            throw new RuntimeException("Unsupported file type");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("timetable.pdf").build());
        headers.setContentType(MediaType.APPLICATION_PDF);

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    private byte[] exportPDF(List<Schedule> schedules, User user) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4.rotate(), 18, 18, 18, 18);
        PdfWriter.getInstance(doc, out);
        doc.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 9);

        String[] DAYS = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};

        // --- GROUP BY COURSE, BRANCH, SEMESTER, SECTION
        Map<String, List<Schedule>> grouped = schedules.stream()
                .collect(Collectors.groupingBy(s ->
                        safe(s.getSection().getCourse().getCourseName()) +
                        "_BR_" + safe(s.getSection().getBranch()) +
                        "_SEM_" + safe(s.getSection().getSemester()) +
                        "_SEC_" + safe(s.getSection().getSectionName())
                ));

        for (List<Schedule> list : grouped.values()) {

            Schedule ref = list.get(0);

            Paragraph p1 = new Paragraph(user.getInstituteName(), titleFont);
            p1.setAlignment(Element.ALIGN_CENTER);
            doc.add(p1);

            doc.add(new Paragraph("Department: " +
                    safe(ref.getSection().getCourse().getDepartment().getDeptName()), headerFont));

            doc.add(new Paragraph("Course: " +
                    safe(ref.getSection().getCourse().getCourseName()), headerFont));

            doc.add(new Paragraph("Branch: " +
                    safe(ref.getSection().getBranch()), headerFont));

            doc.add(new Paragraph("Semester: " +
                    safe(ref.getSection().getSemester()), headerFont));

            doc.add(new Paragraph("Section: " +
                    safe(ref.getSection().getSectionName()), headerFont));

            doc.add(Chunk.NEWLINE);

            // --- TIME SLOT SORTING (CORRECT)
            List<String> slots = list.stream()
                    .sorted(Comparator.comparing(s -> s.getTimeSlot().getStartTime()))
                    .map(s -> safe(s.getTimeSlot().getStartTime()) + " - " + safe(s.getTimeSlot().getEndTime()))
                    .distinct()
                    .collect(Collectors.toList());

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);

            addHeader(table, "TIME / DAY");
            for (String d : DAYS) addHeader(table, d);

            for (String slot : slots) {
                addCell(table, slot, cellFont);

                for (String day : DAYS) {
                    Schedule match = list.stream().filter(s ->
                            cap(s.getTimeSlot().getDay()).equalsIgnoreCase(day) &&
                            (safe(s.getTimeSlot().getStartTime()) + " - " + safe(s.getTimeSlot().getEndTime())).equals(slot)
                    ).findFirst().orElse(null);

                    if (match == null) {
                        addCell(table, "", cellFont);
                    } else {
                        String content =
                                safe(match.getSubject().getSubName()) +
                                "\n(" + safe(match.getTeacher().getTeacherName()) + ")" +
                                "\n" + safe(match.getRoom().getRoomName());

                        addCell(table, content, cellFont);
                    }
                }
            }

            doc.add(table);
            doc.add(Chunk.NEWLINE);

            // --- UNIQUE SUBJECT SUMMARY
            PdfPTable sumTable = new PdfPTable(3);
            sumTable.setWidthPercentage(50);
            addHeader(sumTable, "Subject Name");
            addHeader(sumTable, "Credits");
            addHeader(sumTable, "Teacher");

            Map<Long, Schedule> uniqueSubjects = new LinkedHashMap<>();

            for (Schedule s : list) {
                uniqueSubjects.putIfAbsent(s.getSubject().getSubId(), s);
            }

            for (Schedule s : uniqueSubjects.values()) {
                addCell(sumTable, safe(s.getSubject().getSubName()), cellFont);
                addCell(sumTable, safe(s.getSubject().getSubCredit()), cellFont);
                addCell(sumTable, safe(s.getTeacher().getTeacherName()), cellFont);
            }

            doc.add(sumTable);
            doc.newPage();
        }

        doc.close();
        return out.toByteArray();
    }

    private void addHeader(PdfPTable t, String text) {
        PdfPCell c = new PdfPCell(new Phrase(text, new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)));
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setBackgroundColor(BaseColor.LIGHT_GRAY);
        t.addCell(c);
    }

    private void addCell(PdfPTable t, String text, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        t.addCell(c);
    }

    private static String safe(Object o) {
        return o == null ? "" : o.toString().trim();
    }

    private static String cap(String s) {
        if (s == null) return "";
        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }
}

