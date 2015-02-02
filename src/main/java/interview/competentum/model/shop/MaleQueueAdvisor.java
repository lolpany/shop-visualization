package interview.competentum.model.shop;

import interview.competentum.model.customer.NewCustomer;
import interview.competentum.model.customer.ServicedCustomer;

public class MaleQueueAdvisor implements QueueAdvisor {
    private static MaleQueueAdvisor instance;
    private Shop shop;
    private ShopSortedMetric maleAdvisorMetric;

    public MaleQueueAdvisor(Shop shop) {
        this.shop = shop;
        maleAdvisorMetric = new ShopSortedMetric(shop, (newCustomer) -> {
            int queuePerformance = shop.getCheckoutCounters()[newCustomer.getPickedQueue()].getPerformance();
            int result = newCustomer.getGoods() / queuePerformance;
            return newCustomer.getGoods() % queuePerformance == 0 ? result : result + 1;
        }, (servicedCustomer) -> 1);
    }

    public int adviceQueue() {
        return maleAdvisorMetric.firstQueue();
    }

    @Override
    public void onEnter(NewCustomer newCustomer) {
        maleAdvisorMetric.onEnter(newCustomer);
    }

    @Override
    public void onExit(ServicedCustomer servicedCustomer) {
        maleAdvisorMetric.onExit(servicedCustomer);
    }
}
