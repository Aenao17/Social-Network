package com.lab.laboratorsapte.utils.events;

import com.lab.laboratorsapte.domain.Task;

public class TaskStatusEvent implements Event{
    private TaskExecutionStatusEventType type;
    private Task task;

    public TaskStatusEvent(TaskExecutionStatusEventType type, Task task) {
        this.type = type;
        this.task = task;
    }

    public TaskExecutionStatusEventType getType() {
        return type;
    }

    public void setType(TaskExecutionStatusEventType type) {
        this.type = type;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
