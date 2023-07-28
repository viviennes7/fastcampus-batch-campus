package com.fastcampus.batchcampus.batch.support;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DateFormatJobParametersValidator implements JobParametersValidator {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final String[] names;

    public DateFormatJobParametersValidator(String[] names) {
        this.names = names;
    }

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        for (String name : names) {
            validateDateFormat(parameters, name);
        }
    }

    private void validateDateFormat(JobParameters parameters, String name) throws JobParametersInvalidException {
        try {
            final String string = parameters.getString(name);
            LocalDate.parse(Objects.requireNonNull(string), dateTimeFormatter);
        } catch (Exception e) {
            throw new JobParametersInvalidException("yyyyMMdd 형식만을 지원합니다.");
        }
    }


}
