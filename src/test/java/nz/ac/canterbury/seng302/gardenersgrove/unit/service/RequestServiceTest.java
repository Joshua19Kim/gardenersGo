package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenFormController;
import nz.ac.canterbury.seng302.gardenersgrove.service.RequestService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class RequestServiceTest {

    private static RequestService requestService;

    @BeforeAll
    public static void setUp() {
        requestService = new RequestService();
    }

    @Test
    public void RequestURIRequested_RequestHasContextPathAndParameters_ReturnsRequestWithoutContextPathAndWithParameters() throws Exception {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setContextPath("/test");
        mockHttpServletRequest.setRequestURI("/test/gardens/details");
        mockHttpServletRequest.setQueryString("gardenId=1");



    }
}
