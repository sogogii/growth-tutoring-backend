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

        // Make sure both users exist
        User student = userRepo.findById(studentUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Student not found"));

        User tutor = userRepo.findById(tutorUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Tutor not found"));

        // TODO: you can enforce link ACCEPTED here using linkRepo if you want
        // Optional<StudentTutorLink> linkOpt = linkRepo.findByStudentUserIdAndTutorUserId(...);
        // if (linkOpt.isEmpty() || linkOpt.get().getStatus() != StudentTutorLinkStatus.ACCEPTED) { ... }

        // Try to find existing conversation
        Conversation conv = conversationRepo
                .findByStudent_IdAndTutor_Id(studentUserId, tutorUserId)
                .orElseGet(() -> {
                    Conversation c = new Conversation();
                    c.setStudent(student);
                    c.setTutor(tutor);
                    return conversationRepo.save(c);
                });

        // For this endpoint, "other user" is the tutor (the opponent)
        String otherFullName = tutor.getFirstName() + " " + tutor.getLastName();

        return new ConversationSummaryDto(
                conv.getId(),
                tutor.getId(),
                otherFullName
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
                    // figure out who the "other" person is
                    User other = c.getStudent().getId().equals(userId)
                            ? c.getTutor()
                            : c.getStudent();
                    String name = other.getFirstName() + " " + other.getLastName();

                    ConversationSummaryDto dto = new ConversationSummaryDto(
                            c.getId(),
                            other.getId(),
                            name
                    );

                    // latest message for this conversation
                    Message last = messageRepo
                            .findTopByConversation_IdOrderByCreatedAtDesc(c.getId());
                    if (last != null) {
                        dto.setLastMessageContent(last.getContent());
                        dto.setLastMessageCreatedAt(last.getCreatedAt());
                    }

                    // unread messages for this user in this conversation
                    long unread = messageRepo
                            .countByConversation_IdAndSender_IdNotAndReadAtIsNull(c.getId(), userId);
                    dto.setUnreadCount(unread);

                    return dto;
                })
                // newest conversations first (by last message time)
                .sorted((a, b) -> {
                    if (a.getLastMessageCreatedAt() == null && b.getLastMessageCreatedAt() == null) {
                        return 0;
                    }
                    if (a.getLastMessageCreatedAt() == null) {
                        return 1;
                    }
                    if (b.getLastMessageCreatedAt() == null) {
                        return -1;
                    }
                    return b.getLastMessageCreatedAt().compareTo(a.getLastMessageCreatedAt());
                })
                .collect(java.util.stream.Collectors.toList());
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
                .map(m -> {
                    User sender = m.getSender();
                    return new MessageDto(
                            m.getId(),
                            sender.getId(),
                            m.getContent(),
                            m.getCreatedAt(),
                            sender.getFirstName(),
                            sender.getLastName(),
                            null // avatar URL placeholder
                    );
                })
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
                    HttpStatus.BAD_REQUEST, "Message content cannot be empty");
        }

        Conversation conv = conversationRepo.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Conversation not found"));

        User sender = userRepo.findById(senderUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Sender not found"));

        // Optional: verify sender is part of this conversation
        if (!sender.getId().equals(conv.getStudent().getId())
                && !sender.getId().equals(conv.getTutor().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Sender is not part of this conversation");
        }

        Message msg = new Message();
        msg.setConversation(conv);
        msg.setSender(sender);
        msg.setContent(req.getContent().trim());

        messageRepo.save(msg);

        return new MessageDto(
                msg.getId(),
                sender.getId(),
                msg.getContent(),
                msg.getCreatedAt(),
                sender.getFirstName(),
                sender.getLastName(),
                null // avatar URL placeholder
        );
    }

    @GetMapping("/unread-count")
    public long getUnreadCount(@RequestParam Long userId) {
        return messageRepo.countUnreadForUser(userId);
    }

    @PostMapping("/conversations/{conversationId}/read")
    public ResponseEntity<Void> markConversationRead(
            @PathVariable Long conversationId,
            @RequestParam Long userId) {

        Conversation conv = conversationRepo.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!conv.getStudent().getId().equals(userId) &&
                !conv.getTutor().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        messageRepo.markConversationRead(conversationId, userId);
        return ResponseEntity.ok().build();
    }

}
