package com.emojisphere.service;

import com.emojisphere.dto.SubmitFeedbackRequest;
import com.emojisphere.entity.Feedback;
import com.emojisphere.entity.User;
import com.emojisphere.repository.FeedbackRepository;
import com.emojisphere.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // Admin email - fallback to spring.mail.username if not set
    @Value("${app.admin.email:${spring.mail.username}}")
    private String adminEmail;

    public Feedback saveFeedback(Long userId, SubmitFeedbackRequest request) {
        Feedback fb = new Feedback();
        fb.setUserId(userId);
        fb.setType(request.getType());
        fb.setSubject(request.getSubject());
        fb.setMessage(request.getMessage());
        fb.setStatus("open");

        Feedback saved = feedbackRepository.save(fb);

        return saved;
    }

    /**
     * Send notification email to admin for a saved feedback.
     * This method will throw RuntimeException on failure so callers can decide how to handle it.
     */
    public void notifyAdmin(Feedback fb) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(adminEmail);
        message.setSubject("[EmojiSphere] New Feedback: " + (fb.getSubject() == null ? "(no subject)" : fb.getSubject()));

        String userInfo = "Unknown user";
        if (fb.getUserId() != null) {
            User user = userRepository.findById(fb.getUserId()).orElse(null);
            if (user != null) {
                userInfo = String.format("User: %s (id=%d, email=%s, mobile=%s)", user.getFullName(), user.getId(), user.getEmail(), user.getMobileNumber());
            } else {
                userInfo = "User id=" + fb.getUserId();
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("A new feedback has been submitted on EmojiSphere:\n\n");
        sb.append(userInfo).append("\n\n");
        sb.append("Type: ").append(fb.getType()).append("\n");
        sb.append("Subject: ").append(fb.getSubject()).append("\n\n");
        sb.append("Message:\n").append(fb.getMessage()).append("\n\n");
        sb.append("Status: ").append(fb.getStatus()).append("\n");

        message.setText(sb.toString());

        try {
            mailSender.send(message);
        } catch (org.springframework.mail.MailException e) {
            // bubble up as runtime so controller can return 500
            throw new RuntimeException("Failed to send feedback email: " + e.getMessage(), e);
        }
    }
}
