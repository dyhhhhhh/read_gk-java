package com.dyhhhhh.controller;

import com.dyhhhhh.bean.dto.AccountDTO;
import com.dyhhhhh.bean.task.TaskStatus;
import com.dyhhhhh.common.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    /**
     * 批量提交账号密码
     * @param accounts
     * @return
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitTasks(@RequestBody List<AccountDTO> accounts) {
        return taskService.submitTasks(accounts);
    }
    /**
     * 提交cookie
     */
    @GetMapping("/submitCookie")
    public ResponseEntity<?> submitCookieTasks(@RequestParam("cookie") String cookie) {
       return taskService.submitCookieTasks(cookie);
    }
    /**
     * 提交单个cookie和要执行的课程
     */
    @GetMapping("/submitCookieAndCourses")
    public ResponseEntity<?> submitCookieAndCourseTasks(@RequestParam("cookie") String cookie,
                                                        @RequestParam("courses") List<String> courses) {
        return taskService.submitCookieTargetCourses(cookie,courses);
    }
//    /**
//     * 查询所有正在执行的线程
//     * @return
//     */
//    @GetMapping("/status")
//    public ResponseEntity<List<TaskStatus>> getAllTaskStatus() {
//        return ResponseEntity.ok(threadManagerService.getAllTaskStatus());
//    }

    /**
     * 根据线程id来查询
     * @param taskId
     * @return
     */
    @GetMapping("/status/{taskId}")
    public ResponseEntity<TaskStatus> getTaskStatus(@PathVariable Long taskId) {
        return taskService.getTaskStatus(taskId);
    }

    /**
     * 查询当前线程的日志
     * @param taskId
     * @param maxLines
     * @return
     */
    @GetMapping("/logs/{taskId}")
    public ResponseEntity<List<String>> getTaskLogs(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "100") int maxLines
    ) {
        return taskService.getTaskLogs(taskId, maxLines);
    }
}
