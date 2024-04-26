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

import java.time.LocalDate;
import java.util.Optional;

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
    private static Model modelMock;
    @MockBean
    private static GardenerFormService gardenerFormService;
    @MockBean
    private static AuthenticationManager authenticationManager;
    @MockBean
    private static Authentication authentication;

    @BeforeEach
    @WithMockUser
    void setUp() {
//        authentication = Mockito.mock(Authentication.class);
//        modelMock = Mockito.mock(Model.class);
//        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
//        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Gardener testGardener = new Gardener(
                "testFirstName",
                "testLastName",
                LocalDate.of(1980, 1, 1),
                "testEmail@gmail.com",
                "testPassword",
                "6.jpg");
        Mockito.when(gardenerFormService.findByEmail(anyString())).thenReturn(Optional.of(testGardener));

    }

    @Test
    @WithMockUser
    void onUserPage_validUserWantsToSeeDetails_showCorrectDetails() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user"))
                .andExpect(model().attribute("firstName", "testFirstName"))
                .andExpect(model().attribute("lastName", "testLastName"))
                .andExpect(model().attribute("DoB", LocalDate.of(1980, 1, 1)))
                .andExpect(model().attribute("email", "testEmail@example.com"))
                .andExpect(model().attribute("profilePic", "testProfilePhoto.jpg"));

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
