package interview.competentum.model.customer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import interview.competentum.model.*;

import java.util.Queue;

public class MaleCustomer extends BaseCustomer {

    public MaleCustomer(long id, int goods) {
        super(id, goods);
    }

//    @Override
//    public Queue<Customer> pickQueue() throws interview.competentum.model.IllegalStateException {
//        return MaleQueueAdvisor.getInstance().adviceQueue();
//    }
}
