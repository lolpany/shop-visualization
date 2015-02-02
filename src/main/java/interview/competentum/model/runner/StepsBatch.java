package interview.competentum.model.runner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import interview.competentum.model.shop.Shop;
import interview.competentum.model.customer.NewCustomer;
import interview.competentum.model.customer.ServicedCustomer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StepsBatch {
    private List<Step> steps;

    public StepsBatch(Shop shop) {
        this.steps = new ArrayList<>(shop.getCheckoutCounters().length);
    }

    public StepsBatch(StepsBatch stepsBatch, int from , int to) {
        this.steps = new ArrayList<>(stepsBatch.steps.subList(from, to));
    }

    public void addStep(Step step) {
        this.steps.add(step);
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

//    @JsonIgnore
//    public Step getLastStep() {
//        return steps.get(steps.size() - 1);
//    }

    public static class Step {
        private NewCustomer newCustomer;
        private Map<Integer, ServicedCustomer> servicedCustomers;

        public Step(Shop shop, NewCustomer newCustomer) {
            this.newCustomer = newCustomer;
            this.servicedCustomers = new ConcurrentHashMap<>(shop.getCheckoutCounters().length);
        }

        public void putServicedCustomer(ServicedCustomer servicedCustomer) {
            this.servicedCustomers.put(servicedCustomer.getQueueNumber(), servicedCustomer);
        }

        public NewCustomer getNewCustomer() {
            return newCustomer;
        }

        public void setNewCustomer(NewCustomer newCustomer) {
            this.newCustomer = newCustomer;
        }

        public Map<Integer, ServicedCustomer> getServicedCustomers() {
            return servicedCustomers;
        }

        public void setServicedCustomers(Map<Integer, ServicedCustomer> servicedCustomers) {
            this.servicedCustomers = servicedCustomers;
        }
    }
}
