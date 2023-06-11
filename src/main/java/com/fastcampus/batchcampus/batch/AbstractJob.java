package com.fastcampus.batchcampus.batch;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class AbstractJob implements Job {

    private final JobExecutionListener jobExecutionListener;

    public AbstractJob(JobExecutionListener jobExecutionListener) {
        this.jobExecutionListener = Objects.requireNonNullElseGet(jobExecutionListener, () -> new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
            }
        });
    }

    @Override
    public JobExecution execute() {
        final JobExecution jobExecution = new JobExecution();
        jobExecution.setStatus(BatchStatus.STARTING);
        jobExecution.setStartTime(LocalDateTime.now());

        jobExecutionListener.beforeJob(jobExecution);

        try {

            doExecute();
            jobExecution.setStatus(BatchStatus.COMPLETED);

        } catch (Exception e) {
            jobExecution.setStatus(BatchStatus.FAILED);
        }

        jobExecution.setEndTime(LocalDateTime.now());

        jobExecutionListener.afterJob(jobExecution);

        return jobExecution;
    }

    public abstract void doExecute();

}
