package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class IndexControllerIT extends BaseTest{

    @Test
    void testGetIndexSlash() throws Exception {
        mockMvc.perform(get("/")
                        .with(httpBasic("spring", "guru")))
                .andExpect(status().isOk());
    }
}