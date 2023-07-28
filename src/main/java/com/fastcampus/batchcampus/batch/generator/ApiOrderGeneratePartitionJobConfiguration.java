package com.fastcampus.batchcampus.batch.generator;

import com.fastcampus.batchcampus.domain.ApiOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@Configuration
@RequiredArgsConstructor
public class ApiOrderGeneratePartitionJobConfiguration {

    /**
     * STEP [Master Step]
     * Work Step1, Work Step2, Work Step3, Work Step4, Work Step5, Work Step6, Work Step7
     */
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job apiOrderGenerateJob(Step managerStep) {
        return new JobBuilder("apiOrderGenerateJob", jobRepository)
                .start(managerStep)
                .incrementer(new RunIdIncrementer())
                .validator(
                        new DefaultJobParametersValidator(
                                new String[]{"targetDate", "totalCount"}, new String[0]
                        )
                )
                .build();
    }

    @Bean
    @JobScope
    public Step managerStep(
            PartitionHandler partitionHandler,
            @Value("#{jobParameters['targetDate']}") String targetDate,
            Step apiOrderGenerateStep
    ) {
        return new StepBuilder("managerStep", jobRepository)
                .partitioner("delegateStep", getPartitioner(targetDate))
                .step(apiOrderGenerateStep)
                .partitionHandler(partitionHandler)
                .build();
    }

    // 매니저 스텝이 워커 스텝을 어떻게 다룰지를 정의
    @Bean
    public PartitionHandler partitionHandler(Step apiOrderGenerateStep) {
        final TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
        taskExecutorPartitionHandler.setStep(apiOrderGenerateStep);
        taskExecutorPartitionHandler.setGridSize(7);
        taskExecutorPartitionHandler.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return taskExecutorPartitionHandler;
    }

    // 워커 스텝을 위해서 StepExecution을 생성하는 인터페이스
    Partitioner getPartitioner(String targetDate) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        final LocalDate date = LocalDate.parse(targetDate, formatter);

        return x -> {
            final Map<String, ExecutionContext> result = new HashMap<>();

            IntStream.range(0, 7)
                    .forEach(it -> {
                        final ExecutionContext value = new ExecutionContext();
                        value.putString("targetDate", date.minusDays(it).format(formatter));
                        result.put("partition" + it, value);
                    });
            return result;
        };
    }

    @Bean
    public Step apiOrderGenerateStep(
            ApiOrderGenerateReader apiOrderGenerateReader,
            ApiOrderGenerateProcessor apiOrderGenerateProcessor
    ) {
        return new StepBuilder("apiOrderGenerateStep", jobRepository)
                .<Boolean, ApiOrder>chunk(5000, platformTransactionManager)
                .reader(apiOrderGenerateReader)
                .processor(apiOrderGenerateProcessor)
                .writer(apiOrderGenerateWriter(null))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<ApiOrder> apiOrderGenerateWriter(
            @Value("#{stepExecutionContext['targetDate']}") String targetDate
    ) {
        final String fileName = targetDate + "_api_orders.csv";

        return new FlatFileItemWriterBuilder<ApiOrder>()
                .name("apiOrderGenerateWriter")
                .resource(new PathResource("src/main/resources/datas/" + fileName))
                .delimited()
                .names("id", "customerId", "url", "state", "createdAt")
                .headerCallback(writer -> writer.write("id,customerId,url,state,createdAt"))
                .build();
    }

}
