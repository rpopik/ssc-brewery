package guru.sfg.brewery.web.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.brewery.bootstrap.DefaultBreweryLoader;
import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderLineDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
class BeerOrderControllerTestV2IT extends BaseIT implements BeerOrderController {

    String API_ORDERS_URI = BEER_ORDER_API_V2;
    String API_ORDERS_PATH = BEER_ORDER_API_V2 + BEER_ORDER_ID_PATH_V2;
    String API_ORDERS_PICKUP = BEER_ORDER_API_V2 + BEER_ORDER_PICKUP_PATH_V2;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    ObjectMapper objectMapper;

    Customer stPeteCustomer;
    Customer dunedinCustomer;
    Customer keyWestCustomer;
    List<Beer> loadedBeers;

    @BeforeEach
    void setUp() {
        stPeteCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.ST_PETE_DISTRIBUTING).orElseThrow();
        dunedinCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.DUNEDIN_DISTRIBUTING).orElseThrow();
        keyWestCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.KEY_WEST_DISTRIBUTING).orElseThrow();
        loadedBeers = beerRepository.findAll();
    }

    @DisplayName("Create Test")
    @Nested
    class CreateOrderTests {

        @Test
        void createOrderNotAuth() throws Exception {
            BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.getFirst().getId());

            mockMvc.perform(post(BEER_ORDER_API_V2)
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(beerOrderDto)))
                    .andExpect(status().isUnauthorized());
        }

        @WithUserDetails("spring")
        @Test
        void createOrderUserAdmin() throws Exception {
            BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.getFirst().getId());

            mockMvc.perform(post(BEER_ORDER_API_V2)
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(beerOrderDto)))
                    .andExpect(status().isCreated());
        }

        @WithUserDetails(DefaultBreweryLoader.STPETE_USER)
        @Test
        void createOrderUserAuthCustomer() throws Exception {
            BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.getFirst().getId());

            mockMvc.perform(post(BEER_ORDER_API_V2)
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(beerOrderDto)))
                    .andExpect(status().isCreated());
        }

        @WithUserDetails(DefaultBreweryLoader.KEYWEST_USER)
        @Test
        void createOrderUserNOTAuthCustomer() throws Exception {
            BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.getFirst().getId());

            mockMvc.perform(post(BEER_ORDER_API_V2)
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(beerOrderDto)))
                    .andExpect(status().isForbidden());
        }
    }

    @DisplayName("List Orders")
    @Nested
    class ListOrders {
        @Test
        void listOrdersNotAuth() throws Exception {
            mockMvc.perform(get(API_ORDERS_URI))
                    .andExpect(status().isUnauthorized());
        }

        @WithUserDetails("spring")
        @Test
        void listOrdersAdminAuth() throws Exception {
            mockMvc.perform(get(API_ORDERS_URI))
                    .andExpect(status().isOk());
        }

        @WithUserDetails(value = DefaultBreweryLoader.STPETE_USER)
        @Test
        void listOrdersCustomerAuth() throws Exception {
            mockMvc.perform(get(API_ORDERS_URI))
                    .andExpect(status().isOk());
        }

        @WithUserDetails(value = DefaultBreweryLoader.DUNEDIN_USER)
        @Test
        void listOrdersCustomerDunedinAuth() throws Exception {
            mockMvc.perform(get(API_ORDERS_URI))
                    .andExpect(status().isOk());
        }

        @Test
        void listOrdersNoAuth() throws Exception {
            mockMvc.perform(get(API_ORDERS_URI))
                    .andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("Get Order")
    @Nested
    class GetOrder {

        @Transactional
        @Test
        void getByOrderIdNotAuth() throws Exception {
            BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

            mockMvc.perform(get(API_ORDERS_PATH,beerOrder.getId()))
                    .andExpect(status().isUnauthorized());
        }


        @Transactional
        @WithUserDetails("spring")
        @Test
        void getByOrderIdADMIN() throws Exception {
            BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

            mockMvc.perform(get(API_ORDERS_PATH, beerOrder.getId()))
                    .andExpect(status().is2xxSuccessful());
        }

        @Transactional
        @WithUserDetails(DefaultBreweryLoader.STPETE_USER)
        @Test
        void getByOrderIdCustomerAuth() throws Exception {
            BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

            mockMvc.perform(get(API_ORDERS_PATH,   beerOrder.getId()))
                    .andExpect(status().is2xxSuccessful());
        }

        @Transactional
        @WithUserDetails(DefaultBreweryLoader.DUNEDIN_USER)
        @Test
        void getByOrderIdCustomerNOTAuth() throws Exception {
            BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();

            mockMvc.perform(get(API_ORDERS_PATH, beerOrder.getId()))
                    .andExpect(status().isNotFound());
        }
    }

    @DisplayName("Pick Up Order")
    @Nested
    class PickUpOrder {
        @Test
        void pickUpOrderNotAuth() throws Exception {
            BeerOrder beerOrder = getBeerOrder(stPeteCustomer);

            mockMvc.perform(put(API_ORDERS_PICKUP, beerOrder.getId()))
                    .andExpect(status().isUnauthorized());
        }

        @WithUserDetails(value = DefaultBreweryLoader.STPETE_USER)
        @Test
        void pickUpOrderNotAdminUser() throws Exception {
            BeerOrder beerOrder = getBeerOrder(stPeteCustomer);

            mockMvc.perform(put(API_ORDERS_PICKUP, beerOrder.getId()))
                    .andExpect(status().isNoContent());
        }

        @WithUserDetails(value = DefaultBreweryLoader.STPETE_USER)
        @Test
        void pickUpOrderCustomerUserAUTH() throws Exception {
            BeerOrder beerOrder = getBeerOrder(stPeteCustomer);
            mockMvc.perform(put(API_ORDERS_PICKUP, beerOrder.getId()))
                    .andExpect(status().isNoContent());
        }

        @WithUserDetails("spring")
        @Test
        void pickUpOrderAdminUserAUTH() throws Exception {
            BeerOrder beerOrder = getBeerOrder(dunedinCustomer);
            mockMvc.perform(put(API_ORDERS_PICKUP, beerOrder.getId()))
                    .andExpect(status().isNoContent());
        }

        @WithUserDetails(value = DefaultBreweryLoader.DUNEDIN_USER)
        @Test
        void pickUpOrderCustomerUserNOT_AUTH() throws Exception {
            BeerOrder beerOrder = getBeerOrder(stPeteCustomer);
            mockMvc.perform(put(API_ORDERS_PICKUP, beerOrder.getId()))
                    .andExpect(status().isForbidden());
        }
    }

    private BeerOrderDto buildOrderDto(Customer customer, UUID beerId) {
        List<BeerOrderLineDto> orderLines = Arrays.asList(BeerOrderLineDto.builder()
                .id(UUID.randomUUID())
                .beerId(beerId)
                .orderQuantity(5)
                .build());

        return BeerOrderDto.builder()
                .customerId(customer.getId())
                .customerRef("123")
                .orderStatusCallbackUrl("http://example.com")
                .beerOrderLines(orderLines)
                .build();
    }

    private BeerOrder getBeerOrder(Customer customer) {
        return beerOrderRepository.findAll().stream()
                .filter(order -> order.getCustomer().getId().equals(customer.getId()))
                .findFirst()
                .orElseThrow();
    }
}