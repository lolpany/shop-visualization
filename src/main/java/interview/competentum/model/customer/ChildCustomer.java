package interview.competentum.model.customer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Queue;

public class ChildCustomer extends BaseCustomer {

    public ChildCustomer(long id, int goods) {
        super(id, goods);
    }

//    @Override
//    public Queue<Customer> pickQueue() throws interview.competentum.model.IllegalStateException {
//        return null;
//    }
}
