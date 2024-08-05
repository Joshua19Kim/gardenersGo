package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;


import io.cucumber.java.en.Then;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailUserService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.anyString;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class MockConfigurationSteps {
    @Autowired
    private EmailUserService emailUserService;


    @Then("send an email.")
    public void send_an_email() {
        Mockito.verify(emailUserService, Mockito.times(1)).sendEmail(anyString(), anyString(), anyString());
        Mockito.doNothing().when(emailUserService).sendEmail(anyString(),anyString(),anyString());
    }
}
