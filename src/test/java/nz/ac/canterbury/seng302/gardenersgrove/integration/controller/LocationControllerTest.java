package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;



import nz.ac.canterbury.seng302.gardenersgrove.controller.TestLocationController;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestLocationController.class)
public class LocationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LocationService locationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    @WithMockUser
    void onLocationAutocompletePage_userEntersPartOfAddress_dropDownListShowsAddresses() throws Exception {
        String testPartialAddress = "20 Kirkwood";
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/testLocationAuto")
                        .param("address-input", testPartialAddress)
                .with(csrf()))
                .andExpect(status().isOk());


    }








}
