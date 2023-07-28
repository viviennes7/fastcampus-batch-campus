package com.fastcampus.batchcampus.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@ToString
public class SettleDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    private Long serviceId;

    private Long count;

    private Long fee;

    private LocalDate targetDate;

    public SettleDetail(Long customerId, Long serviceId, Long count, Long fee, LocalDate targetDate) {
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.count = count;
        this.fee = fee;
        this.targetDate = targetDate;
    }
}
