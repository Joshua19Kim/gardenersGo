package nz.ac.canterbury.seng302.gardenersgrove;

import nz.ac.canterbury.seng302.gardenersgrove.controller.UserProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.InputValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.times;

public class UserProfileControllerTest {

    private UserProfileController userProfileController;
    private GardenerFormService gardenerFormService;
    private Model modelMock;
    private Gardener gardener;
    private Authentication authentication;
    private InputValidationService inputValidator;
    private Optional optional;
//    private StandaloneMockMvcBuilder mockMvc;


    @BeforeEach
    public void setUp() {
        authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        gardenerFormService = Mockito.mock(GardenerFormService.class);
        userProfileController = new UserProfileController(gardenerFormService);
        modelMock = Mockito.mock(Model.class);
        gardener = Mockito.mock(Gardener.class);
        inputValidator = Mockito.mock(InputValidationService.class);
        optional = Mockito.mock(Optional.class);
        gardener.setEmail("testSameEmail@test.test");
//        this.mockMvc = MockMvcBuilders.standaloneSetup(new UserProfileController(gardenerFormService));


    }

    @Test
    void GivenGardenerEmailExistingInServer_WhenToShowDetails_ControllerFindsDetailsWithEmail() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("testSameEmail@test.test");
        userProfileController.getUserProfile(null, null, null, null, false, modelMock);
        Mockito.verify(gardenerFormService, times(1)).findByEmail(gardener.getEmail());
    }

    @Test
    void GivenValidGardenerEdit_WhenUserConfirms_GardenerEditUploaded() {

        Mockito.when(authentication.getName()).thenReturn("new@new.new");
        Mockito.when(gardenerFormService.findByEmail(Mockito.any())).thenReturn(Optional.ofNullable(gardener));
        Mockito.when(optional.get()).thenReturn(gardener);
        Mockito.when(inputValidator.checkValidEmail(Mockito.any())).thenReturn(Optional.empty());
        // ONLY works when the email is the same as the submitted one
        Mockito.when(gardener.getEmail()).thenReturn("new@new.new");
//        Mockito.when(SecurityContextHolder.getContext().getAuthentication()).thenReturn();
//        Gardener testGardener = new Gardener("Ben", "Moore", LocalDate.of(2001, 11, 11),"test@test.test", "password");
//        Authentication newAuth = new UsernamePasswordAuthenticationToken(testGardener.getEmail(), testGardener.getPassword(), testGardener.getAuthorities());
        Mockito.when(authentication.getName()).thenReturn("new@new.new");
        userProfileController.getUserProfile("Ben", "Moore", LocalDate.of(2001, 11, 11), "new@new.new", false, modelMock);
        Mockito.verify(gardenerFormService, times(1)).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenInvalidFirstNameEdit_WhenUserConfirms_GardenerEditNotUploaded() {
        userProfileController.getUserProfile("$#@", "Desai", LocalDate.of(2004, 1, 15), "test@gmail.com", false, modelMock);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenInvalidLastNameEdit_WhenLastNameIsNotOptional_GardenerEditNotUploaded() {
        userProfileController.getUserProfile("Kush", "$#@", LocalDate.of(2004, 1, 15), "test@gmail.com", false, modelMock);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenInvalidLastName_WhenLastNameIsOptional_NewGardenerCreated() {
        Mockito.when(authentication.getName()).thenReturn("test@gmail.com");
        Mockito.when(gardenerFormService.findByEmail(Mockito.any())).thenReturn(Optional.ofNullable(gardener));
        Mockito.when(optional.get()).thenReturn(gardener);
        Mockito.when(inputValidator.checkValidEmail(Mockito.any())).thenReturn(Optional.empty());
        // ONLY works when the email is the same as the submitted one
        Mockito.when(gardener.getEmail()).thenReturn("test@gmail.com");
//        Mockito.when(SecurityContextHolder.getContext().getAuthentication()).thenReturn();
//        Gardener testGardener = new Gardener("Ben", "Moore", LocalDate.of(2001, 11, 11),"test@test.test", "password");
//        Authentication newAuth = new UsernamePasswordAuthenticationToken(testGardener.getEmail(), testGardener.getPassword(), testGardener.getAuthorities());
        Mockito.when(authentication.getName()).thenReturn("test@gmail.com");
        userProfileController.getUserProfile("Kush", "$#@", LocalDate.of(2004, 1, 15), "test@gmail.com", true, modelMock);
        Mockito.verify(gardenerFormService, times(1)).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenAgeTooLow_WhenUserConfirms_GardenerEditNotUploaded() {
        userProfileController.getUserProfile("Kush", "Desai", LocalDate.of(2024, 1, 15), "test@gmail.com", false, modelMock);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenAgeTooHigh_WhenUserConfirms_GardenerEditNotUploaded() {
        userProfileController.getUserProfile("Kush", "Desai", LocalDate.of(1024, 1, 15), "test@gmail.com", false, modelMock);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

}
