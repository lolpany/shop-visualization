function App() {
    this.BATCH_SIZE = 5;
    this.PRELOAD_STEPS_NUMBER = Math.floor(this.BATCH_SIZE/2) + 1;
    this.currentStep = 0;
    this.countersNumber = 0;
    this.stepsNumber = 0;
    this.steps = [];
    this.loadedBatches = 0;
}

App.prototype.drawInput = function() {
    var COUNTERS_TIP = 'enter number of checkout counters (>0)';
    var STEPS_TIP = 'enter number of steps (>0)';

    var self = this;
    $.ajax({
        type: 'POST',
        url: '../shop/stop'
    });
    var input = $(document.createElement('form')).addClass('inputDiv');
    $('#app').append(input);
    var counters = $(document.createElement('input')).attr('id', 'counters').addClass('input').val(COUNTERS_TIP).addClass('tip');
    counters.focus(function() {
        if ($(this).val() == COUNTERS_TIP) {
            $(this).val('');
            $(this).toggleClass('tip')
        }
    });
    counters.blur(function() {
        if ($(this).val() == '') {
            $(this).val(COUNTERS_TIP);
            $(this).toggleClass('tip')
        } else {
            if (isNaN(parseInt($(this).val()))) {
                $(this).addClass('error');
            } else {
                $(this).removeClass('error');
            }
        }
    });
    input.append(counters);
    var steps = $(document.createElement('input')).attr('id', 'steps').addClass('input').val(STEPS_TIP).addClass('tip');
    steps.focus(function() {
        if ($(this).val() == STEPS_TIP) {
            $(this).val('');
            $(this).toggleClass('tip')
        }
    });
    steps.blur(function() {
        if ($(this).val() == '') {
            $(this).val(STEPS_TIP);
            $(this).toggleClass('tip')
        } else {
            if (isNaN(parseInt($(this).val()))) {
                $(this).addClass('error');
            } else {
                $(this).removeClass('error');
            }
        }
    });
    input.append(steps);
    input.append($(document.createElement('input')).attr('id', 'go').addClass('input')
        .attr('type', 'button').attr('value', 'go')
        .click(function() {
            self.countersNumber = parseInt($('#counters').val());
            self.stepsNumber = parseInt($('#steps').val());
            if (isNaN(self.stepsNumber) || isNaN(self.countersNumber) || self.stepsNumber < 1 || self.countersNumber < 1) {
                return;
            }
            self.go(self.countersNumber, self.stepsNumber);
    }));
};

App.prototype.go = function(counters, steps) {
    var self = this;
    $.ajax({
        type: 'POST',
        url: '../shop/start?counters=' + counters + '&steps=' + steps,
        dataType: 'json',
        success: function(data) {
            self.loadedBatches++;
            data.stepsBatch.steps.forEach(function(element) {
                self.steps.push(element);
            });

            //self.drawBackground();
            self.shop = new Shop(data.counters, self.stepsNumber, self.drawBackground());
            $.proxy(self.shop.applyStep, self.shop)(self.steps[self.currentStep]);
            //$.proxy(self.shop.redraw, self.shop)();
        }
    });
};

App.prototype.drawBackground = function() {
    var self = this;

    $('#app').empty();

    var svg = d3.select('#app').append('svg')
        .attr('width', '100%')
        .attr('height', '100%')
        .attr('id','shop');

    svg.append('rect')
        //.attr('id','shop')
        .classed('background', true)
        .attr('width', '100%')
        .attr('height', '100%')
        .attr('fill', d3.rgb(43, 43, 43));

    $('#app').click(function() {
        self.nextStep();
    });

    //todo on key
    $(document).on('keydown', function(e) {
        var tag = e.target.tagName.toLowerCase();
        if ( e.which === 32/* && tag != 'input' && tag != 'textarea'*/)
            self.nextStep();
    });

    return '#shop';
};

App.prototype.nextStep = function() {
    this.currentStep++;
    if (this.currentStep >= this.stepsNumber || (this.steps[this.currentStep] == null && this.currentStep < this.stepsNumber)) {
        return;
    }
    if (this.loadedBatches * this.BATCH_SIZE < this.stepsNumber
        && (this.currentStep + this.PRELOAD_STEPS_NUMBER) % this.BATCH_SIZE == 0) {
        var self = this;
        $.ajax({
            type: 'GET',
            url: '../shop/next',
            dataType: 'json',
            success: function(data) {
                self.loadedBatches++;
                data.steps.forEach(function(element) {
                    self.steps.push(element);
                });
            }
        });
    }
    $.proxy(this.shop.applyStep, this.shop)(this.steps[this.currentStep]);
}

