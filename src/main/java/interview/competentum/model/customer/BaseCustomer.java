package interview.competentum.model.customer;

public abstract class BaseCustomer implements Customer {
    private long id;
    private int goods;

    private int queue;

    public BaseCustomer(long id, int goods) {
        this.id = id;
        this.goods = goods;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int getGoods() {
        return goods;
    }

    @Override
    public int getQueue() {
        return queue;
    }

    @Override
    public void setQueue(int queue) {
        this.queue = queue;
    }
}
