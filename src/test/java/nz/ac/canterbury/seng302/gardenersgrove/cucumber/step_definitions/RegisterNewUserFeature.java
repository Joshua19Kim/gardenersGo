package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.ForgotPasswordFormController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.LoginController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.RegisterController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import nz.ac.canterbury.seng302.gardenersgrove.util.WriteEmail;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
public class RegisterNewUserFeature {
    @Mock
    private GardenerFormService gardenerFormService;
    @Mock
    private TokenService tokenService;
    @Mock
    private WriteEmail writeEmail;

    private MockMvc mockMvcLogin;
    private MockMvc mockMvcRegister;
    private MvcResult mvcResult;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String passwordConfirm;
    private LocalDate DoB;
    private Boolean isLastNameOptional = false;

    @Before("@U1")
    public void setUp() {
        gardenerFormService = Mockito.mock(GardenerFormService.class);
        tokenService = Mockito.mock(TokenService.class);
        writeEmail = Mockito.mock(WriteEmail.class);

        LoginController loginController = new LoginController();
        mockMvcLogin = MockMvcBuilders.standaloneSetup(loginController).build();

        RegisterController registerController = new RegisterController(
                gardenerFormService, tokenService, writeEmail);
        mockMvcRegister = MockMvcBuilders.standaloneSetup(registerController).build();
    }

    @Given("I connect to the system’s main URL")
    public void i_connect_to_the_system_s_main_url() throws Exception {
        mvcResult = mockMvcLogin
                .perform(MockMvcRequestBuilders.get("/login"))
                .andReturn();
    }

    @When("I see the home page")
    public void i_see_the_home_page() {
        assertEquals(mvcResult.getResponse().getStatus(), 200);
    }

    @Then("It includes a form for registering a new user")
    public void it_includes_a_form_for_registering_a_new_user() {
        assertEquals(Objects.requireNonNull(mvcResult.getModelAndView()).getViewName(), "loginForm");
    }

    @Given("I am on the registration form")
    public void i_am_on_the_registration_form() throws Exception {
        mockMvcRegister
                .perform(MockMvcRequestBuilders.get("/register/"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Given("I enter the first name {string}")
    public void i_enter_the_first_name(String firstName) {
        this.firstName = firstName;
    }

    @Given("I enter the last name {string}")
    public void i_enter_the_last_name(String lastName) {
        this.lastName = lastName;
    }

    @Given("I enter the email address {string}")
    public void i_enter_the_email_address(String email) {
        this.email = email;
    }

    @Given("I enter the password {string}")
    public void i_enter_the_password(String password) {
        this.password = password;
    }

    @Given("I confirm my password as {string}")
    public void i_confirm_my_password_as(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    @Given("I enter a date of birth of {string}")
    public void i_enter_a_date_of_birth_of(String DoB) {
        this.DoB = LocalDate.parse(DoB, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @When("I submit the register form")
    public void i_submit_the_register_form() throws Exception {
        mvcResult = mockMvcRegister.perform(MockMvcRequestBuilders.post("/register/")
                .param("firstName", firstName)
                .param("lastName", lastName)
                .param("email", email)
                .param("password", password)
                .param("passwordConfirm", passwordConfirm)
                .param("DoB", DoB.toString())
                .param("isLastNameOptional", String.valueOf(isLastNameOptional)))
                .andReturn();
    }

    @Then("I am redirected to the signup code page")
    public void i_am_redirected_to_the_signup_code_page() {
        assertEquals(mvcResult.getResponse().getStatus(), 302);
        assertEquals(Objects.requireNonNull(mvcResult.getModelAndView()).getViewName(), "redirect:/signup");
        verify(gardenerFormService, times(1)).addGardener(any(Gardener.class));
        verify(writeEmail, times(1)).sendSignupEmail(any(Gardener.class), eq(tokenService));
    }

    @Given("I check the box to indicate I have no surname")
    public void i_check_the_box_to_indicate_i_have_no_surname() {
        this.isLastNameOptional = true;
    }

    @Then("an error message for the first name on the signup form tells me {string}")
    public void an_error_message_for_the_first_name_on_the_signup_form_tells_me(String errorMessage) {
        assertEquals(Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("firstNameValid"), errorMessage);
    }

    @Then("an error message for the last name on the signup form tells me {string}")
    public void an_error_message_for_the_last_name_on_the_signup_form_tells_me(String errorMessage) {
        assertEquals(Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("lastNameValid"), errorMessage);
    }

    @Then("no account is created")
    public void no_account_is_created() {
        verify(gardenerFormService, times(0)).addGardener(any(Gardener.class));
        verify(writeEmail, times(0)).sendSignupEmail(any(Gardener.class), eq(tokenService));
    }

    @Then("an error message for the email address on the signup form tells me {string}")
    public void an_error_message_for_the_email_address_on_the_signup_form_tells_me(String errorMessage) {
        assertEquals(Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("emailValid"), errorMessage);
    }
}