function Shop(counters, steps, htmlElementId) {
    var self = this;
    this.htmlElementId = htmlElementId;
    //this.newCustomerQueue = null;
    this.servicedCustomers = [];
    this.CUSTOMER_GRAPHICS_MAP = {
        '.MaleCustomer': function(x, y) {
            return Math.round(x - self.STYLE.CUSTOMER_WIDTH/2) + ',' + Math.round(y - self.STYLE.CUSTOMER_WIDTH/2)
                + ' ' + Math.round(x + self.STYLE.CUSTOMER_WIDTH/2) + ',' + Math.round(y - self.STYLE.CUSTOMER_WIDTH/2)
                + ' ' + Math.round(x) + ',' + Math.round(y + self.STYLE.CUSTOMER_WIDTH/2);
        },
        '.FemaleCustomer': function(x, y) {
            return Math.round(x - self.STYLE.CUSTOMER_WIDTH/2) + ',' + Math.round(y + self.STYLE.CUSTOMER_WIDTH/2)
                + ' ' + Math.round(x) + ',' + Math.round(y - self.STYLE.CUSTOMER_WIDTH/2)
                + ' ' + Math.round(x + self.STYLE.CUSTOMER_WIDTH/2) + ',' + Math.round(y + self.STYLE.CUSTOMER_WIDTH/2);
        },
        '.ChildCustomer': function(x, y) {
            return Math.round(x - self.STYLE.CUSTOMER_WIDTH/2) + ',' + Math.round(y)
                + ' ' + Math.round(x) + ',' + Math.round(y - self.STYLE.CUSTOMER_WIDTH/2)
                + ' ' + Math.round(x+ self.STYLE.CUSTOMER_WIDTH/2) + ',' + Math.round(y)
                + ' ' + Math.round(x) + ',' + Math.round(y + self.STYLE.CUSTOMER_WIDTH/2);
        }
    };

    this.STYLE= {};
    this.STYLE.WIDTH = 100;
    this.STYLE.PERFORMANCE_SIZE = 2*this.STYLE.WIDTH/3;
    this.STYLE.MARGIN = this.STYLE.PERFORMANCE_SIZE/2 +this.STYLE.WIDTH/6;
    this.STYLE.CUSTOMER_WIDTH = this.STYLE.WIDTH * 0.8;
    //this.CUSTOMER_WIDTH = 25;
    this.STYLE.CUSTOMER_MARGIN = this.STYLE.WIDTH * 0.1;
    this.cutomerCounter = 0;
    this.queues = [];
    var i = 0;
    for (i < 0; i < counters.length; i++) {
        this.queues[i] = {};
        this.queues[i].id = i;
        this.queues[i].performance = counters[i].performance;
        this.queues[i].customers = [];
    }

    var queues = d3.select(htmlElementId).selectAll('g.queue')
        .data(this.queues);
    queues
        .enter()
        .append('g')
        .classed('queue', true)
        .append('rect')
        .classed('queue',true)
        .attr('x', function(d, i) {
            return self.STYLE.MARGIN +  i * (self.STYLE.WIDTH + self.STYLE.MARGIN)
        })
        .attr('y', self.STYLE.MARGIN)
        .attr('width',self.STYLE.WIDTH)
        .attr('height', this.STYLE.WIDTH)
        .attr('fill', 'rgb(169, 183, 198)');
    queues
        .append('circle')
        .attr('cx',function(d, i) {
            return self.STYLE.MARGIN + self.STYLE.WIDTH +  i * (self.STYLE.WIDTH + self.STYLE.MARGIN)
        })
        .attr('cy',self.STYLE.MARGIN)
        .attr('r',self.STYLE.PERFORMANCE_SIZE/2);
    queues
        .append('text')
        .classed('performance', true)
        .text(function(queue) {
            return queue.performance;
        })
        .attr('x',function(d, i) {
            return self.STYLE.MARGIN + self.STYLE.WIDTH +  i * (self.STYLE.WIDTH + self.STYLE.MARGIN)
        })
        .attr('y', self.STYLE.MARGIN + self.STYLE.PERFORMANCE_SIZE/3)
        .attr('text-anchor', 'middle')
        .attr('font-size', self.STYLE.PERFORMANCE_SIZE);
}

