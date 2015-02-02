package interview.competentum.model.shop;

import interview.competentum.model.customer.NewCustomer;
import interview.competentum.model.customer.ServicedCustomer;

import java.util.Random;

public class ChildQueueAdvisor implements QueueAdvisor {
    private static ChildQueueAdvisor instance;
    private Shop shop;
    private Random random;

    public ChildQueueAdvisor(Shop shop) {
        this.shop = shop;
        random = new Random();
    }

//    public static void setInstance(Shop shop) {
//        instance = new ChildQueueAdvisor(shop);
//    }
//
//    public static ChildQueueAdvisor getInstance() throws IllegalStateException {
//        if (instance == null) {
//            throw new IllegalStateException("Child queue advisor uninitialized.");
//        }
//        return instance;
//    }

    public int adviceQueue() {
        return random.nextInt(shop.getCheckoutCounters().length);
    }

    @Override
    public void onEnter(NewCustomer newCustomer) {
        // do nothing
    }

    @Override
    public void onExit(ServicedCustomer exit) {
        // do nothing
    }
}
