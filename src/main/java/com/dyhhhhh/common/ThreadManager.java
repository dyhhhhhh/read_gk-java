package com.dyhhhhh.common;

import com.dyhhhhh.bean.dto.AccountDTO;
import com.dyhhhhh.bean.task.TaskStatus;
import com.dyhhhhh.common.service.StartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

@Service
public class ThreadManager {
    //内部类，用于将日志输出到当前线程类中
    private static class LoggedPrintStream extends PrintStream {

        private final TaskStatus taskStatus;

        //系统输出
        public LoggedPrintStream(OutputStream outputStream, TaskStatus taskStatus) {
            super(outputStream, true);
            this.taskStatus = taskStatus;
        }

        //重新println，让日志输出到线程状态类中
        @Override
        public void println(String x) {
            taskStatus.addLog(x);
            super.println(x);
        }
    }

    @Autowired
    private StartService startService;

    // 线程安全的任务存储
    private final ConcurrentMap<Long, TaskStatus> taskStatusMap = new ConcurrentHashMap<>();
    //线程安全的随机数
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    // 创建固定10线程的线程池
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    //提交任务到线程
    public <T> List<Long> submitTasks(List<T> taskInputs, TaskProcessor<T> processor) {
        ArrayList<Long> taskIds = new ArrayList<>();
        for (T input : taskInputs) {
            long taskId;
            TaskStatus taskStatus;

            // 循环生成唯一ID
            do {
                // 生成正随机数（范围：1 ~ Long.MAX_VALUE）
                taskId = random.nextLong(1, Long.MAX_VALUE);
                taskStatus = new TaskStatus();
                taskStatus.setTaskId(taskId);
                setTaskIdentifier(taskStatus, input);
                taskStatus.setStatus("PENDING");
            } while (taskStatusMap.putIfAbsent(taskId, taskStatus) != null); // 原子性操作

            long finalTaskId = taskId;
            executor.submit(() -> processTask(finalTaskId, input, processor));
            taskIds.add(taskId);
        }
        return taskIds;
    }


    // 统一任务处理器接口
    @FunctionalInterface
    public interface TaskProcessor<T> {
        void process(T input) throws Exception;
    }


    //线程处理器
    private <T> void processTask(Long taskId, T input, TaskProcessor<T> processor) {
        final TaskStatus status = taskStatusMap.get(taskId);

        final PrintStream originalOut = System.out;
        try {
            //重定向System.out到当前任务的日志系统s
            System.setOut(new LoggedPrintStream(originalOut, status));

            status.setStatus("RUNNING");
            status.setStartTime(LocalDateTime.now());

            processor.process(input);
            status.setStatus("COMPLETED");
            status.setResult("Success");
        } catch (Exception e) {
            status.setStatus("FAILED");
            status.setResult(e.getMessage());
            e.printStackTrace();
        } finally {
            System.setOut(originalOut); // 恢复原始输出流
            status.setEndTime(LocalDateTime.now());
        }
    }

    //处理不同类型
    private <T> void setTaskIdentifier(TaskStatus status, T input) {
        if (input instanceof AccountDTO) {
            status.setUsername(((AccountDTO) input).getUsername());
        } else if (input instanceof String) {
            status.setUsername("CookieTask-" + input.hashCode());
        }
    }

    // 原有具体处理方法保持不变
    private void processAccount(AccountDTO account) {
        startService.start(account);
    }

    private void processCookie(String cookie) {
        startService.start(cookie);
    }

    private void processCookie(String cookie, List<String> courses) {
        startService.start(cookie, courses);
    }

    public List<Long> submitAccountTasks(List<AccountDTO> accounts) {
        return submitTasks(accounts, this::processAccount);
    }

    public List<Long> submitCookieTasks(List<String> cookies) {
        return submitTasks(cookies, this::processCookie);
    }

    public List<Long> submitCookieTasks(List<String> cookies, List<String> courses) {
        return submitTasks(cookies, cookie -> {
            // 使用lambda表达式捕获courses参数
            processCookie(cookie, new ArrayList<>(courses)); // 创建课程列表副本保证线程安全
        });
    }

    public Long submitSingleCookieTask(String cookie, List<String> courses) {
        List<Long> ids = submitCookieTasks(Collections.singletonList(cookie), courses);
        return ids.isEmpty() ? null : ids.get(0);
    }

    //获取所有线程的状态
    public List<TaskStatus> getAllTaskStatus() {
        return new ArrayList<>(taskStatusMap.values());
    }

    public TaskStatus getTaskStatus(Long taskId) {
        return taskStatusMap.get(taskId);
    }

}
