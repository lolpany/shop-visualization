package interview.competentum.model.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Queue;

public abstract class CustomerDecorator implements Customer {
    Customer customer;

    public CustomerDecorator(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    @JsonIgnore
    public long getId() {
        return customer.getId();
    }

    public void setId(long id) {
        customer.setId(id);
    }

    @JsonIgnore
    public int getGoods() {
        return customer.getGoods();
    }

    @JsonIgnore
    public int getQueue() {
        return customer.getQueue();
    }

    public void setQueue(int queue) {
        customer.setQueue(queue);
    }

//    public Queue<Customer> pickQueue() throws interview.competentum.model.IllegalStateException {
//        return customer.pickQueue();
//    };
}
