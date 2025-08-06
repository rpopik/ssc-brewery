package guru.sfg.brewery.web.controllers.api;

public interface BeerOrderController {

    String BEER_ORDER_API = "/api/v1/customers/{customerId}";
    String BEER_ORDER_PATH = "orders";
    String BEER_ORDER_ID_PATH = "orders/{orderId}";
    String BEER_ORDER_PICKUP_PATH = "orders/{orderId}/pickup";
}