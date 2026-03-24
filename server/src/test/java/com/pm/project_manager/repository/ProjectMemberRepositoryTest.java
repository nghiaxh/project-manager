package com.pm.project_manager.repository;

import com.pm.project_manager.model.Project;
import com.pm.project_manager.model.ProjectMember;
import com.pm.project_manager.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class ProjectMemberRepositoryTest {

    @Autowired
    private ProjectMemberRepository repo;

    @Autowired
    private TestEntityManager em;

    @Test
    void existsByProjectIdAndUserId_shouldReturnTrue() {
        User user = new User();
        user.setUsername("hung");
        user.setName("hung");
        user.setEmail("hung@gmail.com");
        user.setPassword("secret");
        em.persist(user);

        Project project = new Project();
        em.persist(project);

        ProjectMember pm = new ProjectMember();
        pm.setUser(user);
        pm.setProject(project);
        em.persist(pm);

        boolean exists = repo.existsByProjectIdAndUserId(
                project.getId(), user.getId());

        assertTrue(exists);
    }
}