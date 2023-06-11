package com.fastcampus.batchcampus.batch;

import lombok.Builder;

public class Step {

    private final Tasklet tasklet;

    public Step(Tasklet tasklet) {
        this.tasklet = tasklet;
    }

    @Builder
    public Step(ItemReader<?> itemReader, ItemProcessor<?, ?> itemProcessor, ItemWriter<?> itemWriter) {
        this.tasklet = new SimpleTasklet(itemReader, itemProcessor, itemWriter);
    }

    public void execute() {
        tasklet.execute();
    }
}
