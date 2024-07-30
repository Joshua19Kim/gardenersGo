package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.controller.ForgotPasswordFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import nz.ac.canterbury.seng302.gardenersgrove.util.WriteEmail;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import nz.ac.canterbury.seng302.gardenersgrove.controller.LoginController;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
public class ResetPasswordFeature {

    @Mock
    private GardenerFormService gardenerFormService;
    @Mock
    private TokenService tokenService;
    @Mock
    private EmailUserService emailUserService;
    @Mock
    private WriteEmail writeEmail;

    private MockMvc mockMvcLogin;
    private MockMvc mockMvcForgotPassword;
    private MvcResult mvcResult;
    private String email;
    private Gardener gardener;

    @Before("@U16")
    public void setUp() {
        gardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");

        gardenerFormService = Mockito.mock(GardenerFormService.class);
        tokenService = Mockito.mock(TokenService.class);
        emailUserService = Mockito.mock(EmailUserService.class);
        writeEmail = Mockito.mock(WriteEmail.class);

        LoginController loginController = new LoginController();
        ForgotPasswordFormController forgotPasswordFormController = new ForgotPasswordFormController(
                gardenerFormService, tokenService, emailUserService, writeEmail);

        mockMvcLogin = MockMvcBuilders.standaloneSetup(loginController).build();
        mockMvcForgotPassword = MockMvcBuilders.standaloneSetup(forgotPasswordFormController).build();
    }

    @Given("I am on the login page")
    public void i_am_on_the_login_page() throws Exception {
        mockMvcLogin
                .perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginForm"));
    }

    @When("I hit the forgot your password link")
    public void i_hit_the_forgot_your_password_link() throws Exception {
        mvcResult = mockMvcForgotPassword
                .perform(MockMvcRequestBuilders.get("/forgotPassword"))
                .andReturn();
    }

    @Then("I am taken to the forgot your password page")
    public void i_am_taken_to_the_forgot_your_password_page() {
        ModelAndView modelAndView = mvcResult.getModelAndView();
        assert modelAndView != null;
        assertEquals(modelAndView.getViewName(), "forgotPasswordForm");
    }

    @Given("I am on the forgot your password page")
    public void i_am_on_the_forgot_your_password_page() throws Exception {
        mockMvcForgotPassword
                .perform(MockMvcRequestBuilders.get("/forgotPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("forgotPasswordForm"));
    }

    @Given("I enter the empty or malformed email address {string}")
    public void i_enter_the_empty_or_malformed_email_address(String malformedEmail) {
        email = malformedEmail;
    }

    @When("I submit the invalid email")
    public void i_submit_the_invalid_email() throws Exception {
        mvcResult = mockMvcForgotPassword.perform(MockMvcRequestBuilders.post("/forgotPassword")
                        .param("email", email))
                .andReturn();
    }

    @Then("an error message says {string}")
    public void an_error_message_says(String errorMessage) {
        MockHttpServletRequest request = mvcResult.getRequest();
        String emailError = (String) request.getAttribute("emailError");
        assertEquals(errorMessage, emailError);
    }

    @Given("I enter a valid email that is not known to the system")
    public void i_enter_a_valid_email_that_is_not_known_to_the_system() {
        email = "unknown@gmail.com";
    }

    @When("I submit the valid email that is not known to the system")
    public void i_submit_the_valid_email_that_is_not_known_to_the_system() throws Exception {
        mvcResult = mockMvcForgotPassword.perform(MockMvcRequestBuilders.post("/forgotPassword")
                        .param("email", email))
                .andReturn();
    }

    @Then("a confirmation message tells me {string}")
    public void a_confirmation_message_tells_me(String confirmationMessage) {
        MockHttpServletRequest request = mvcResult.getRequest();
        String returnMessage = (String) request.getAttribute("returnMessage");
        assertEquals(confirmationMessage, returnMessage);
    }

    @Given("I enter an email that is known to the system")
    public void i_enter_an_email_that_is_known_to_the_system() {
        email = "testgardener@gmail.com";
        when(gardenerFormService.findByEmail(email)).thenReturn(Optional.of(gardener));
    }

    @When("I submit the email that is known to the system")
    public void i_submit_the_email_that_is_known_to_the_system() throws Exception {
        mvcResult = mockMvcForgotPassword.perform(MockMvcRequestBuilders.post("/forgotPassword")
                        .param("email", email))
                .andReturn();
    }

    @Then("an email is sent to the email address")
    public void an_email_is_sent_to_the_email_address() {
        ArgumentCaptor<Gardener> gardenerCaptor = ArgumentCaptor.forClass(Gardener.class);
        ArgumentCaptor<String> requestCaptor = ArgumentCaptor.forClass(String.class);
        verify(writeEmail, times(1)).sendPasswordForgotEmail(gardenerCaptor.capture(), requestCaptor.capture());
    }
}