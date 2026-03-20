package com.pm.project_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.pm.project_manager.model.ProjectMember;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProjectId(Long projectId);
    List<ProjectMember> findByUserId(Long userId);
<<<<<<< HEAD

    boolean existsByProjectIdAndUserUsername(Long projectId, String username);
=======
    boolean existsByProjectIdAndUserId(Long projectId, Long userId);
    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);
>>>>>>> d17e24c3d68b7dcf495f7b4fda3c004d4c1c759e
}