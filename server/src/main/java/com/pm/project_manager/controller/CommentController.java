package com.pm.project_manager.controller;

import com.pm.project_manager.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        commentService.deleteComment(id, currentUser.getUsername());
        return ResponseEntity.ok().build();
    }
}