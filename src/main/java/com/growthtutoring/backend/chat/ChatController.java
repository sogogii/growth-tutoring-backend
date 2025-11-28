package com.growthtutoring.backend.chat;

import com.growthtutoring.backend.chat.dto.ConversationSummaryDto;
import com.growthtutoring.backend.chat.dto.MessageDto;
import com.growthtutoring.backend.chat.dto.SendMessageRequest;
import com.growthtutoring.backend.matching.StudentTutorLink;
import com.growthtutoring.backend.matching.StudentTutorLinkRepository;
import com.growthtutoring.backend.matching.StudentTutorLinkStatus;
import com.growthtutoring.backend.user.User;
import com.growthtutoring.backend.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:8080",
        "https://dev.growthtutoringhq.com"
})
public class ChatController {

    private final ConversationRepository conversationRepo;
    private final MessageRepository messageRepo;
    private final StudentTutorLinkRepository linkRepo;
    private final UserRepository userRepo;

    public ChatController(
            ConversationRepository conversationRepo,
            MessageRepository messageRepo,
            StudentTutorLinkRepository linkRepo,
            UserRepository userRepo) {
        this.conversationRepo = conversationRepo;
        this.messageRepo = messageRepo;
        this.linkRepo = linkRepo;
        this.userRepo = userRepo;
    }

    // ---------------------------------------------------------
    // 1. Get or create conversation for a student/tutor pair
    //    Only allowed if they have an ACCEPTED link
    //    POST /api/chat/conversation?studentUserId=..&tutorUserId=..
    // ---------------------------------------------------------
    @PostMapping("/conversation")
    public ConversationSummaryDto getOrCreateConversation(
            @RequestParam Long studentUserId,
            @RequestParam Long tutorUserId) {

        StudentTutorLink link = linkRepo
                .findByStudent_IdAndTutor_Id(studentUserId, tutorUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You don't have a relationship with this tutor"
                ));

        if (link.getStatus() != StudentTutorLinkStatus.ACCEPTED) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Chat is only available after you are matched."
            );
        }

        Conversation conv = conversationRepo
                .findByStudent_IdAndTutor_Id(studentUserId, tutorUserId)
                .orElseGet(() -> {
                    Conversation c = new Conversation();
                    User student = userRepo.findById(studentUserId)
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND, "Student not found"));
                    User tutor = userRepo.findById(tutorUserId)
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND, "Tutor not found"));

                    c.setStudent(student);
                    c.setTutor(tutor);
                    return conversationRepo.save(c);
                });

        // The frontend can figure out who "other user" is,
        // but we return conversation id so it can navigate.
        return new ConversationSummaryDto(
                conv.getId(),
                null,
                null
        );
    }

    // ---------------------------------------------------------
    // 2. List all conversations for current user
    //    GET /api/chat/conversations?userId=..
    // ---------------------------------------------------------
    @GetMapping("/conversations")
    public List<ConversationSummaryDto> listUserConversations(
            @RequestParam Long userId) {

        List<Conversation> convs =
                conversationRepo.findByStudent_IdOrTutor_Id(userId, userId);

        return convs.stream()
                .map(c -> {
                    User other = c.getStudent().getId().equals(userId)
                            ? c.getTutor()
                            : c.getStudent();
                    String name = other.getFirstName() + " " + other.getLastName();
                    return new ConversationSummaryDto(
                            c.getId(),
                            other.getId(),
                            name
                    );
                })
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------
    // 3. Get messages in a conversation
    //    GET /api/chat/conversations/{conversationId}/messages
    // ---------------------------------------------------------
    @GetMapping("/conversations/{conversationId}/messages")
    public List<MessageDto> getMessages(
            @PathVariable Long conversationId) {

        return messageRepo.findByConversation_IdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(m -> new MessageDto(
                        m.getId(),
                        m.getSender().getId(),
                        m.getContent(),
                        m.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------
    // 4. Send message
    //    POST /api/chat/conversations/{conversationId}/messages?senderUserId=..
    //    body: { "content": "..." }
    // ---------------------------------------------------------
    @PostMapping("/conversations/{conversationId}/messages")
    public MessageDto sendMessage(
            @PathVariable Long conversationId,
            @RequestParam Long senderUserId,
            @RequestBody SendMessageRequest req) {

        if (req.getContent() == null || req.getContent().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Message content cannot be empty"
            );
        }

        Conversation conv = conversationRepo.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Conversation not found"));

        // sender must be part of this conversation
        boolean isStudent = conv.getStudent().getId().equals(senderUserId);
        boolean isTutor = conv.getTutor().getId().equals(senderUserId);

        if (!isStudent && !isTutor) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not part of this conversation"
            );
        }

        User sender = userRepo.findById(senderUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Sender not found"));

        Message msg = new Message();
        msg.setConversation(conv);
        msg.setSender(sender);
        msg.setContent(req.getContent().trim());

        messageRepo.save(msg);

        return new MessageDto(
                msg.getId(),
                sender.getId(),
                msg.getContent(),
                msg.getCreatedAt()
        );
    }
}