Shop.prototype.applyStep = function(step) {
    //if (this.newCustomerQueue != null) {
    //    var cus = this.queues[this.newCustomerQueue].customers[this.queues[this.newCustomerQueue].customers.length -1];
    //    if (cus.clas != '.ServicedCustomer')
    //        this.queues[this.newCustomerQueue].customers[this.queues[this.newCustomerQueue].customers.length -1] = cus.customer;
    //}
    var newCustomer = step.newCustomer;
    //this.newCustomerQueue = newCustomer.pickedQueue;
    //newCustomer.position = this.queues[newCustomer.pickedQueue].customers.length;
    this.queues[newCustomer.pickedQueue].customers.push(newCustomer/*.customer*/);

    //for (var customerNumber = 0; customerNumber < this.servicedCustomers.length; customerNumber++) {
    for (var customerNumber in this.servicedCustomers) {
        var customer = this.servicedCustomers[customerNumber];
        this.queues[customer.queueNumber].customers.shift();
        //for (var customerNumber in this.queues[customer.queueNumber].customers) {
        //    this.queues[customer.queueNumber].customers[customerNumber].position--;
        //}
    }
    this.servicedCustomers = [];
    //for (var servicedCustomerNumber = 0; servicedCustomerNumber < step.servicedCustomers.length; servicedCustomerNumber++) {
    for (var servicedCustomerNumber in step.servicedCustomers) {
        var servicedCustomer = step.servicedCustomers[servicedCustomerNumber];
        servicedCustomer.position = 0;
        this.queues[servicedCustomer.queueNumber].customers[0] = servicedCustomer;
        if (servicedCustomer.done) {
            this.servicedCustomers.push(servicedCustomer);
        }
    }
    this.redraw();
};

