package interview.competentum.model.shop;

import interview.competentum.model.customer.*;

import java.util.*;
import java.util.concurrent.*;

public class Shop {
    private static final int CHECKOUT_COUNTER_MAX_PERFORMANCE = 1;
    private CheckoutCounter[] counters;
    private List<ShopListener> listeners;
    private Map<Class<Customer>, QueueAdvisor> queueAdvisors;
    private BlockingQueue<Boolean> generatorQueue;
    private Semaphore startServiceSemaphore;
    private BlockingQueue<Boolean> servicedQueue;
    private CyclicBarrier stepBarrier;
    private CustomerGenerator customerGenerator;
    private GeneratorThread generatorThread;
    private CheckoutCounterServicingThread[] servicingThreads;
//    private int numberOfSteps;
    private int currentStep;


    public Shop(int countersNumber/*, int numberOfSteps*/) {
        if (countersNumber == 0/* || numberOfSteps == 0*/) {
            return;
        }
        listeners = new LinkedList<>();
        this.customerGenerator = new CustomerGenerator(new HashMap<Class<? extends Customer>, Integer>() {{
            put(MaleCustomer.class, 40);
            put(FemaleCustomer.class, 50);
            put(ChildCustomer.class, 10);
        }});
        generatorQueue = new LinkedBlockingQueue<>();
        this.startServiceSemaphore = new Semaphore(0);
        this.servicedQueue = new LinkedBlockingQueue<>();
        try {
            this.servicedQueue.put(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.stepBarrier = new CyclicBarrier(countersNumber, () -> {
            try {
                currentStep++;
                servicedQueue.put(true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        this.counters = new CheckoutCounter[countersNumber];
        this.servicingThreads = new CheckoutCounterServicingThread[countersNumber];
        Random random = new Random();
        for (int i = 0; i < this.counters.length; i++) {
            this.counters[i] = new CheckoutCounter(random.nextInt(CHECKOUT_COUNTER_MAX_PERFORMANCE) + 1);
            this.servicingThreads[i] = new CheckoutCounterServicingThread(this, counters[i]);
            this.servicingThreads[i].start();
        }
        queueAdvisors = new HashMap<>();
        putQueueAdvisor(MaleCustomer.class, new MaleQueueAdvisor(this));
        putQueueAdvisor(FemaleCustomer.class, new FemaleQueueAdvisor(this));
        putQueueAdvisor(ChildCustomer.class, new ChildQueueAdvisor(this));
//        this.numberOfSteps = numberOfSteps;
        this.currentStep = 0;
        this.generatorThread = new GeneratorThread(this);
        this.generatorThread.start();
    }

    public CheckoutCounter[] getCheckoutCounters() {
        return counters;
    }

    public void addListener(ShopListener shopListener) {
        listeners.add(shopListener);
    }

    private void putQueueAdvisor(Class customerClass, QueueAdvisor queueAdvisor) {
        queueAdvisors.put(customerClass, queueAdvisor);
        listeners.add(queueAdvisor);
    }

//    public void start() {
//        generateCustomerAndPutInQueue();
//        for (Thread servicingThread : servicingThreads) {
//            servicingThread.start();
//        }
//    }

    public void nextStep() {
//        generateCustomerAndPutInQueue();
//        if (currentStep < numberOfSteps) {
            generatorQueue.add(true);
//        }
    }

    public void stopThreads() {
        this.generatorThread.interrupt();
        for (CheckoutCounterServicingThread thread: this.servicingThreads) {
            thread.interrupt();
        }
    }

    private void generateCustomerAndPutInQueue() {
        Customer newCustomer = customerGenerator.generateCustomer();
        int pickedQueue = queueAdvisors.get(newCustomer.getClass()).adviceQueue();
        counters[pickedQueue].offerCustomer(newCustomer, pickedQueue);
        for (ShopListener listener : listeners) {
            listener.onEnter(new NewCustomer(newCustomer, pickedQueue));
        }
    }

    public List<ShopListener> getListeners() {
        return listeners;
    }

    public CyclicBarrier getStepBarrier() {
        return stepBarrier;
    }

    private static class GeneratorThread extends Thread {
        Shop shop;

        public GeneratorThread(Shop shop) {
            this.shop = shop;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    shop.servicedQueue.take();
                    shop.generatorQueue.take();
                } catch (InterruptedException e) {
                    return;
                }
                Customer newCustomer = shop.customerGenerator.generateCustomer();
                int pickedQueue = shop.queueAdvisors.get(newCustomer.getClass()).adviceQueue();
                shop.counters[pickedQueue].offerCustomer(newCustomer, pickedQueue);
                for (ShopListener listener : shop.listeners) {
                    listener.onEnter(new NewCustomer(newCustomer, pickedQueue));
                }
                shop.startServiceSemaphore.release(shop.counters.length);
            }
        }
    }

    private static class CheckoutCounterServicingThread extends Thread {
        private Shop shop;
        private CheckoutCounter checkoutCounter;

        public CheckoutCounterServicingThread(Shop shop, CheckoutCounter checkoutCounter) {
            this.shop = shop;
            this.checkoutCounter = checkoutCounter;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    shop.startServiceSemaphore.acquire();
                ServicedCustomer servicedCustomer = checkoutCounter.service();
                    for (ShopListener listener : shop.getListeners()) {
                        listener.onExit(servicedCustomer);
                    }
                    shop.getStepBarrier().await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    return;
                }
            }
        }
    }
}
