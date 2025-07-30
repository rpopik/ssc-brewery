package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BreweryControllerIT extends BaseIT {

    @Test
    void testGetBreweriesJSONWithCustomerRole() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries")
                        .with(httpBasic("scott", "tiger")))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBreweriesJSONWithADMINRole() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries")
                        .with(httpBasic("spring", "guru")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testGetBreweriesJSONWithUserRole() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries")
                        .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }
    @Test
    void testGetBreweriesJSONWithNotAuthenticated() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries")
                        .with(httpBasic("abcde", "blah")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testListBreweriesWithCustomerRole() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
                        .with(httpBasic("scott", "tiger")))
                .andExpect(status().isOk());
    }

    @Test
    void testListBreweriesWithAdminRole() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
                        .with(httpBasic("spring", "guru")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testListBreweriesWithUserRole() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
                        .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }
    @Test
    void testListBreweriesWithNotAuthenticated() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
                        .with(httpBasic("abcde", "blah")))
                .andExpect(status().isUnauthorized());
    }
}