package com.fastcampus.batchcampus.application.dormant;

import com.fastcampus.batchcampus.EmailProvider;
import com.fastcampus.batchcampus.batch.ItemWriter;
import com.fastcampus.batchcampus.customer.Customer;
import com.fastcampus.batchcampus.customer.CustomerRepository;
import org.springframework.stereotype.Component;

@Component
public class DormantBatchItemWriter implements ItemWriter<Customer> {

    private final CustomerRepository customerRepository;
    private final EmailProvider emailProvider;

    public DormantBatchItemWriter(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.emailProvider = new EmailProvider.Fake();
    }

    @Override
    public void write(Customer item) {
        customerRepository.save(item);
        emailProvider.send(item.getEmail(), "휴먼전환 안내메일입니다.", "내용");
    }

}
