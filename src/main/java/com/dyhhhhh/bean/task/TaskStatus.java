package com.dyhhhhh.bean.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 线程状态对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatus {

    //现在日志列表，防止内存溢出
    private static final int MAX_LOG_LINES = 1000;

    private Long taskId;
    private String username;
    private String status; // PENDING, RUNNING, COMPLETED, FAILED
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String result;

    // 新增线程安全的日志存储
    private final List<String> consoleLogs = new CopyOnWriteArrayList<>();
    // 添加日志方法
    public void addLog(String log) {
        if (consoleLogs.size() >= MAX_LOG_LINES) {
            //删除最早的数据
            consoleLogs.remove(0);
        }
        consoleLogs.add(LocalDateTime.now() + " - " + log);
    }
    // 获取日志拷贝
    public List<String> getLogs() {
        return new ArrayList<>(consoleLogs);
    }
}
