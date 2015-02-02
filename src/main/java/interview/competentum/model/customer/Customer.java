package interview.competentum.model.customer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Queue;

@JsonTypeInfo(use= JsonTypeInfo.Id.MINIMAL_CLASS, include= JsonTypeInfo.As.PROPERTY, property="clas")
public interface Customer {
    long getId();

    void setId(long id);

    int getQueue();

    void setQueue(int queue);

    int getGoods();
}
