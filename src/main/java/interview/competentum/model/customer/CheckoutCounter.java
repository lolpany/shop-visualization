package interview.competentum.model.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Deque;
import java.util.LinkedList;

public class CheckoutCounter {
    private int performance;
    private Deque<Customer> deque;
    @JsonIgnore
    private ServicedCustomer servicedCustomer;

    public CheckoutCounter(int performance) {
        this.performance = performance;
        deque = new LinkedList<>();
    }

    public Deque<Customer> getDeque() {
        return deque;
    }

    public int getPerformance() {
        return performance;
    }

//    public ServicedCustomer getServicedCustomer() {
//        return servicedCustomer;
//    }

    public ServicedCustomer service() {
        if (servicedCustomer == null && deque.peekFirst() == null) {
            return null;
        }
        if (servicedCustomer == null && deque.peekFirst() != null) {
            servicedCustomer = new ServicedCustomer(deque.pollFirst());
        }
        servicedCustomer.service(performance);
        ServicedCustomer result = servicedCustomer.clone();
        if (servicedCustomer.isDone()) {
            servicedCustomer = null;
        }
        return result;
    }

    public void offerCustomer(Customer newCustomer, int queueNumber) {
        newCustomer.setQueue(queueNumber);
        deque.offerLast(newCustomer);
    }
}
