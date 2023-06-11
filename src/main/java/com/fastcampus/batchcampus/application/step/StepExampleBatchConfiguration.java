package com.fastcampus.batchcampus.application.step;

import com.fastcampus.batchcampus.batch.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StepExampleBatchConfiguration {

    @Bean
    public Job stepExampleBatchJob(
            Step step1,
            Step step2,
            Step step3
    ) {
        return new StepJobBuilder()
                .start(step1)
                .next(step2)
                .next(step3)
                .build();
    }

    @Bean
    public Step step1() {
        return new Step(
                () -> System.out.println("step1")
        );
    }

    @Bean
    public Step step2() {
        return new Step(
                () -> System.out.println("step2")
        );
    }

    @Bean
    public Step step3() {
        return new Step(
                () -> System.out.println("step3")
        );
    }

}
