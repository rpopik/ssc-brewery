package guru.sfg.brewery.web.controllers.api;

public interface BeerOrderController {

    String BEER_ORDER_API = "/api/v1/customers/{customerId}";
    String BEER_ORDER_PATH = "orders";
    String BEER_ORDER_ID_PATH = "orders/{orderId}";
    String BEER_ORDER_PICKUP_PATH = "/orders/{orderId}/pickup";

    String BEER_ORDER_API_V2 = "/api/v2/orders/";
    String BEER_ORDER_ID_PATH_V2 = "{orderId}";
    String BEER_ORDER_PICKUP_PATH_V2 = "{orderId}/pickup";

}