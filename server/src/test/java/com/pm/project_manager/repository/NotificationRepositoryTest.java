package com.pm.project_manager.repository;

import com.pm.project_manager.model.Notification;
import com.pm.project_manager.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository repo;

    @Autowired
    private TestEntityManager em;

    private User testUser;

    //Setup user 1 lần
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test_" + UUID.randomUUID() + "@gmail.com");
        testUser.setUsername("hung_" + UUID.randomUUID());
        testUser.setName("Hung");
        testUser.setPassword("123");
        em.persist(testUser);
        em.flush(); // đẩy vào DB
    }

    @Test
    void findByUserIdOrderByCreatedAtDesc_shouldReturnSorted() {
        // Arrange
        Notification older = buildNotification(testUser, LocalDateTime.now().minusDays(1), false);
        Notification newer = buildNotification(testUser, LocalDateTime.now(), false);
        em.persist(older);
        em.persist(newer);
        em.flush();
        em.clear(); // clear cache, buộc query đọc từ DB thật

        List<Notification> result =
                repo.findByUserIdOrderByCreatedAtDesc(testUser.getId());

        assertEquals(2, result.size());
        assertTrue(result.get(0).getCreatedAt().isAfter(result.get(1).getCreatedAt()),
                "Notification mới hơn phải đứng trước"); //message khi fail
    }

    @Test
    void findByUserIdAndReadFalse_shouldReturnUnreadOnly() {
        em.persist(buildNotification(testUser, LocalDateTime.now(), true));
        em.persist(buildNotification(testUser, LocalDateTime.now(), false));
        em.flush();
        em.clear();

        List<Notification> result =
                repo.findByUserIdAndReadFalse(testUser.getId());

        assertEquals(1, result.size());
        assertFalse(result.get(0).getRead(), "Chỉ trả về notification chưa đọc");
    }

    //user không có notification
    @Test
    void findByUserIdOrderByCreatedAtDesc_shouldReturnEmpty_whenNoNotification() {
        List<Notification> result =
                repo.findByUserIdOrderByCreatedAtDesc(testUser.getId());

        assertTrue(result.isEmpty());
    }

    // không trả về notification của user khác
    @Test
    void findByUserIdAndReadFalse_shouldNotReturnOtherUsersNotification() {
        // User khác
        User otherUser = new User();
        otherUser.setEmail("other_" + UUID.randomUUID() + "@gmail.com");
        otherUser.setUsername("other_" + UUID.randomUUID());
        otherUser.setName("Other");
        otherUser.setPassword("123");
        em.persist(otherUser);

        // Notification của otherUser
        em.persist(buildNotification(otherUser, LocalDateTime.now(), false));
        em.flush();
        em.clear();

        //không được thấy notification của otherUser
        List<Notification> result =
                repo.findByUserIdAndReadFalse(testUser.getId());

        assertTrue(result.isEmpty());
    }

    //tránh lặp code
    private Notification buildNotification(User user, LocalDateTime createdAt, boolean read) {
        Notification n = new Notification();
        n.setUser(user);
        n.setCreatedAt(createdAt);
        n.setRead(read);
        return n;
    }
}