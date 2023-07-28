package com.fastcampus.batchcampus.batch.generator;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
@StepScope
public class ApiOrderGenerateReader implements ItemReader<Boolean> {

    private Long totalCount;
    private AtomicLong current;

    public ApiOrderGenerateReader(
            @Value("#{jobParameters['totalCount']}") String totalCount
    ) {
        this.totalCount = Long.parseLong(totalCount);
        this.current = new AtomicLong(0);
    }

    @Override
    public Boolean read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if(current.incrementAndGet() > totalCount)
            return null;

        return true;
    }

}
