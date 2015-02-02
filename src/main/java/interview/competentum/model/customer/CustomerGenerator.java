package interview.competentum.model.customer;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

public class CustomerGenerator {
    private static final int MAX_CUSTOMER_GOODS = 20;
    private RangeMap<Integer, Constructor<? extends Customer>> customerSpace;
    private Random customerRandom;
    private Random goodsRandom;
    private long idCounter;

    public CustomerGenerator(Map<Class<? extends Customer>, Integer> probabilities) {
        customerSpace = TreeRangeMap.create();
        int probabilitySum = 0;
        for (ClassToInstanceMap.Entry<Class<? extends Customer>, Integer> probability : probabilities.entrySet()) {
            try {
                customerSpace.put(Range.closedOpen(probabilitySum, probabilitySum + probability.getValue()), probability.getKey().getConstructor(long.class, int.class));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            probabilitySum += probability.getValue();
        }
        customerRandom = new Random();
        goodsRandom = new Random();
        idCounter = 0;
    }

    public Customer generateCustomer() {
        Customer customer = null;
        try {
            customer = customerSpace.get(customerRandom.nextInt(100)).newInstance(idCounter++, goodsRandom.nextInt(MAX_CUSTOMER_GOODS) + 1);
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return customer;
    }
}
