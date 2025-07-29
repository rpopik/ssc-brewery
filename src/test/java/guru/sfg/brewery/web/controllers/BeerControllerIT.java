package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.config.SecurityConfig;
import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BeerControllerIT extends BaseTest {

    @Autowired
    BeerRepository beerRepository;


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
    };

    @Test
    void securityConfigIsLoaded() {
        assertNotNull(wac.getBean(SecurityConfig.class));
    }

    @Test
    void initCreationForm() throws Exception {
        mockMvc.perform(get("/beers/new").with(httpBasic("spring", "guru")))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/createBeer"))
                .andExpect(model().attributeExists("beer"));
    }

    @Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/api/v1/beer"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerById() throws Exception {
        mockMvc.perform(get("/api/v1/beer/{beerId}", getTestBeer().getId()))
                .andExpect(status().isOk());
    }

    //    @WithMockUser(value = "spring", password = "guru")
    @Test
    void findBeersWithMockUser() throws Exception {
        mockMvc.perform(get("/beers/find")
                        .with(httpBasic("spring", "guru")))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
    }

    @Test
    void findBeersWithHttpBasic() throws Exception {
        mockMvc.perform(get("/beers/find")
                        .with(httpBasic("user", "password")))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
    }

    @Test
    void initCreationFormWithScott() throws Exception {
        mockMvc.perform(get("/beers/new").with(httpBasic("scott", "tiger")))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/createBeer"))
                .andExpect(model().attributeExists("beer"));
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
        mockMvc.perform(delete("/api/v1/beer/{beerId}",  getTestBeer().getId())
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