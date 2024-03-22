package nz.ac.canterbury.seng302.gardenersgrove;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.controller.RegisterFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;

public class RegisterFormControllerTest {

    private RegisterFormController registerFormController;
    private GardenerFormService gardenerFormService;
    private AuthenticationManager authenticationManager;
    private Authentication authentication;
    private HttpServletRequest request;
    private HttpSession sessionMock;
    private Model model;

    @BeforeEach
    public void setUp() {
        gardenerFormService = Mockito.mock(GardenerFormService.class);
        request = Mockito.mock(HttpServletRequest.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        registerFormController = new RegisterFormController(gardenerFormService, authenticationManager);
        model = Mockito.mock(Model.class);
        assertTrue(true);
    }

    @Test
    void GivenValidGardenerInput_WhenUserRegisters_NewGardenerCreated() {

        authentication = Mockito.mock(Authentication.class);
        sessionMock = Mockito.mock(HttpSession.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(request.getSession()).thenReturn(sessionMock);
        registerFormController.submitForm(request, "Kush", "Desai", LocalDate.of(2004, 1, 15),"test@gmail.com", "Password1!", "Password1!", false, model);
        Mockito.verify(gardenerFormService, times(1)).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenInvalidFirstName_WhenUserRegisters_NewGardenerNotCreated() {
        registerFormController.submitForm(request, "Kush1", "Desai", LocalDate.of(2004, 1, 15),"test@gmail.com", "Password1!", "Password1!", false, model);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenInvalidLastName_WhenLastNameIsNotOptional_NewGardenerNotCreated() {
        registerFormController.submitForm(request, "Kush", "Desai1", LocalDate.of(2004, 1, 15),"test@gmail.com", "Password1!", "Password1!", false, model);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenInvalidLastName_WhenLastNameIsOptional_NewGardenerCreated() {
        authentication = Mockito.mock(Authentication.class);
        sessionMock = Mockito.mock(HttpSession.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(request.getSession()).thenReturn(sessionMock);
        registerFormController.submitForm(request, "Kush", "Desai1", LocalDate.of(2004, 1, 15),"test@gmail.com", "Password1!", "Password1!", true, model);
        Mockito.verify(gardenerFormService, times(1)).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenAgeTooLow_WhenUserRegisters_NewGardenerNotCreated() {
        registerFormController.submitForm(request, "Kush", "Desai", LocalDate.of(2024, 1, 15),"test@gmail.com", "Password1!", "Password1!", true, model);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenAgeTooHigh_WhenUserRegisters_NewGardenerNotCreated() {
        registerFormController.submitForm(request, "Kush", "Desai", LocalDate.of(1024, 1, 15),"test@gmail.com", "Password1!", "Password1!", true, model);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenPasswordsDontMatch_WhenUserRegisters_NewGardenerNotCreated() {
        registerFormController.submitForm(request, "Kush", "Desai", LocalDate.of(2024, 1, 15),"test@gmail.com", "Password1!", "Password2!", true, model);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenEmptyDoB_WhenUserRegisters_NewGardenerCreated() {
        authentication = Mockito.mock(Authentication.class);
        sessionMock = Mockito.mock(HttpSession.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(request.getSession()).thenReturn(sessionMock);
        registerFormController.submitForm(request, "Kush", "Desai", null,"test@gmail.com", "Password1!", "Password1!", true, model);
        Mockito.verify(gardenerFormService, times(1)).addGardener(Mockito.any(Gardener.class));
    }

}
