package com.growthtutoring.backend.matching;

import com.growthtutoring.backend.user.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "student_tutor_links",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_user_id", "tutor_user_id"})
)
public class StudentTutorLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // student is a USER with role STUDENT
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_user_id", nullable = false)
    private User student;

    // tutor is a USER with role TUTOR
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_user_id", nullable = false)
    private User tutor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentTutorLinkStatus status = StudentTutorLinkStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    public StudentTutorLink() {
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    // -------- getters / setters ----------

    public Long getId() {
        return id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public User getTutor() {
        return tutor;
    }

    public void setTutor(User tutor) {
        this.tutor = tutor;
    }

    public StudentTutorLinkStatus getStatus() {
        return status;
    }

    public void setStatus(StudentTutorLinkStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}