package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(SecurityConfig.class)
public class BeerControllerIT extends BaseTest {

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
        mockMvc.perform(get("/api/v1/beer/{beerId}", "123e4567-e89b-12d3-a456-426614174000"))
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
        mockMvc.perform(delete("/api/v1/beer/123e4567-e89b-12d3-a456-426614174000")
                        .with(httpBasic("spring", "guru")))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteBeerWithBasicAuthUserRole() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/123e4567-e89b-12d3-a456-426614174000")
                        .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteBeerWithBasicAuthCustomerRole() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/123e4567-e89b-12d3-a456-426614174000")
                        .with(httpBasic("scott", "tiger")))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteBeerWithHeaderParams() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/123e4567-e89b-12d3-a456-426614174000")
                        .header("Api-Key", "spring")
                        .header("Api-Secret", "guru"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteBeerWithUrlParams() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/123e4567-e89b-12d3-a456-426614174000")
                        .param("apiKey", "spring").param("apiSecret", "guru"))
                .andExpect(status().isNoContent());
    }
}