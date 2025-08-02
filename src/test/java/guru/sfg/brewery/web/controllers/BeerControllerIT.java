package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.config.SecurityConfig;
import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BeerControllerIT extends BaseIT {

    @Autowired
    BeerRepository beerRepository;

    @DisplayName("Init New Form")
    @Nested
    class InitNewForm {

        @Test
        void initCreationFormAuth() throws Exception {
            mockMvc.perform(get("/beers/new")
                            .with(httpBasic("spring", "guru")))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/createBeer"))
                    .andExpect(model().attributeExists("beer"));
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamNotAdmin")
        void initCreationFormAuthNotAdmin(String user, String pwd) throws Exception {
            mockMvc.perform(get("/beers/new")
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void initCreationFormNotAuth() throws Exception {
            mockMvc.perform(get("/beers/new"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("Init Find Beer Form")
    @Nested
    class FindBeerForm {
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeersWithAuth(String user, String pwd) throws Exception {
            mockMvc.perform(get("/beers/find").with(httpBasic(user, pwd)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/findBeers"))
                    .andExpect(model().attributeExists("beer"));
        }

        @Test
        void findBeersWithAnonymous() throws Exception{
            mockMvc.perform(get("/beers/find").with(anonymous()))
                    .andExpect(status().isUnauthorized());
        }

    }

    @DisplayName("Process Find Beer Form")
    @Nested
    class ProcessFindForm{
        @Test
        void findBeerForm() throws Exception {
            mockMvc.perform(get("/beers").param("beerName", ""))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeerFormAuth(String user, String pwd) throws Exception {
            mockMvc.perform(get("/beers").param("beerName", "")
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("Get Beer By Id")
    @Nested
    class GetByID {
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void getBeerByIdAUTH(String user, String pwd) throws Exception{
            Beer beer = beerRepository.findAll().getFirst();

            mockMvc.perform(get("/beers/" + beer.getId())
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/beerDetails"))
                    .andExpect(model().attributeExists("beer"));
        }

        @Test
        void getBeerByIdNoAuth() throws Exception{
            Beer beer = beerRepository.findAll().getFirst();

            mockMvc.perform(get("/beers/" + beer.getId()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    void securityConfigIsLoaded() {
        assertNotNull(wac.getBean(SecurityConfig.class));
    }

    @Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/api/v1/beer"))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Delete Beer")
    @Nested
    class DeleteBeer {

        @Test
        void findBeerById() throws Exception {
            mockMvc.perform(get("/api/v1/beer/{beerId}", getTestBeer().getId()))
                    .andExpect(status().isUnauthorized());
        }

        Beer getTestBeer() {
            Random rand = new Random();
            return beerRepository.saveAndFlush(Beer.builder()
                    .beerName("Zombie Dirt")
                    .beerStyle(BeerStyleEnum.IPA)
                    .upc(String.valueOf(rand.nextInt(99999999)))
                    .price(BigDecimal.valueOf(12.99))
                    .minOnHand(250)
                    .quantityToBrew(0)
                    .build());
        }

        @Test
        void testDeleteBeerWithBasicAuth() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/{beerId}", getTestBeer().getId())
                            .with(httpBasic("spring", "guru")))
                    .andExpect(status().isNoContent());
        }

        @Test
        void testDeleteBeerWithBasicAuthUserRole() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/{beerId}", getTestBeer().getId())
                            .with(httpBasic("user", "password")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void testDeleteBeerWithBasicAuthCustomerRole() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/{beerId}", getTestBeer().getId())
                            .with(httpBasic("scott", "tiger")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void testDeleteBeerWithHeaderParams() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/{beerId}", getTestBeer().getId())
                            .header("Api-Key", "spring")
                            .header("Api-Secret", "guru"))
                    .andExpect(status().isNoContent());
        }

        @Test
        void testDeleteBeerWithUrlParams() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/{beerId}", getTestBeer().getId())
                            .param("apiKey", "spring").param("apiSecret", "guru"))
                    .andExpect(status().isNoContent());
        }
    }
}