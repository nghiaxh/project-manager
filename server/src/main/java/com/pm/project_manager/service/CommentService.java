package com.pm.project_manager.service;

import com.pm.project_manager.model.Comment;
import com.pm.project_manager.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!comment.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You don't have permission to delete this comment");
        }

        Long taskId = comment.getTask().getId();

        commentRepository.delete(comment);

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "DELETE");
        payload.put("id", commentId);
        messagingTemplate.convertAndSend("/topic/tasks/" + taskId + "/comments", (Object) payload);
    }
}