Shop.prototype.redraw = function() {
    var self = this;

    var queues = d3.selectAll(this.htmlElementId).selectAll('g.queue');

    var maxQueueLength = 0;
    self.queues.forEach(function(queue) {
        maxQueueLength = Math.max(maxQueueLength, queue.customers.length);
    });

    d3.select('#app').select('svg')
        .attr('width', function() {
            return self.queues.length * (self.STYLE.WIDTH + self.STYLE.MARGIN)+ self.STYLE.MARGIN;
        })
        .attr('height', function() {
         return maxQueueLength * (self.STYLE.CUSTOMER_WIDTH + self.STYLE.CUSTOMER_MARGIN) + self.STYLE.CUSTOMER_MARGIN + 2* self.STYLE.MARGIN;
    });

    queues.selectAll('rect.queue')
        .attr('height', function(queue) {
            return queue.customers.length * (self.STYLE.CUSTOMER_WIDTH + self.STYLE.CUSTOMER_MARGIN) + self.STYLE.CUSTOMER_MARGIN;
        });

    var customers = queues
        .selectAll('g.customer')
        .data(function(queue) {
            return queue.customers;
        }, function(customer, i) {
            return customer.customer == null ? customer.id : customer.customer.id;
        });
    customers.exit().remove();
    var customersEnter = customers.enter();
    var newCustomersGroups = customersEnter
        //.enter()
        .append('g')
        .classed('customer', true);
    newCustomersGroups.append('polyline')
        .classed('customer', true)
        .attr('points', function(customer, i) {
            //var customer = d3.select(this.parentNode).datum();
            //var i = d3.select(this.parentNode).datum().position;
            var cl = customer.customer == null ? customer.clas : customer.customer.clas;
            return self.CUSTOMER_GRAPHICS_MAP[cl](
                self.STYLE.MARGIN + d3.select(this.parentNode.parentNode).datum().id * (self.STYLE.WIDTH + self.STYLE.MARGIN) + self.STYLE.WIDTH/2,
                self.STYLE.MARGIN + self.STYLE.CUSTOMER_WIDTH/2 + self.STYLE.CUSTOMER_MARGIN + i * (self.STYLE.CUSTOMER_MARGIN + self.STYLE.CUSTOMER_WIDTH)
            );
        });
    newCustomersGroups
        .append('text')
        .classed('goods', true)
        .text(function(customer) {
            return customer.customer == null ? customer.goods : customer.unprocessedGoods == null ? customer.goods : customer.unprocessedGoods;
        })
        .attr('x',function(d, i) {
            return self.STYLE.MARGIN + d3.select(this.parentNode.parentNode).datum().id * (self.STYLE.WIDTH + self.STYLE.MARGIN) + self.STYLE.WIDTH/2;
        })
        .attr('y', function(d, i) {
            return self.STYLE.MARGIN + self.STYLE.CUSTOMER_WIDTH/2 + self.STYLE.CUSTOMER_WIDTH/4 + self.STYLE.CUSTOMER_MARGIN + i * (self.STYLE.CUSTOMER_MARGIN + self.STYLE.CUSTOMER_WIDTH);
        })
        .attr('text-anchor', 'middle')
        .attr('font-size', self.STYLE.CUSTOMER_WIDTH * 3 /4);
    customers.selectAll('.new').remove();
    customers.selectAll('.newText').remove();
    newCustomersGroups
        .append('rect')
        .classed('new', true)
        .attr('x',function(d, i) {
            return self.STYLE.MARGIN + d3.select(this.parentNode.parentNode).datum().id * (self.STYLE.WIDTH + self.STYLE.MARGIN) + self.STYLE.WIDTH/2;
        })
        .attr('y', function(d, i) {
            return self.STYLE.MARGIN + self.STYLE.CUSTOMER_MARGIN + i * (self.STYLE.CUSTOMER_MARGIN + self.STYLE.CUSTOMER_WIDTH);
        })
        .attr('height', self.STYLE.CUSTOMER_WIDTH / 4)
        .attr('width',self.STYLE.CUSTOMER_WIDTH / 2);
    newCustomersGroups
        .append('text')
        .classed('newText', true)
        .text('new')
        .attr('x',function(d, i) {
            return self.STYLE.MARGIN + self.STYLE.CUSTOMER_WIDTH /4 + d3.select(this.parentNode.parentNode).datum().id * (self.STYLE.WIDTH + self.STYLE.MARGIN) + self.STYLE.WIDTH/2;
        })
        .attr('y', function(d, i) {
            return self.STYLE.MARGIN + self.STYLE.CUSTOMER_MARGIN + self.STYLE.CUSTOMER_WIDTH/5 + i * (self.STYLE.CUSTOMER_MARGIN + self.STYLE.CUSTOMER_WIDTH);
        })
        .attr('text-anchor', 'middle')
        .attr('font-size', self.STYLE.CUSTOMER_WIDTH /4);
    customers
        .select('polyline')
        .attr('points', function(customer, i) {
            var cl = customer.customer == null ? customer.clas : customer.customer.clas;
            return self.CUSTOMER_GRAPHICS_MAP[cl](
                self.STYLE.MARGIN + d3.select(this.parentNode.parentNode).datum().id * (self.STYLE.WIDTH + self.STYLE.MARGIN) + self.STYLE.WIDTH/2,
                self.STYLE.MARGIN + self.STYLE.CUSTOMER_WIDTH/2 + self.STYLE.CUSTOMER_MARGIN + i * (self.STYLE.CUSTOMER_MARGIN + self.STYLE.CUSTOMER_WIDTH)
            );
        });
    customers
        .select('text')
        .text(function(customer) {
            return customer.unprocessedGoods != null ? customer.unprocessedGoods : customer.customer != null ?  customer.customer.goods : customer.goods;
        })
        .attr('x',function(d, i) {
            return self.STYLE.MARGIN + d3.select(this.parentNode.parentNode).datum().id * (self.STYLE.WIDTH + self.STYLE.MARGIN) + self.STYLE.WIDTH/2;
        })
        .attr('y', function(d, i) {
            return self.STYLE.MARGIN + self.STYLE.CUSTOMER_WIDTH/2 + self.STYLE.CUSTOMER_WIDTH/4 + self.STYLE.CUSTOMER_MARGIN + i * (self.STYLE.CUSTOMER_MARGIN + self.STYLE.CUSTOMER_WIDTH);
        });
};

$().ready(function() {
    (new App()).drawInput();
});