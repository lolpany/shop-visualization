package interview.competentum.model.customer;

public class ServicedCustomer extends CustomerDecorator implements Cloneable {
    private int unprocessedGoods;
//    private int queueNumber;

    public ServicedCustomer(Customer customer) {
        super(customer);
        unprocessedGoods = getGoods();
    }

    public int getUnprocessedGoods() {
        return unprocessedGoods;
    }

    public void setUnprocessedGoods(int unprocessedGoods) {
        this.unprocessedGoods = unprocessedGoods;
    }

    public int getQueueNumber() {
        return customer.getQueue();
    }

//    public void setQueueNumber(int queueNumber) {
//        this.queueNumber = queueNumber;
//    }

    public void service(int numberOfGoods) {
        unprocessedGoods = unprocessedGoods - numberOfGoods > 0 ? unprocessedGoods - numberOfGoods : 0;
    }

    public boolean isDone() {
        return unprocessedGoods == 0;
    }

    @Override
    public ServicedCustomer clone() {
        ServicedCustomer result = new ServicedCustomer(customer);
        result.setUnprocessedGoods(unprocessedGoods);
        return result;
    }
}
