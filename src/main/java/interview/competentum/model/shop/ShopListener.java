package interview.competentum.model.shop;

import interview.competentum.model.customer.NewCustomer;
import interview.competentum.model.customer.ServicedCustomer;
public interface ShopListener {
    void onEnter(NewCustomer newCustomer);
    void onExit(ServicedCustomer servicedCustomer);
}
