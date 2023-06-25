package com.fastcampus.batchcampus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class JobConfiguration {

    @Bean
    public Job job(JobRepository jobRepository, Step step) {
        return new JobBuilder("job", jobRepository)
                .start(step)
                .preventRestart()
                .incrementer(new RunIdIncrementer())
                .validator(new DefaultJobParametersValidator(new String[]{"name"}, new String[]{"age"}))
                .listener(new AnnotationJobExecutionListener())
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("step", jobRepository)
                .tasklet((a, b) -> {
                    log.info("step 실행");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    public static class CustomJobParametersValidator implements JobParametersValidator {

        @Override
        public void validate(JobParameters parameters) throws JobParametersInvalidException {
            if (parameters.getString("name") == null) {
                throw new JobParametersInvalidException("name이 필요해요!");
            }
        }

    }

    @Slf4j
    public static class CustomJobExecutionListener implements JobExecutionListener {

        @Override
        public void beforeJob(JobExecution jobExecution) {
            log.info("beforeJob 입니다");
            JobExecutionListener.super.beforeJob(jobExecution);
        }

        @Override
        public void afterJob(JobExecution jobExecution) {
            if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                log.info("afterJob 입니다.");
                JobExecutionListener.super.afterJob(jobExecution);
            }
        }
    }

    public static class AnnotationJobExecutionListener {

        @BeforeJob
        public void before() {
            log.info("annotation before 입니다.");

        }

        @AfterJob
        public void after() {
            log.info("annotation after 입니다.");
        }

    }

}
