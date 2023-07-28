package com.fastcampus.batchcampus.batch.detail;

import com.fastcampus.batchcampus.domain.ServicePolicy;
import com.fastcampus.batchcampus.domain.SettleDetail;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class SettleDetailProcessor implements ItemProcessor<KeyAndCount, SettleDetail>, StepExecutionListener {

    private final DateTimeFormatter dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyyMMdd");
    private StepExecution stepExecution;

    @Override
    public SettleDetail process(KeyAndCount item) throws Exception {
        final Key key = item.key();
        final ServicePolicy servicePolicy = ServicePolicy.findById(key.serviceId());
        final Long count = item.count();

        final String targetDate = stepExecution.getJobParameters().getString("targetDate");

        return new SettleDetail(
                key.customerId(),
                key.serviceId(),
                count,
                servicePolicy.getFee() * count,
                LocalDate.parse(targetDate, dateTimeFormatter)
        );
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }
}
