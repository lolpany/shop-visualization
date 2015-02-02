package interview.competentum.model.customer;

public class NewCustomer extends CustomerDecorator {
    private int pickedQueue;

    public NewCustomer(Customer customer, int pickedQueue) {
        super(customer);
        this.pickedQueue = pickedQueue;
    }

    public int getPickedQueue() {
        return pickedQueue;
    }
}
