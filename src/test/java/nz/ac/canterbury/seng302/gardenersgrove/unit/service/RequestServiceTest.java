package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import nz.ac.canterbury.seng302.gardenersgrove.service.RequestService;
import org.junit.jupiter.api.Assertions;
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

        String actualRequestURI = requestService.getRequestURI(mockHttpServletRequest);
        String expectedRequestURI = "/gardens/details?gardenId=1";

        Assertions.assertEquals(expectedRequestURI, actualRequestURI);

    }

    @Test
    public void RequestURIRequested_NoContextPathAndParameters_ReturnsRequestURI() throws Exception {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setRequestURI("/gardens");

        String actualRequestURI = requestService.getRequestURI(mockHttpServletRequest);
        String expectedRequestURI = "/gardens";

        Assertions.assertEquals(expectedRequestURI, actualRequestURI);

    }
}
