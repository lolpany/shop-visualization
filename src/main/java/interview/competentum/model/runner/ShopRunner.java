package interview.competentum.model.runner;

import interview.competentum.model.customer.CheckoutCounter;
import interview.competentum.model.shop.Shop;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(proxyMode= ScopedProxyMode.TARGET_CLASS, value="session")
public class ShopRunner {
    private Shop shop;
    private int batchSize;
    private int steps;
    private int currentStep;
    private StepDataExtractor stepDataExtractor;

    public ShopRunner(int countersNumber, int steps, int batchSize) {
        this.steps = steps;
        this.batchSize = batchSize;
        this.currentStep = 0;
        Shop shop = new Shop(countersNumber);
        stepDataExtractor = new StepDataExtractor(shop, steps, batchSize);
        shop.addListener(stepDataExtractor);
        this.shop = shop;
    }

    public StepsBatch nextStepsBatch() {
        do {
            shop.nextStep();
            currentStep++;
        } while (currentStep % batchSize != 0 && currentStep < steps);
        StepsBatch result = stepDataExtractor.getStepsBatch();
        if (currentStep == steps) {
            shop.stopThreads();
        }
        return result;
    }

    public CheckoutCounter[] getCheckoutCounters() {
        return shop.getCheckoutCounters();
    }

    public void stopThreads() {
        shop.stopThreads();
    }
}
