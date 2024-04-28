package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.UserProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
public class UserProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private Model modelMock;
    @MockBean
    private GardenerFormService gardenerFormService;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private Optional gardenerOptional;
    @MockBean
    private Authentication authentication;
    @MockBean
    private ImageService imageService;
    @MockBean
    private Gardener testGardener;
    @MockBean
    private UsernamePasswordAuthenticationToken token;

    @BeforeEach
    void setUp() {
        //Use this following gardener as the one called from db.
        testGardener = new Gardener(
                "testFirstName",
                "testLastName",
                LocalDate.of(1980, 1, 1),
                "testEmail@gmail.com",
                "testPassword",
                "testProfilePhoto.jpg");
//        gardenerFormService = Mockito.mock(GardenerFormService.class);
//        gardenerFormService.addGardener(testGardener);
//        authenticationManager = Mockito.mock(AuthenticationManager.class);
//        token = new UsernamePasswordAuthenticationToken(testGardener.getEmail(), testGardener.getPassword());
//        authentication = authenticationManager.authenticate(token);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        Mockito.when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
        gardenerFormService.addGardener(testGardener);
        when(gardenerFormService.findByEmail("testEmail@gmail.com")).thenReturn(Optional.of(testGardener));

    }

    @Test
    @WithMockUser("testEmail@gmail.com")
    void onUserPage_userWantsToSeeDetails_showCorrectDetails() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user"))
                .andExpect(model().attribute("firstName", "testFirstName"))
                .andExpect(model().attribute("lastName", "testLastName"))
                .andExpect(model().attribute("DoB", LocalDate.of(1980, 1, 1)))
                .andExpect(model().attribute("email", "testEmail@gmail.com"))
                .andExpect(model().attribute("profilePic", "testProfilePhoto.jpg"));

    }

    @Test
    @WithMockUser("testEmail@gmail.com")
    void onUserPage_userChangesFirstNameWithInvalidFirstName_errorMessageProvided() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user")
                        .param("firstName", " ")
                        .param("lastName", "testLastName")
                        .param("DoB", "1980-01-01")
                        .param("email", "testEmail@gmail.com")
                        .param("isLastNameOptional", "false")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("user"))
                .andExpect(model().attribute("firstNameValid", "First name cannot be empty and must only include letters, spaces, " +
                        "hyphens or apostrophes"));
    }


    @Test
    @WithMockUser("testEmail@gmail.com")
    void onUserPage_userChangesLastNameWithInvalidLastName_errorMessageProvided() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user")
                        .param("firstName", "testFirstName")
                        .param("lastName", "!%ASava")
                        .param("DoB", "1980-01-01")
                        .param("email", "testEmail@gmail.com")
                        .param("isLastNameOptional", "false")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("user"))
                .andExpect(model().attribute("lastNameValid", "Last name cannot be empty and must only include letters, spaces, " +
                        "hyphens or apostrophes"));
    }

    @Test
    @WithMockUser("testEmail@gmail.com")
    void onUserPage_userChangesDoBWithTooLowDoB_errorMessageProvided() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user")
                        .param("firstName", "testFirstName")
                        .param("lastName", "testLastName")
                        .param("DoB", "2400-01-01")
                        .param("email", "testEmail@gmail.com")
                        .param("isLastNameOptional", "false")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("user"))
                .andExpect(model().attribute("DoBValid", "You must be 13 years or older to create an account"));
    }

    @Test
    @WithMockUser("testEmail@gmail.com")
    void onUserPage_userChangesDoBWithTooHighDoB_errorMessageProvided() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user")
                        .param("firstName", "testFirstName")
                        .param("lastName", "testLastName")
                        .param("DoB", "1700-01-01")
                        .param("email", "testEmail@gmail.com")
                        .param("isLastNameOptional", "false")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("user"))
                .andExpect(model().attribute("DoBValid", "The maximum age allowed is 120 years"));
    }

    @Test
    @WithMockUser("testEmail@gmail.com")
    void onUserPage_userChangesEmailWithInvalidEmail_errorMessageProvided() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user")
                        .param("firstName", "testFirstName")
                        .param("lastName", "testLastName")
                        .param("DoB", "1980-01-01")
                        .param("email", "testEmailgmail.m")
                        .param("isLastNameOptional", "false")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("user"))
                .andExpect(model().attribute("emailValid", "Email address must be in the form ‘jane@doe.nz"));
    }

    @Test
    @WithMockUser("testEmail@gmail.com")
    void onUserPage_userChangesFirstNameWithValidName_NoErrorProvidedAndSaveNewFirstName() throws Exception {
        testGardener = new Gardener(
                "testFirstName",
                "testLastName",
                LocalDate.of(1980, 1, 1),
                "testEmail@gmail.com",
                "testPassword",
                "testProfilePhoto.jpg");
        gardenerFormService = Mockito.mock(GardenerFormService.class);
        when(gardenerFormService.findByEmail(anyString())).thenReturn(Optional.of(testGardener));

//        UserProfileController userProfileController = new UserProfileController(gardenerFormService);
//        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(userProfileController).build();
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user")
                        .param("firstName", "testFirstName")
                        .param("lastName", "testLastName")
                        .param("DoB", "1980-01-01")
                        .param("email", "testEmail@gmail.com")
                        .param("isLastNameOptional", "false")
                        .with(csrf())
                )
                .andExpect(status().isOk());
//                .andExpect(model().attribute("firstNameValid",""))
//                .andExpect(model().attribute("lastNameValid",""))
//                .andExpect(model().attribute("DoBValid",""));
//                .andExpect(model().attrrmService = Mockito.mock(GardenerFormService.class);
//        Mockito.whibute("emailValid",""));

//        gardenerCaptor = ArgumentCaptor.forClass(Gardener.class);
//        Mockito.verify(gardenerFormService).addGardener(gardenerCaptor.capture());
//        Gardener updatedGardener = gardenerCaptor.getValue();
//        assertEquals("newTestFirstName", updatedGardener.getFirstName());
    }

    @Test
    @WithMockUser("testEmail@gmail.com")
    void OnPasswordUpdatePage_userPutWrongOldPassword_errorMessageProvided() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/password")
                        .param("oldPassword", "wrongPassword")
                        .param("newPassword", "NewPassWord1@")
                        .param("retypePassword", "NewPassWord1@")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("password"))
                .andExpect(model().attribute("passwordCorrect", "Your old password is incorrect."));
    }
    @Test
    @WithMockUser("testEmail@gmail.com")
    void OnPasswordUpdatePage_userPutNotMatchedNewPasswords_errorMessageProvided() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/password")
                        .param("oldPassword", "testPassword")
                        .param("newPassword", "NewPassWord1@")
                        .param("retypePassword", "DifferentPassWord1@")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("password"))
                .andExpect(model().attribute("passwordsMatch", "Passwords do not match."));
    }
    @Test
    @WithMockUser("testEmail@gmail.com")
    void OnPasswordUpdatePage_userPutInvalidNewPasswords_errorMessageProvided() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/password")
                        .param("oldPassword", "testPassword")
                        .param("newPassword", "newpassWord")
                        .param("retypePassword", "newpassWord")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("password"))
                .andExpect(model().attribute("passwordStrong", "Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character."));
    }
}
