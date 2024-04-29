package nz.ac.canterbury.seng302.gardenersgrove.authentication;

import nz.ac.canterbury.seng302.gardenersgrove.controller.SignupCodeFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.InputValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Custom Authentication Provider class, to allow for handling authentication in any way we see fit.
 * In this case using our existing {@link nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener}
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    /**
     * User service for custom authentication using our own user objects
     */
    private final GardenerFormService gardenerFormService;

    private final Logger logger = LoggerFactory.getLogger(SignupCodeFormController.class);
    /**
     * @param gardenerFormService Gardener service for custom authentication using our own user objects to be injected in
     */
    public CustomAuthenticationProvider(GardenerFormService gardenerFormService) {
        super();
        this.gardenerFormService = gardenerFormService;

    }

    /**
     * Custom authentication implementation
     *
     * @param authentication An implementation object that must have non-empty email (name) and password (credentials)
     * @return A new {@link UsernamePasswordAuthenticationToken} if email and password are valid with users authorities
     */
    @Override
    public Authentication authenticate(Authentication authentication) {
        InputValidationService inputValidationService = new InputValidationService(gardenerFormService);
        String email = String.valueOf(authentication.getName());
        String password = String.valueOf(authentication.getCredentials());

        if (email == null || email.isEmpty() || inputValidationService.checkValidEmail(email).isPresent()) {
            throw new BadCredentialsException("Email address must be in the form 'jane@doe.nz'");
        }

        if (password == null || password.isEmpty()) {
            throw new BadCredentialsException("The email address is unknown, or the password is invalid");
        }

        Gardener u = gardenerFormService.getUserByEmailAndPassword(email, password).orElse(null);
        if (u == null) {
            throw new BadCredentialsException("The email address is unknown, or the password is invalid");
        }
//        logger.info("AUTHENTICATIONNNNNNNNNNNNNNNNNNNNNNNNNN");
//        logger.info(String.valueOf(u));
//        logger.info(u.getAuthorities().toString());
        if ((u.getAuthorities().isEmpty())) {
            throw new BadCredentialsException("Email not verified");
        }
        return new UsernamePasswordAuthenticationToken(u.getEmail(), null, u.getAuthorities());
    }

    /**
     * Function used by spring security to ensure that authentication is of the correct class
     * @param authentication - the authentication object to check
     * @return true if @param authentication is of class UsernamePasswordAuthenticationToken
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

