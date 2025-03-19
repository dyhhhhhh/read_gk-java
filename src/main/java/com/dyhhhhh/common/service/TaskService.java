package com.dyhhhhh.common.service;

import com.dyhhhhh.bean.dto.AccountDTO;
import com.dyhhhhh.bean.task.TaskStatus;
import org.springframework.http.ResponseEntity;


import java.util.List;

/**
 * 提交任务接口
 */
public interface TaskService {
    /**
     * 批量提交账号
     * @param accounts:账户信息
     * @return
     */
    ResponseEntity<?> submitTasks(List<AccountDTO> accounts);

    /**
     * 提交单个cookie和nickname
     * @param cookie:cookie
     * @return
     */
    ResponseEntity<?> submitCookieTasks(String cookie);
    /**
     * 只执行传来的课程
     * @param cookie
     * @param courseIds
     * @return
     */
    ResponseEntity<?> submitCookieTargetCourses(String cookie, List<String> courseIds);
    /**
     * 根据线程id来查询
     * @param taskId
     * @return
     */
    ResponseEntity<TaskStatus> getTaskStatus(Long taskId);
    /**
     *查询当前线程的日志
     */
    ResponseEntity<List<String>> getTaskLogs(Long taskId,int maxLines);
}
