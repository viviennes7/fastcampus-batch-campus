package com.fastcampus.batchcampus.application.dormant;

import com.fastcampus.batchcampus.customer.Customer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class PreDormantBatchItemProcessorTest {

    private PreDormantBatchItemProcessor preDormantBatchItemProcessor;

    @BeforeEach
    void setup() {
        preDormantBatchItemProcessor = new PreDormantBatchItemProcessor();
    }

    @Test
    @DisplayName("로그인_날짜가_오늘로부터_358일전이면_customer를_반환해야한다.")
    void test1() {

        // given
        final Customer customer = new Customer("minsoo", "minsoo@fastcampus.com");
        // 오늘은 2023.06.04 예정자는 2022.06.11
        customer.setLoginAt(LocalDateTime.now().minusDays(365).plusDays(7));

        // when
        final Customer result = preDormantBatchItemProcessor.process(customer);

        // then
        Assertions.assertThat(result).isEqualTo(customer);
        Assertions.assertThat(result).isNotNull();

    }

    @Test
    @DisplayName("로그인_날짜가_오늘로부터_358일전이_아니면_null을_반환해야한다.")
    void test2() {

        // given
        final Customer customer = new Customer("minsoo", "minsoo@fastcampus.com");

        // when
        final Customer result = preDormantBatchItemProcessor.process(customer);

        // then
        Assertions.assertThat(result).isNull();

    }

}