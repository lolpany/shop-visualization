package interview.competentum.controller;

import interview.competentum.model.customer.CheckoutCounter;
import interview.competentum.model.runner.ShopRunner;
import interview.competentum.model.runner.StepsBatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
public class ShopController {
    public static final String SHOP_RUNNER = "shopRunner";
    private static final int BATCH_SIZE = 5;
    private ShopRunner shopRunner;

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    @ResponseBody
    public InitialData startShop(@RequestParam("counters") int countersNumber, @RequestParam("steps") int steps, HttpSession httpSession) {
        if (httpSession.getAttribute(SHOP_RUNNER) != null) {
            ((ShopRunner)httpSession.getAttribute(SHOP_RUNNER)).stopThreads();
        }
        shopRunner = new ShopRunner(countersNumber, steps, BATCH_SIZE);
        httpSession.setAttribute(SHOP_RUNNER, shopRunner);
        return new InitialData(shopRunner.getCheckoutCounters(), shopRunner.nextStepsBatch());
    }

    @RequestMapping(value = "/next", method = RequestMethod.GET)
    @ResponseBody
    public StepsBatch nextStep(HttpSession httpSession) {
        return ((ShopRunner)httpSession.getAttribute(SHOP_RUNNER)).nextStepsBatch();
    }

    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    @ResponseBody
    public void stopThreads(HttpSession httpSession) {
        if (httpSession.getAttribute(SHOP_RUNNER) != null) {
            ((ShopRunner)httpSession.getAttribute(SHOP_RUNNER)).stopThreads();
        }
    }

    private static class InitialData {
        private CheckoutCounter[] counters;
        private StepsBatch stepsBatch;

        public InitialData(CheckoutCounter[] counters, StepsBatch stepsBatch) {
            this.counters = counters;
            this.stepsBatch = stepsBatch;
        }

        public CheckoutCounter[] getCounters() {
            return counters;
        }

        public StepsBatch getStepsBatch() {
            return stepsBatch;
        }
    }


}
