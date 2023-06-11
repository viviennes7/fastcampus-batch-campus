package com.fastcampus.batchcampus.batch;

import lombok.Builder;

public class TaskletJob extends AbstractJob {

    private final Tasklet tasklet;

    public TaskletJob(Tasklet tasklet) {
        super(null);
        this.tasklet = tasklet;
    }

    @Builder
    public TaskletJob(ItemReader<?> itemReader, ItemProcessor<?, ?> itemProcessor, ItemWriter<?> itemWriter, JobExecutionListener jobExecutionListener) {
        super(jobExecutionListener);
        this.tasklet = new SimpleTasklet(itemReader, itemProcessor, itemWriter);
    }

    @Override
    public void doExecute() {
        tasklet.execute();
    }
}
