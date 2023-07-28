package com.fastcampus.batchcampus.batch.detail;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class PreSettleDetailWriter implements ItemWriter<Key>, StepExecutionListener {

    private StepExecution stepExecution;

    @Override
    public void write(Chunk<? extends Key> chunk) throws Exception {
        final ConcurrentMap<Key, Long> snapshotMap = (ConcurrentMap<Key, Long>) stepExecution.getExecutionContext().get("snapshots");
        chunk.forEach(key -> {
            snapshotMap.compute(
                    key,
                    (k, v) -> (v == null) ? 1 : v + 1
            );
        });

    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;

        final ConcurrentMap<Key, Long> snapshotMap = new ConcurrentHashMap<>();
        stepExecution.getExecutionContext().put("snapshots", snapshotMap);
    }
}


// JOB -  (Step1, Step2)
// Step1에 StepExecution snapshots 넣었어요
// Step1:StepExecution -> JobExecution

// Step2는 Step1:StepExecution 접근할 수 없음
