package com.pm.project_manager.service;

import com.pm.project_manager.model.Comment;
import com.pm.project_manager.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    @Transactional
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!comment.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You don't have permission to delete this comment");
        }
        commentRepository.delete(comment);
    }
}