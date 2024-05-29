package nz.ac.canterbury.seng302.gardenersgrove.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.RegisterController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.SignupCodeFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.AuthorityFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.LostPasswordTokenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.AuthorityFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import nz.ac.canterbury.seng302.gardenersgrove.util.WriteEmail;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
public class RegistrationEmailFeature {

    private static MockMvc REGISTER_MOCK_MVC;
    private static MockMvc SIGN_UP_MOCK_MVC;

    @Autowired
    private GardenerFormRepository gardenerRepository;

    @Autowired
    private LostPasswordTokenRepository lostPasswordTokenRepository;

    @Autowired
    private AuthorityFormRepository authorityFormRepository;
    private TokenService tokenService;
    private WriteEmail mockWriteEmail;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;
    private LocalDate dateOfBirth;
    private String signUpCode = "123456";
    private MvcResult mvcResult;

    @Before("@U6")
    public void setUp() {
        // Mocking
        EmailUserService emailUserService = Mockito.mock(EmailUserService.class);
        WriteEmail writeEmail = new WriteEmail(emailUserService);
        mockWriteEmail = Mockito.spy(writeEmail);
        gardenerRepository = Mockito.mock(GardenerFormRepository.class);
        tokenService = Mockito.mock(TokenService.class);
        GardenerFormService gardenerFormService = new GardenerFormService(gardenerRepository, lostPasswordTokenRepository);
        AuthorityFormService authorityFormService = new AuthorityFormService(authorityFormRepository);

        //Setup MOCK_MVC
        RegisterController registerController = new RegisterController(gardenerFormService, tokenService, mockWriteEmail);
        REGISTER_MOCK_MVC = MockMvcBuilders.standaloneSetup(registerController).build();

        SignupCodeFormController signupCodeFormController = new SignupCodeFormController(gardenerFormService, authorityFormService, tokenService);
        SIGN_UP_MOCK_MVC = MockMvcBuilders.standaloneSetup(signupCodeFormController).build();

        // Set variables
        firstName = "John";
        lastName = "Doe";
        email = "john@doe.com";
        password = "Password1!";
        confirmPassword = "Password1!";
        dateOfBirth = LocalDate.of(2000, 1, 1);
        Gardener testGardener = new Gardener(firstName, lastName, dateOfBirth, email, password);

        // Setup Mocks
        Mockito.when(gardenerRepository.findByEmail(email)).thenReturn(Optional.empty());
        Mockito.when(tokenService.findGardenerbyToken(signUpCode)).thenReturn(Optional.of(testGardener));
        Mockito.when(tokenService.validateLostPasswordToken("")).thenReturn("invalidToken");
    }

    @Given("I submit a fully valid registration form")
    public void i_submit_a_fully_valid_registration_form() {
        firstName = "John";
        lastName = "Doe";
        email = "john@doe.com";
        password = "Password1!";
        confirmPassword = "Password1!";
        dateOfBirth = LocalDate.of(2000, 1, 1);
    }

    @Given("I have a signup code")
    public void i_have_a_signup_code() {
        signUpCode = "123456";
    }

    @When("when I click the “Register” button")
    public void i_click_the_register_button() throws Exception {
        REGISTER_MOCK_MVC.perform(MockMvcRequestBuilders.post("/register")
                .param("firstName", firstName)
                .param("lastName", lastName)
                .param("email", email)
                .param("password", password)
                .param("passwordConfirm", confirmPassword)
                .param("DoB", dateOfBirth.toString())
                .with(csrf()));
    }

    @When("The signup code is deleted")
    public void the_signup_code_is_deleted() {
        signUpCode = null;
    }

    @When("I try to use the signup code")
    public void i_try_to_use_the_signup_code() throws Exception {
        mvcResult = SIGN_UP_MOCK_MVC.perform(MockMvcRequestBuilders.post("/signup")
                        .param("signupToken", signUpCode)
                        .with(csrf()))
                .andReturn();

    }

    @Then("A confirmation email is sent to my email address")
    public void a_confirmation_email_is_sent_to_my_email_address() {
        Mockito.verify(mockWriteEmail, Mockito.times(1)).sendSignupEmail(Mockito.any(Gardener.class), Mockito.any(TokenService.class));
    }

    @Then("A unique registration token is included in the email in the form of a unique signup code")
    public void a_unique_registration_token_is_included_in_the_email_in_the_form_of_a_unique_signup_code() {
        Mockito.verify(tokenService, Mockito.times(1)).createLostPasswordTokenForGardener(Mockito.any(Gardener.class), Mockito.anyString());
    }

    @Then("I’m presented with a page asking for the signup code")
    public void im_presented_with_a_page_asking_for_the_signup_code() {
        // This is a UI test, so we can't test it here
    }

    @Then("I see an error message “Signup code invalid”")
    public void i_see_an_error_message_signup_code_invalid() {
        ModelAndView result = mvcResult.getModelAndView();
        assert result != null;
        assertEquals("redirect:/signup?invalid", result.getViewName());
    }

    @Then("I am logged in")
    public void i_am_logged_in() {
        ModelAndView result = mvcResult.getModelAndView();
        assert result != null;
        assertEquals("redirect:/login?signedup", result.getViewName());
    }
}
