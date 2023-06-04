package com.fastcampus.batchcampus.batch;

public interface ItemProcessor<I, O> {

    O process(I item);

}
