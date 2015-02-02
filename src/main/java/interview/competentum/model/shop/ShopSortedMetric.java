package interview.competentum.model.shop;

import interview.competentum.model.customer.NewCustomer;
import interview.competentum.model.customer.ServicedCustomer;

import java.util.Comparator;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;

public class ShopSortedMetric {
//    private Shop shop;
    private SortedSet<QueueMetricValue> backingSet;
    private Function<NewCustomer, Integer> enterValueFunction;
    private Function<ServicedCustomer, Integer> exitValueFunction;

    public ShopSortedMetric(Shop shop, Function<NewCustomer, Integer> enterValueFunction, Function<ServicedCustomer, Integer> exitValueFunction) {
//        this.shop = shop;
        backingSet = new ConcurrentSkipListSet<>(new Comparator<QueueMetricValue>() {
            @Override
            public int compare(QueueMetricValue value, QueueMetricValue otherValue) {
                if (value.getQueueNumber() == otherValue.getQueueNumber()) {
                    return 0;
                } else if (value.getSize() > otherValue.getSize()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        for (int i = 0; i < shop.getCheckoutCounters().length; i++) {
            backingSet.add(new QueueMetricValue(i));
        }
        this.enterValueFunction = enterValueFunction;
        this.exitValueFunction = exitValueFunction;
    }

    public void onEnter(NewCustomer newCustomer) {
        QueueMetricValue queueInfoSearch = new QueueMetricValue(newCustomer.getPickedQueue());
        QueueMetricValue queueInfo= null;
        synchronized (backingSet) {
            for (QueueMetricValue value : backingSet) {
                if (value.equals(queueInfoSearch)) {
                    queueInfo = value;
                    break;
                }
            }
//            queueInfo = backingSet.parallelStream().filter((element) -> element.equals(queueInfoSearch)).findAny().get();
            backingSet.remove(queueInfo);
            backingSet.add(queueInfo.incrementSize(enterValueFunction.apply(newCustomer)));
        }

//        QueueMetricValue queueInfo = backingSet.ceiling(new QueueMetricValue(newCustomer.getPickedQueue()));

    }

    public void onExit(ServicedCustomer servicedCustomer) {
        if (servicedCustomer != null) {
            QueueMetricValue queueInfoSearch = new QueueMetricValue(servicedCustomer.getQueueNumber());
            QueueMetricValue queueInfo = null;
            synchronized (backingSet) {
                for (QueueMetricValue value : backingSet) {
                    if (value.equals(queueInfoSearch)) {
                        queueInfo = value;
                        break;
                    }
                }
//                queueInfo = backingSet.parallelStream().filter((element) -> element.equals(queueInfoSearch)).findAny().get();

//            QueueMetricValue queueInfo = backingSet.ceiling(new QueueMetricValue(servicedCustomer.getQueueNumber()));
                backingSet.remove(queueInfo);
                backingSet.add(queueInfo.decrementSize(exitValueFunction.apply(servicedCustomer)));
            }
        }
    }

    public int firstQueue() {
        return backingSet.first().getQueueNumber();
//        return shop.getCheckoutCounters()[backingSet.first().getQueueNumber()].getQueue();
    }

    private static class QueueMetricValue {
        private int queueNumber;
        private int size;

        private QueueMetricValue(int queueNumber) {
            this.queueNumber = queueNumber;
            this.size = 0;
        }

        public int getQueueNumber() {
            return queueNumber;
        }


        public QueueMetricValue incrementSize(int increment) {
            this.size += increment;
            return this;
        }

        public QueueMetricValue decrementSize(int decrement) {
            this.size -= decrement;
            return this;
        }

        public int getSize() {
            return size;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            QueueMetricValue that = (QueueMetricValue) o;

            if (queueNumber != that.queueNumber) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return queueNumber;
        }
    }
}
