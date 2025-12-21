package com.growthtutoring.backend.matching;

import com.growthtutoring.backend.user.User;
import com.growthtutoring.backend.user.UserRepository;
import com.growthtutoring.backend.matching.dto.PendingStudentDto;
import com.growthtutoring.backend.matching.dto.PendingTutorDto;
import com.growthtutoring.backend.matching.dto.SimpleUserDto;
import com.growthtutoring.backend.matching.dto.CreateLinkRequest;
import com.growthtutoring.backend.matching.dto.LinkStatusResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class StudentTutorLinkController {

    @Autowired
    private StudentTutorLinkRepository linkRepo;

    @Autowired
    private UserRepository userRepo;

    // --------------------------------------------------------------------
    // Helper: create or reuse a student–tutor link
    // --------------------------------------------------------------------
    private ResponseEntity<?> handleCreateLink(Long studentUserId, Long tutorUserId) {
        User student = userRepo.findById(studentUserId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        User tutor = userRepo.findById(tutorUserId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor not found"));

        // Check existing link between this student & tutor
        Optional<StudentTutorLink> existingOpt =
                linkRepo.findByStudent_IdAndTutor_Id(studentUserId, tutorUserId);

        if (existingOpt.isPresent()) {
            StudentTutorLink existing = existingOpt.get();
            StudentTutorLinkStatus status = existing.getStatus();

            if (status == StudentTutorLinkStatus.DECLINED) {
                // allow re-request: just flip back to PENDING
                existing.setStatus(StudentTutorLinkStatus.PENDING);
                linkRepo.save(existing);
                return ResponseEntity.ok("Tutor request sent again.");
            } else if (status == StudentTutorLinkStatus.PENDING) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("You already have a pending request to this tutor.");
            } else { // ACCEPTED
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("You are already matched with this tutor.");
            }
        }

        // No existing link → create new one
        StudentTutorLink link = new StudentTutorLink();
        link.setStudent(student);
        link.setTutor(tutor);
        link.setStatus(StudentTutorLinkStatus.PENDING);

        linkRepo.save(link);
        return ResponseEntity.ok("Tutor request sent.");
    }

    // ------------------------------------------------------------
// 1. STUDENT SENDS REQUEST TO TUTOR
// ------------------------------------------------------------
    @PostMapping("/students/{studentUserId}/request-tutor/{tutorUserId}")
    public ResponseEntity<?> requestTutor(
            @PathVariable Long studentUserId,
            @PathVariable Long tutorUserId) {

        // just reuse the same logic
        return handleCreateLink(studentUserId, tutorUserId);
    }

    // --------------------------------------------------------------------
    // 1b. STUDENT SENDS REQUEST TO TUTOR (JSON body)
    //     POST /api/student-tutor-links
    //     body: { "studentUserId": ..., "tutorUserId": ... }
    // --------------------------------------------------------------------
    @PostMapping("/student-tutor-links")
    public ResponseEntity<?> requestTutorViaBody(@RequestBody CreateLinkRequest req) {
        if (req.getStudentUserId() == null || req.getTutorUserId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "studentUserId and tutorUserId are required"
            );
        }
        return handleCreateLink(req.getStudentUserId(), req.getTutorUserId());
    }

    // --------------------------------------------------------------------
    // 1c. CHECK CURRENT LINK STATUS BETWEEN STUDENT & TUTOR
    //     GET /api/student-tutor-links/status?studentUserId=..&tutorUserId=..
    //     returns { "status": "NONE" | "PENDING" | "ACCEPTED" | "DECLINED" }
    // --------------------------------------------------------------------
    @GetMapping("/student-tutor-links/status")
    public ResponseEntity<LinkStatusResponse> getLinkStatus(
            @RequestParam Long studentUserId,
            @RequestParam Long tutorUserId) {

        Optional<StudentTutorLink> existingOpt =
                linkRepo.findByStudent_IdAndTutor_Id(studentUserId, tutorUserId);

        if (existingOpt.isEmpty()) {
            return ResponseEntity.ok(new LinkStatusResponse("NONE"));
        }

        StudentTutorLink link = existingOpt.get();
        return ResponseEntity.ok(new LinkStatusResponse(link.getStatus().name()));
    }

    // --------------------------------------------------------------------
    // 2. TUTOR SEES PENDING STUDENT REQUESTS
    //     GET /api/tutors/user/{tutorUserId}/student-requests
    // --------------------------------------------------------------------
    @GetMapping("/tutors/user/{tutorUserId}/student-requests")
    public List<PendingStudentDto> getTutorPendingRequests(
            @PathVariable Long tutorUserId) {

        return linkRepo.findByTutor_IdAndStatus(tutorUserId, StudentTutorLinkStatus.PENDING)
                .stream()
                .map(link -> {
                    User s = link.getStudent();
                    return new PendingStudentDto(
                            link.getId(),
                            s.getId(),
                            s.getFirstName(),
                            s.getLastName(),
                            s.getUserUid(),
                            s.getEmail()
                    );
                })
                .collect(Collectors.toList());
    }

    // --------------------------------------------------------------------
    // 3. TUTOR SEES ACCEPTED STUDENTS
    //     GET /api/tutors/user/{tutorUserId}/students
    // --------------------------------------------------------------------
    @GetMapping("/tutors/user/{tutorUserId}/students")
    public List<SimpleUserDto> getTutorAcceptedStudents(
            @PathVariable Long tutorUserId) {

        return linkRepo.findByTutor_IdAndStatus(tutorUserId, StudentTutorLinkStatus.ACCEPTED)
                .stream()
                .map(link -> new SimpleUserDto(link.getStudent()))
                .collect(Collectors.toList());
    }

    // --------------------------------------------------------------------
    // 4. STUDENT SEES ACCEPTED TUTORS
    //     GET /api/students/user/{studentUserId}/tutors
    // --------------------------------------------------------------------
    @GetMapping("/students/user/{studentUserId}/tutors")
    public List<SimpleUserDto> getStudentTutors(
            @PathVariable Long studentUserId) {

        return linkRepo.findByStudent_IdAndStatus(studentUserId, StudentTutorLinkStatus.ACCEPTED)
                .stream()
                .map(link -> new SimpleUserDto(link.getTutor()))
                .collect(Collectors.toList());
    }

    // --------------------------------------------------------------------
    // 4b. STUDENT SEES PENDING TUTOR REQUESTS
    //     GET /api/students/user/{studentUserId}/tutor-requests
    // --------------------------------------------------------------------
    @GetMapping("/students/user/{studentUserId}/tutor-requests")
    public List<PendingTutorDto> getStudentPendingTutors(
            @PathVariable Long studentUserId) {

        return linkRepo.findByStudent_IdAndStatus(studentUserId, StudentTutorLinkStatus.PENDING)
                .stream()
                .map(link -> {
                    User t = link.getTutor();
                    return new PendingTutorDto(
                            link.getId(),
                            t.getId(),
                            t.getFirstName(),
                            t.getLastName(),
                            t.getUserUid(),
                            t.getEmail()
                    );
                })
                .collect(Collectors.toList());
    }

    // --------------------------------------------------------------------
    // 5. ACCEPT / DECLINE REQUEST
    //     POST /api/student-tutor-links/{linkId}/decision
    //     body: { "decision": "ACCEPT" | "DECLINE" }
    // --------------------------------------------------------------------
    public static class DecisionRequest {
        private String decision;

        public String getDecision() {
            return decision;
        }

        public void setDecision(String decision) {
            this.decision = decision;
        }
    }

    @PostMapping("/student-tutor-links/{linkId}/decision")
    public ResponseEntity<?> decideOnRequest(
            @PathVariable Long linkId,
            @RequestBody DecisionRequest req) {

        StudentTutorLink link = linkRepo.findById(linkId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Link not found"));

        if (req.getDecision() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "decision is required"
            );
        }

        String d = req.getDecision().trim().toUpperCase();

        switch (d) {
            case "ACCEPT" -> link.setStatus(StudentTutorLinkStatus.ACCEPTED);
            case "DECLINE" -> link.setStatus(StudentTutorLinkStatus.DECLINED);
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "decision must be ACCEPT or DECLINE"
            );
        }

        linkRepo.save(link);
        return ResponseEntity.ok().build();
    }
}
