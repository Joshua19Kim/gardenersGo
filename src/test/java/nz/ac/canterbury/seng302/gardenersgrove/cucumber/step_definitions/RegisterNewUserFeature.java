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

    @Given("I enter the first name {string}, last name {string}, email address {string}, the password {string} twice, and a date of birth of {string}")
    public void i_enter_the_first_name_last_name_email_address_the_password_twice_and_a_date_of_birth_of(String firstName, String lastName, String email, String password, String DoB) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.passwordConfirm = password;
        this.DoB = LocalDate.parse(DoB, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @When("I submit the register form")
    public void i_submit_the_register_form() throws Exception {
        mvcResult = mockMvcRegister.perform(MockMvcRequestBuilders.post("/register")
                .param("firstName", firstName)
                .param("lastName", lastName)
                .param("email", email)
                .param("password", password)
                .param("passwordConfirm", passwordConfirm)
                .param("DoB", DoB.toString()))
                .andReturn();
    }

    @Then("I am redirected to the signup code page")
    public void i_am_redirected_to_the_signup_code_page() {
        assertEquals(mvcResult.getResponse().getStatus(), 302);
        assertEquals(mvcResult.getModelAndView().getViewName(), "redirect:/signup");
        verify(gardenerFormService, times(1)).addGardener(any(Gardener.class));
        verify(writeEmail, times(1)).sendSignupEmail(any(Gardener.class), eq(tokenService));
    }

    @Given("I click the check box marked I have no surname ticked,")
    public void i_click_the_check_box_marked_i_have_no_surname_ticked() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("the last name text field is disabled")
    public void the_last_name_text_field_is_disabled() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("it will be ignored when I click the Sign Up button")
    public void it_will_be_ignored_when_i_click_the_sign_up_button() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
}
