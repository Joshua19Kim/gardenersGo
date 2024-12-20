package nz.ac.canterbury.seng302.gardenersgrove.unit.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.controller.RegisterController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.MainPageLayoutService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import nz.ac.canterbury.seng302.gardenersgrove.util.WriteEmail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;

public class RegisterControllerTest {

    private RegisterController registerFormController;
    private GardenerFormService gardenerFormService;
    private AuthenticationManager authenticationManager;
    private Authentication authentication;
    private HttpServletRequest request;
    private HttpSession sessionMock;
    private MainPageLayoutService mainPageLayoutService;
    private Model model;

    @BeforeEach
    public void setUp() {
        gardenerFormService = Mockito.mock(GardenerFormService.class);
        request = Mockito.mock(HttpServletRequest.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        TokenService tokenService = Mockito.mock(TokenService.class);
        WriteEmail writeEmail = Mockito.mock(WriteEmail.class);
        mainPageLayoutService = Mockito.mock(MainPageLayoutService.class);
        registerFormController = new RegisterController(gardenerFormService, tokenService, writeEmail, mainPageLayoutService);
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
        registerFormController.submitForm("Kush", "Desai", LocalDate.of(2004, 1, 15).toString(),"test@gmail.com", "Password1!", "Password1!", false, false, model);
        Mockito.verify(gardenerFormService, times(1)).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenInvalidFirstName_WhenUserRegisters_NewGardenerNotCreated() {
        registerFormController.submitForm("Kush1", "Desai", LocalDate.of(2004, 1, 15).toString(),"test@gmail.com", "Password1!", "Password1!", false, false, model);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenInvalidLastName_WhenLastNameIsNotOptional_NewGardenerNotCreated() {
        registerFormController.submitForm("Kush", "Desai1", LocalDate.of(2004, 1, 15).toString(),"test@gmail.com", "Password1!", "Password1!", false, false, model);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenInvalidLastName_WhenLastNameIsOptional_NewGardenerCreated() {
        authentication = Mockito.mock(Authentication.class);
        sessionMock = Mockito.mock(HttpSession.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(request.getSession()).thenReturn(sessionMock);
        registerFormController.submitForm("Kush", "", LocalDate.of(2004, 1, 15).toString(),"test@gmail.com", "Password1!", "Password1!", true, false, model);
        Mockito.verify(gardenerFormService, times(1)).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenAgeTooLow_WhenUserRegisters_NewGardenerNotCreated() {
        registerFormController.submitForm("Kush", "Desai", LocalDate.of(2024, 1, 15).toString(),"test@gmail.com", "Password1!", "Password1!", true, false, model);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenAgeTooHigh_WhenUserRegisters_NewGardenerNotCreated() {
        registerFormController.submitForm("Kush", "Desai", LocalDate.of(1024, 1, 15).toString(),"test@gmail.com", "Password1!", "Password1!", true, false, model);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenDateOverUpperBound_WhenUserRegisters_NewGardenerNotCreated() {
        registerFormController.submitForm("Kush", "Desai", LocalDate.of(9999, 12, 31).toString(),"test@gmail.com", "Password1!", "Password1!", true, false, model);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenDateOutOfBounds_WhenUserRegisters_NewGardenerNotCreated() {
        registerFormController.submitForm("Kush", "Desai", LocalDate.of(999999, 12, 31).toString(),"test@gmail.com", "Password1!", "Password1!", true, false, model);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenPasswordsDontMatch_WhenUserRegisters_NewGardenerNotCreated() {
        registerFormController.submitForm("Kush", "Desai", LocalDate.of(2024, 1, 15).toString(),"test@gmail.com", "Password1!", "Password2!", true, false, model);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenEmptyDoB_WhenUserRegisters_NewGardenerCreated() {
        authentication = Mockito.mock(Authentication.class);
        sessionMock = Mockito.mock(HttpSession.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(request.getSession()).thenReturn(sessionMock);
        registerFormController.submitForm("Kush", "Desai", null,"test@gmail.com", "Password1!", "Password1!", false, false, model);
        Mockito.verify(gardenerFormService, times(1)).addGardener(Mockito.any(Gardener.class));
    }

}
