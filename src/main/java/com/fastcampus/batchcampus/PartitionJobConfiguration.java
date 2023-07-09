package com.fastcampus.batchcampus;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class PartitionJobConfiguration {

    @Bean
    public Job partitionJob(JobRepository jobRepository, Step managerStep) {
        return new JobBuilder("partitionJob", jobRepository)
                .start(managerStep)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step managerStep(
            JobRepository jobRepository,
            Step step,
            PartitionHandler partitionHandler,
            DataSource dataSource
    ) {
        return new StepBuilder("managerStep", jobRepository)
                .partitioner("delegateStep", new ColumnRangePartitioner(dataSource))
                .step(step)
                .partitionHandler(partitionHandler)
                .build();
    }

    @Bean
    public PartitionHandler partitionHandler(Step step) {
        final TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
        taskExecutorPartitionHandler.setStep(step);
        taskExecutorPartitionHandler.setTaskExecutor(new SimpleAsyncTaskExecutor());
        taskExecutorPartitionHandler.setGridSize(5);
        return taskExecutorPartitionHandler;
    }

    @Bean
    public Step step(
            JobRepository jobRepository,
            JpaPagingItemReader<User> jpaPagingItemReader,
            PlatformTransactionManager platformTransactionManager
    ) {
        return new StepBuilder("step", jobRepository)
                .<User, User>chunk(4, platformTransactionManager)
                .reader(jpaPagingItemReader)
                .writer(result -> log.info(result.toString()))
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<User> itemReader(
            @Value("#{stepExecutionContext[minValue]}") Long minValue,
            @Value("#{stepExecutionContext[maxValue]}") Long maxValue,
            EntityManagerFactory entityManagerFactory
    ) {
        log.info("minValue : {}, maxValue : {}", minValue, maxValue);

        final Map<String, Object> params = new HashMap<>();
        params.put("minValue", minValue);
        params.put("maxValue", maxValue);

        return new JpaPagingItemReaderBuilder<User>()
                .name("itemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(5)
                .queryString("""
                        SELECT u FROM User u
                        WHERE u.id BETWEEN :minValue AND :maxValue
                        """
                )
                .parameterValues(params)
                .build();
    }

}
