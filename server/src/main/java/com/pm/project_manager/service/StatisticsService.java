package com.pm.project_manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.pm.project_manager.model.Task;
import com.pm.project_manager.model.TaskStatus;
import com.pm.project_manager.repository.TaskRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final TaskRepository taskRepository;

    public Map<String, Long> getProjectStatistics(Long projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", (long) tasks.size());
        stats.put("todo", tasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count());
        stats.put("inProgress", tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count());
        stats.put("done", tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count());
        stats.put("cancelled", tasks.stream().filter(t -> t.getStatus() == TaskStatus.CANCELLED).count());
        return stats;
    }
}