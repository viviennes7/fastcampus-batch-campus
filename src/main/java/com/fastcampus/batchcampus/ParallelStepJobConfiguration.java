package com.fastcampus.batchcampus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
//@Configuration
public class ParallelStepJobConfiguration {

    // flow1 (step1, step2)
    //                              -> step4
    //     flow2 (step3)


    @Bean
    public Job job(JobRepository jobRepository, Step step4, Flow splitFlow) {
        return new JobBuilder("job", jobRepository)
                .start(splitFlow)
                .next(step4)
                .build()
                .build();
    }

    @Bean
    public Flow splitFlow(Flow flow1, Flow flow2) {
        return new FlowBuilder<SimpleFlow>("splitFlow")
                .split(new SimpleAsyncTaskExecutor())
                .add(flow1, flow2)
                .build();
    }

    @Bean
    public Flow flow1(Step step1, Step step2) {
        return new FlowBuilder<SimpleFlow>("flow1")
                .start(step1)
                .next(step2)
                .build();
    }

    @Bean
    public Flow flow2(Step step3) {
        return new FlowBuilder<SimpleFlow>("flow2")
                .start(step3)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet((a, b) -> {
                    Thread.sleep(1000);
                    log.info("step1");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("step2", jobRepository)
                .tasklet((a, b) -> {
                    Thread.sleep(2000);
                    log.info("step2");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step step3(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("step3", jobRepository)
                .tasklet((a, b) -> {
                    Thread.sleep(2500);
                    log.info("step3");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step step4(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("step4", jobRepository)
                .tasklet((a, b) -> {
                    Thread.sleep(1000);
                    log.info("step1");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

}
