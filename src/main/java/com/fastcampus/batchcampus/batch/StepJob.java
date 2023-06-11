package com.fastcampus.batchcampus.batch;

import java.util.List;

public class StepJob extends AbstractJob {

    private final List<Step> steps;

    public StepJob(List<Step> steps, JobExecutionListener jobExecutionListener) {
        super(jobExecutionListener);
        this.steps = steps;
    }

    @Override
    public void doExecute() {
        steps.forEach(Step::execute);
    }
}
