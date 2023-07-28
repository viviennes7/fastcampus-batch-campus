package com.fastcampus.batchcampus.batch.group;

import com.fastcampus.batchcampus.domain.Customer;
import com.fastcampus.batchcampus.domain.repository.CustomerRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Iterator;

@Component
public class SettleGroupReader implements ItemReader<Customer> {

    private final CustomerRepository customerRepository;

    private Iterator<Customer> customerIterator;
    private int pageNo = 0;

    public SettleGroupReader() {
        this.customerRepository = new CustomerRepository.Fake();
        customerIterator = Collections.emptyIterator();
    }

    @Override
    public Customer read() {
        if (customerIterator.hasNext())
            return customerIterator.next();

        customerIterator = customerRepository.findAll(PageRequest.of(pageNo++, 10)).iterator();

        if (!customerIterator.hasNext()) {
            return null;
        }

        return customerIterator.next();
    }

}
