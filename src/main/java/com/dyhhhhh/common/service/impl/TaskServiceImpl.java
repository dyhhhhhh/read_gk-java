package com.dyhhhhh.common.service.impl;

import com.dyhhhhh.bean.dto.AccountDTO;
import com.dyhhhhh.bean.task.TaskStatus;
import com.dyhhhhh.common.service.TaskService;
import com.dyhhhhh.common.ThreadManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private ThreadManager threadManager;

    /**
     * 提交多个账号
     * @param accounts:账户信息
     * @return
     */
    @Override
    public ResponseEntity<?> submitTasks(List<AccountDTO> accounts) {
        List<Long> longs = threadManager.submitAccountTasks(accounts);
        return ResponseEntity.ok(Map.of("taskIds", longs));
    }

    /**
     * 提交单个cookie
     * @param cookie:cookie
     * @return
     */
    @Override
    public ResponseEntity<?> submitCookieTasks(String cookie) {
        ArrayList<String> cookieString = new ArrayList<>(1);
        cookieString.add(cookie);
        List<Long> longs = threadManager.submitCookieTasks(cookieString);
        return ResponseEntity.ok(Map.of("taskIds", longs));
    }

    /**
     * 只执行传来的课程
     * @param cookie
     * @param courseIds
     * @return
     */
    @Override
    public ResponseEntity<?> submitCookieTargetCourses(String cookie, List<String> courseIds) {
        Long l = threadManager.submitSingleCookieTask(cookie, courseIds);
        return ResponseEntity.ok(Map.of("taskId", l));
    }

    @Override
    public ResponseEntity<TaskStatus> getTaskStatus(Long taskId) {
        TaskStatus taskStatus = threadManager.getTaskStatus(taskId);
        return taskStatus != null ?
                ResponseEntity.ok(taskStatus) :
                ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<List<String>> getTaskLogs(Long taskId,int maxLines) {
        TaskStatus status = threadManager.getTaskStatus(taskId);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }

        List<String> logs = status.getLogs();
        if(maxLines > 0 && logs.size() > maxLines) {
            logs = logs.subList(logs.size() - maxLines, logs.size());
        }
        return ResponseEntity.ok(logs);
    }
}
