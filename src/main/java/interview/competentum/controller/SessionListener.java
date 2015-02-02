package interview.competentum.controller;

import interview.competentum.model.runner.ShopRunner;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Created by user on 2015-01-13.
 */
public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        // do nothing
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        ((ShopRunner)httpSessionEvent.getSession().getAttribute(ShopController.SHOP_RUNNER)).stopThreads();
    }
}
