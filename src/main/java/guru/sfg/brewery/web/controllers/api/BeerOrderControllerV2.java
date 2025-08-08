package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.domain.security.Users;
import guru.sfg.brewery.security.perms.BeerOrderCreatePermissionV2;
import guru.sfg.brewery.security.perms.BeerOrderPickupPermissionV2;
import guru.sfg.brewery.security.perms.BeerOrderReadPermissionV2;
import guru.sfg.brewery.services.BeerOrderService;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderPagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static guru.sfg.brewery.web.controllers.api.BeerOrderController.BEER_ORDER_API_V2;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(BEER_ORDER_API_V2)
@RestController
public class BeerOrderControllerV2 implements BeerOrderController {


    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerOrderService beerOrderService;

    @BeerOrderReadPermissionV2
    @GetMapping
    public BeerOrderPagedList listOrders(@AuthenticationPrincipal Users user,
                                         @RequestParam(required = false) Integer pageNumber,
                                         @RequestParam(required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        if(user.getCustomer() != null)
            return beerOrderService.listOrders( user.getCustomer().getId(),
                    PageRequest.of(pageNumber, pageSize));
        else return beerOrderService.listOrders( PageRequest.of(pageNumber, pageSize));
    }

    @BeerOrderCreatePermissionV2
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeerOrderDto placeOrder(@RequestBody BeerOrderDto beerOrderDto) {
        return beerOrderService.placeOrder(beerOrderDto);
    }

    @BeerOrderReadPermissionV2
    @GetMapping(BEER_ORDER_ID_PATH_V2)
    public BeerOrderDto getOrder(@PathVariable UUID orderId) {
        BeerOrderDto beerOrderDto = beerOrderService.getOrderById(orderId);

        if (beerOrderDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found. UUID: " + orderId);
        }
        log.debug("Found Beer Order: " + beerOrderDto);
        return beerOrderDto;
    }

    @BeerOrderPickupPermissionV2
    @PutMapping(BEER_ORDER_PICKUP_PATH_V2)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickupOrder(@PathVariable UUID orderId) {
        beerOrderService.pickupOrder(orderId);
    }
}