package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.controller.RegisterController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.UserProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.awt.*;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private Authentication authentication;
    @MockBean
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        //Use this following gardener as the one called from db.
        Gardener testGardener = new Gardener(
                "testFirstName",
                "testLastName",
                LocalDate.of(1980, 1, 1),
                "testEmail@gmail.com",
                "testPassword",
                "testProfilePhoto.jpg");
        Mockito.when(gardenerFormService.findByEmail(anyString())).thenReturn(Optional.of(testGardener));

    }

    @Test
    @WithMockUser
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
    @WithMockUser
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
    @WithMockUser
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
    @WithMockUser
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
    @WithMockUser
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
    @WithMockUser
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
    @WithMockUser
    void onUserPage_userChangesFirstNameWithValidName_SaveNewFirstName() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user")
                        .param("firstName", "newTestFirstName")
                        .param("lastName", "testLastName")
                        .param("DoB", "1980-01-01")
                        .param("email", "testEmail@gmail.com")
                        .param("isLastNameOptional", "false")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(model().attribute("firstNameValid",""))
                .andExpect(model().attribute("lastNameValid",""))
                .andExpect(model().attribute("DoBValid",""));
//                .andExpect(model().attribute("emailValid",""));

//
//        ArgumentCaptor<Gardener> captor = ArgumentCaptor.forClass(Gardener.class);
//        Mockito.verify(gardenerFormService).addGardener(captor.capture());
//        Gardener updatedGardener = captor.getValue();
//        assertEquals("newTestFirstName", updatedGardener.getFirstName());
    }


//    @Test
//    @WithMockUser
//    void onUserProfilePage_CorrectPasswordAndValidMatchingNewPasswordGiven_ChangePassword() throws Exception {
//        UserProfileController userProfileController = new UserProfileController(gardenerFormService);
//        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(userProfileController).build();
//        MOCK_MVC.perform(post("/password")
//                        .with(csrf())
//                        .param("oldPassword", "Password1!")
//                        .param("newPassword", "newPassword1@")
//                        .param("retypePassword", "newPassword1@")
//                )
//                .andExpect(status().isOk())
//                .andExpect(view().name("redirect:/user"));
//    }


}
