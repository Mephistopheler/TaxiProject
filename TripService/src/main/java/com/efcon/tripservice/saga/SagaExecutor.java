package com.efcon.tripservice.saga;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@Component
public class SagaExecutor {

    public void execute(String sagaName, List<SagaStep> steps) {
        Deque<SagaStep> completedSteps = new ArrayDeque<>();

        try {
            for (SagaStep step : steps) {
                step.execute();
                completedSteps.push(step);
            }
        } catch (RuntimeException ex) {
            compensate(completedSteps);
            throw new SagaExecutionException("Saga failed: " + sagaName, ex);
        }
    }

    private void compensate(Deque<SagaStep> completedSteps) {
        while (!completedSteps.isEmpty()) {
            try {
                completedSteps.pop().compensate();
            } catch (RuntimeException ignored) {
                // best-effort compensation
            }
        }
    }
}