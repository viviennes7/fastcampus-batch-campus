package com.fastcampus.batchcampus.application.dormant;

import com.fastcampus.batchcampus.batch.ItemProcessor;
import com.fastcampus.batchcampus.customer.Customer;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PreDormantBatchItemProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer customer) {

        final LocalDate targetDate = LocalDate.now()
                .minusDays(365)
                .plusDays(7);

        if (targetDate.equals(customer.getLoginAt().toLocalDate())) {
            return customer;
        } else {
            return null;
        }

    }

}
