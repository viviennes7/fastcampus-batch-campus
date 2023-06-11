package com.fastcampus.batchcampus.application.dormant;

import com.fastcampus.batchcampus.EmailProvider;
import com.fastcampus.batchcampus.batch.JobExecution;
import com.fastcampus.batchcampus.batch.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class DormantBatchJobExecutionListener implements JobExecutionListener {

    private final EmailProvider emailProvider;

    public DormantBatchJobExecutionListener() {
        this.emailProvider = new EmailProvider.Fake();
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        // no-op
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        // 비즈니스 로직
        emailProvider.send(
                "admin@fastcampus.com",
                "배치 완료 알림",
                "DormantBatchJob이 수행되었습니다. status :" + jobExecution.getStatus()
        );
    }

}
