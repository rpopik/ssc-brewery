package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.security.perms.BeerOrderCreatePermission;
import guru.sfg.brewery.security.perms.BeerOrderPickupPermission;
import guru.sfg.brewery.security.perms.BeerOrderReadPermission;
import guru.sfg.brewery.services.BeerOrderService;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderPagedList;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static guru.sfg.brewery.web.controllers.api.BeerOrderController.BEER_ORDER_API;

@RequestMapping(BEER_ORDER_API)

@RestController
public class BeerOrderControllerImpl implements BeerOrderController{
    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerOrderService beerOrderService;

    public BeerOrderControllerImpl(BeerOrderService beerOrderService) {
        this.beerOrderService = beerOrderService;
    }

    @BeerOrderReadPermission
    @GetMapping(BEER_ORDER_PATH)
    public BeerOrderPagedList listOrders(@PathVariable UUID customerId,
                                         @RequestParam(required = false) Integer pageNumber,
                                         @RequestParam(required = false) Integer pageSize){
        if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return beerOrderService.listOrders(customerId, PageRequest.of(pageNumber, pageSize));
    }

    @BeerOrderCreatePermission
    @PostMapping(BEER_ORDER_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    public BeerOrderDto placeOrder(@PathVariable UUID customerId, @RequestBody BeerOrderDto beerOrderDto){
        return beerOrderService.placeOrder(customerId, beerOrderDto);
    }

    @BeerOrderReadPermission
    @GetMapping(BEER_ORDER_ID_PATH)
    public BeerOrderDto getOrder(@PathVariable UUID customerId, @PathVariable UUID orderId){
        return beerOrderService.getOrderById(customerId, orderId);
    }

    @BeerOrderPickupPermission
    @PutMapping(BEER_ORDER_PICKUP_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickupOrder(@PathVariable UUID customerId, @PathVariable UUID orderId){
        beerOrderService.pickupOrder(customerId, orderId);
    }
}