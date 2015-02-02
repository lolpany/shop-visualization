package interview.competentum.model.runner;

import interview.competentum.model.shop.Shop;
import interview.competentum.model.shop.ShopListener;
import interview.competentum.model.customer.NewCustomer;
import interview.competentum.model.customer.ServicedCustomer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class StepDataExtractor implements ShopListener {
    private Shop shop;
    private StepsBatch stepsBatch;
    private final int batchSize;
    private CyclicBarrier cyclicBarrier;
    private BlockingQueue<StepsBatch> batchQueue;
    private AtomicInteger onExitCount;
    private final int steps;

    public StepDataExtractor(Shop shop, int steps, int batchSize) {
        this.shop = shop;
        this.steps = steps;
        this.batchSize = batchSize;
        this.stepsBatch = new StepsBatch(shop);
        this.cyclicBarrier = new CyclicBarrier(shop.getCheckoutCounters().length, () -> {
            try {
                int currentStep = onExitCount.get() / shop.getCheckoutCounters().length;
                this.batchQueue.put( new StepsBatch(stepsBatch, currentStep - this.batchSize, currentStep));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        this.batchQueue = new LinkedBlockingQueue<>();
        this.onExitCount = new AtomicInteger();
    }

    @Override
    public void onEnter(NewCustomer newCustomer) {
        stepsBatch.addStep(new StepsBatch.Step(this.shop, newCustomer));
    }

    @Override
    public void onExit(ServicedCustomer servicedCustomer) {
        int currentStep = (onExitCount.incrementAndGet() - 1) / shop.getCheckoutCounters().length;
        if (servicedCustomer != null) {
            stepsBatch.getSteps().get(currentStep).putServicedCustomer(servicedCustomer);
        }
        if ((currentStep + 1) % batchSize == 0 || (currentStep + 1) == steps) {
            try {
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    public StepsBatch getStepsBatch() {
        StepsBatch result = null;
        try {
            result = this.batchQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
