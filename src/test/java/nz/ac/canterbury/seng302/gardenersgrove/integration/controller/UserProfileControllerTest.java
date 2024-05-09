package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.UserProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Authority;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.util.WriteEmail;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
public class UserProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GardenerFormService gardenerFormService;
    @MockBean
    private GardenService gardenService; // Tests fill fail without this
    @MockBean
    private ImageService imageService;
    @MockBean
    private Gardener testGardener;
    @MockBean
    private ArgumentCaptor<Gardener> gardenerCaptor;
    @MockBean
    private EmailUserService emailService;
    @MockBean
    private WriteEmail mockWriteEmail;
    @MockBean
    private RelationshipService relationshipService;


    @BeforeEach
    void setUp() {
        Mockito.reset(gardenerFormService);
        //Use this following gardener as the one called from db.
        testGardener = new Gardener(
                "testFirstName",
                "testLastName",
                LocalDate.of(1980, 1, 1),
                "testEmail@gmail.com",
                "testPassword"
        );

        List<Authority> userRoles = new ArrayList<>();
        testGardener.setUserRoles(userRoles);
        testGardener.setId(1L);
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
                .andExpect(model().attribute("profilePic", "/images/defaultProfilePic.png"));

    }
    @Test
    @WithMockUser()
    void onUserPage_userTrysToAccessWithoutLoggingIn_ShowsNotRegisteredMessage() throws Exception {
        when(gardenerFormService.findByEmail("test@.com")).thenReturn(Optional.of(testGardener));
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user"))
                .andExpect(model().attribute("firstName", "Not Registered"));

    }

    @Test
    @WithMockUser("testEmail@gmail.com")
    void onUserPage_userChangesFirstNameWithInvalidFirstName_errorMessageProvided() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user")
                        .param("firstName", "")
                        .param("lastName", "testLastName")
                        .param("DoB", "1980-01-01")
                        .param("email", "testEmail@gmail.com")
                        .param("isLastNameOptional", "false")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("user"))
                .andExpect(model().attribute("firstName", ""))
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
                .andExpect(model().attribute("lastName", "!%ASava"))
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
                .andExpect(model().attribute("DoB", LocalDate.of(2400,1,1)))
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
                .andExpect(model().attribute("DoB", LocalDate.of(1700,1,1)))
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
                .andExpect(model().attribute("email", "testEmailgmail.m"))
                .andExpect(model().attribute("emailValid", "Email address must be in the form ‘jane@doe.nz"));
    }

    @Test
    @WithMockUser("testEmail@gmail.com")
    void onUserPage_userChangesFirstNameWithValidName_NoErrorProvidedAndSaveNewFirstName() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user")
                        .param("firstName", "newTestFirstName")
                        .param("lastName", "testLastName")
                        .param("DoB", "1980-01-01")
                        .param("email", "testEmail@gmail.com")
                        .param("isLastNameOptional", "false")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user"))
                .andExpect(model().attribute("firstNameValid", Matchers.nullValue()))
                .andExpect(model().attribute("lastNameValid",Matchers.nullValue()))
                .andExpect(model().attribute("DoBValid",Matchers.nullValue()))
                .andExpect(model().attribute("emailValid", Matchers.nullValue()));

        ArgumentCaptor<Gardener> gardenerCaptor = ArgumentCaptor.forClass(Gardener.class);
        //wantedNumberOfInvocation has additional 1 since .addGardener() is called once in test
        verify(gardenerFormService, Mockito.times(2)).addGardener(gardenerCaptor.capture());
        List<Gardener> addGardener = gardenerCaptor.getAllValues();
        assertEquals("newTestFirstName", addGardener.get(1).getFirstName());
    }

    @Test
    @WithMockUser("testEmail@gmail.com")
    void onUserPage_userChangesLastNameWithValidName_NoErrorProvidedAndSaveNewLastName() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user")
                        .param("firstName", "testFirstName")
                        .param("lastName", "newTestLastName")
                        .param("DoB", "1980-01-01")
                        .param("email", "testEmail@gmail.com")
                        .param("isLastNameOptional", "false")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user"))
                .andExpect(model().attribute("firstNameValid", Matchers.nullValue()))
                .andExpect(model().attribute("lastNameValid",Matchers.nullValue()))
                .andExpect(model().attribute("DoBValid",Matchers.nullValue()))
                .andExpect(model().attribute("emailValid", Matchers.nullValue()));

        ArgumentCaptor<Gardener> gardenerCaptor = ArgumentCaptor.forClass(Gardener.class);
        //wantedNumberOfInvocation has additional 1 since .addGardener() is called once in test
        verify(gardenerFormService, Mockito.times(2)).addGardener(gardenerCaptor.capture());
        List<Gardener> addGardener = gardenerCaptor.getAllValues();
        assertEquals("newTestLastName", addGardener.get(1).getLastName());
    }

    @Test
    @WithMockUser("testEmail@gmail.com")
    void onUserPage_userChangesDoBWithValidDoB_NoErrorProvidedAndSaveNewDoB() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user")
                        .param("firstName", "testFirstName")
                        .param("lastName", "newTestLastName")
                        .param("DoB", "2001-12-13")
                        .param("email", "testEmail@gmail.com")
                        .param("isLastNameOptional", "false")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user"))
                .andExpect(model().attribute("firstNameValid", Matchers.nullValue()))
                .andExpect(model().attribute("lastNameValid",Matchers.nullValue()))
                .andExpect(model().attribute("DoBValid",Matchers.nullValue()))
                .andExpect(model().attribute("emailValid", Matchers.nullValue()));

        gardenerCaptor = ArgumentCaptor.forClass(Gardener.class);
        //wantedNumberOfInvocation has additional 1 since .addGardener() is called once in test
        verify(gardenerFormService, Mockito.times(2)).addGardener(gardenerCaptor.capture());
        List<Gardener> addGardener = gardenerCaptor.getAllValues();
        assertEquals(LocalDate.of(2001,12,13), addGardener.get(1).getDoB());
    }

    @Test
    @WithMockUser("testEmail@gmail.com")
    void onUserPage_userChangesEmailWithValidEmail_NoErrorProvidedAndSaveNewEmail() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user")
                        .param("firstName", "testFirstName")
                        .param("lastName", "newTestLastName")
                        .param("DoB", "1980-01-01")
                        .param("email", "newTestEmail@gmail.com")
                        .param("isLastNameOptional", "false")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user"))
                .andExpect(model().attribute("firstNameValid", Matchers.nullValue()))
                .andExpect(model().attribute("lastNameValid",Matchers.nullValue()))
                .andExpect(model().attribute("DoBValid",Matchers.nullValue()))
                .andExpect(model().attribute("emailValid", Matchers.nullValue()));

        gardenerCaptor = ArgumentCaptor.forClass(Gardener.class);
        //wantedNumberOfInvocation has additional 1 since .addGardener() is called once in test
        verify(gardenerFormService, Mockito.times(2)).addGardener(gardenerCaptor.capture());
        List<Gardener> addGardener = gardenerCaptor.getAllValues();
        assertEquals("newTestEmail@gmail.com", addGardener.get(1).getEmail());
    }

    @Test
    @WithMockUser("testEmail@gmail.com")
    void onUserPage_userPutNewLastNameButTicksNoLastNameBox_NoErrorProvidedAndLastNameBecomesNull() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user")
                        .param("firstName", "testFirstName")
                        .param("lastName", "newTestLastName")
                        .param("DoB", "1980-01-01")
                        .param("email", "newTestEmail@gmail.com")
                        .param("isLastNameOptional", "true")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user"))
                .andExpect(model().attribute("firstNameValid", Matchers.nullValue()))
                .andExpect(model().attribute("lastNameValid",Matchers.nullValue()))
                .andExpect(model().attribute("DoBValid",Matchers.nullValue()))
                .andExpect(model().attribute("emailValid", Matchers.nullValue()));

        gardenerCaptor = ArgumentCaptor.forClass(Gardener.class);
        //wantedNumberOfInvocation has additional 1 since .addGardener() is called once in test
        verify(gardenerFormService, Mockito.times(2)).addGardener(gardenerCaptor.capture());
        List<Gardener> addGardener = gardenerCaptor.getAllValues();
        assertNull(addGardener.get(1).getLastName());
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
                .andExpect(model().attribute("passwordStrong"
                        , "Your password must be at least 8 characters long " +
                                "and include at least one uppercase letter, " +
                                "one lowercase letter, one number, and one special character."));
    }

    @Test
    @WithMockUser("testEmail@gmail.com")
    void OnPasswordUpdatePage_userPutValidNewPasswords_saveNewPasswordAndSendConfirmationEmail() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/password")
                        .param("oldPassword", "testPassword")
                        .param("newPassword", "NewPassWord1@")
                        .param("retypePassword", "NewPassWord1@")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user"));

        gardenerCaptor = ArgumentCaptor.forClass(Gardener.class);
        Mockito.doNothing().when(mockWriteEmail).sendPasswordUpdateConfirmEmail(Mockito.any(Gardener.class));
        //wantedNumberOfInvocation has additional 1 since .addGardener() is called once in test
        verify(gardenerFormService, times(2)).addGardener(gardenerCaptor.capture());
        verify(mockWriteEmail,times(1)).sendPasswordUpdateConfirmEmail(Mockito.any(Gardener.class));
        List<Gardener> addedGardener = gardenerCaptor.getAllValues();
        assertEquals("testEmail@gmail.com", addedGardener.get(1).getEmail());
    }

    @Test
    @WithMockUser("testEmail@gmail.com")
    void OnEditProfilePhotoMode_userPutsWrongTypeOfPhotos_errorMessageProvided() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "image.txt",
                "plain/text",
                "Hello World!".getBytes()
        );
        String uploadMessage = "Image must be of type png, jpg or svg";
        when(imageService.saveImage(mockMultipartFile)).thenReturn(Optional.of(uploadMessage));
        this.mockMvc
                .perform(MockMvcRequestBuilders.multipart("/user")
                .file(mockMultipartFile)
                .with(csrf()))
                .andExpect(view().name("/user"));

        gardenerCaptor = ArgumentCaptor.forClass(Gardener.class);
        //wantedNumberOfInvocation has additional 1 since .addGardener() is called once in test
        verify(gardenerFormService, Mockito.times(1)).addGardener(gardenerCaptor.capture());
    }
    @Test
    @WithMockUser("testEmail@gmail.com")
    void OnEditProfilePhotoPage_userClicksProfileButtonWithAuthentication_redirectUserToUserPage() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/redirectToUserPage")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("/user"));
    }
    @Test
    @WithMockUser("testEmail@gmail.com")
    void OnEditProfilePhotoPage_userPutsValidTypeOfPhotos_errorMessageProvided() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                "image content".getBytes()
        );
        when(imageService.saveImage(mockMultipartFile)).thenReturn(Optional.empty());
        this.mockMvc
                .perform(MockMvcRequestBuilders.multipart("/user")
                        .file(mockMultipartFile)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user"));

        gardenerCaptor = ArgumentCaptor.forClass(Gardener.class);
        //wantedNumberOfInvocation has additional 1 since .addGardener() is called once in test
        verify(gardenerFormService, Mockito.times(1)).addGardener(gardenerCaptor.capture());
    }

    @Test
    @WithMockUser("testEmail@gmail.com")
    public void ViewFriendProfileRequested_UserIsFriend_FriendProfileViewed() throws Exception {
        Gardener friend = new Gardener("Test", "Gardener 2", LocalDate.of(2000, 1, 1), "test2@test.com", "Password1!");
        friend.setId(2L);

        List<Gardener> friends = new ArrayList<>();
        friends.add(friend);

        when(gardenerFormService.findById(friend.getId())).thenReturn(Optional.of(friend));
        when(relationshipService.getCurrentUserRelationships(testGardener.getId())).thenReturn(friends);

        mockMvc.perform(MockMvcRequestBuilders.get("/user")
                .param("user", String.valueOf(friend.getId())))
                .andExpect(model().attribute("gardener", friend))
                .andExpect(status().isOk())
                .andExpect(view().name("unauthorizedUser"));
    }

    @Test
    @WithMockUser("testEmail@gmail.com")
    public void ViewFriendProfileRequested_UserIsNotFriend_RedirectedToUserProfile() throws Exception {
        Gardener friend = new Gardener("Test", "Gardener 2", LocalDate.of(2000, 1, 1), "test2@test.com", "Password1!");
        friend.setId(2L);

        List<Gardener> friends = new ArrayList<>();

        when(gardenerFormService.findById(friend.getId())).thenReturn(Optional.of(friend));
        when(relationshipService.getCurrentUserRelationships(testGardener.getId())).thenReturn(friends);

        mockMvc.perform(MockMvcRequestBuilders.get("/user")
                        .param("user", String.valueOf(friend.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user"));
    }

    @Test
    @WithMockUser("testEmail@gmail.com")
    public void ViewFriendProfileRequested_FriendDoesNotExist_RedirectedToUserProfile() throws Exception {

        Long nonExistentFriendId = 2L;

        when(gardenerFormService.findById(nonExistentFriendId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/user")
                        .param("user", String.valueOf(nonExistentFriendId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user"));
    }

}
