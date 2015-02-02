package interview.competentum.model.shop;

import interview.competentum.model.customer.NewCustomer;
import interview.competentum.model.customer.ServicedCustomer;

public class FemaleQueueAdvisor implements QueueAdvisor {
    private static FemaleQueueAdvisor instance;
    private Shop shop;
    private ShopSortedMetric femaleAdvisorMetric;

    public FemaleQueueAdvisor(Shop shop) {
        this.shop = shop;
        this.femaleAdvisorMetric = new ShopSortedMetric(shop, (newCustomer) -> 1, (servicedCustomer) -> servicedCustomer.isDone()? 1 : 0);
    }

//    public static void setInstance(Shop shop) {
//        instance = new FemaleQueueAdvisor(shop);
//    }

//    public static FemaleQueueAdvisor getInstance() throws IllegalStateException {
//        if (instance == null) {
//            throw new IllegalStateException("Female queue advisor uninitialized.");
//        }
//        return instance;
//    }

    public int adviceQueue() {
        return femaleAdvisorMetric.firstQueue();
    }

    @Override
    public void onEnter(NewCustomer newCustomer) {
        femaleAdvisorMetric.onEnter(newCustomer);
    }

    @Override
    public void onExit(ServicedCustomer servicedCustomer) {
        femaleAdvisorMetric.onExit(servicedCustomer);
    }
}
