package nz.ac.canterbury.seng302.gardenersgrove.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * This is a basic spring boot controller, note the @link{Controller} annotation which defines this.
 * This controller defines endpoints as functions with specific HTTP mappings. This controller is used
 * for all the login related routes, as well as routing to the login page by default
 */
@Controller
public class LoginController {
    Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * Redirects GET default url '/' to '/login'
     * @return redirect to /login
     */
    @GetMapping("/")
    public String home() {
        logger.info("GET /");
        return "redirect:/login";
    }

    /**
     * Method for the /login route
     * @return redirect to /login if not authenticated, otherwise go to the main page
     */
    @GetMapping("/login")
    public String login(Authentication authentication) {
        logger.info("GET /");
        logger.info("Authentication: " + authentication);

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return "redirect:/gardens";
        }
        return "login";
    }

}
