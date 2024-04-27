package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


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
    public String login(Authentication authentication, HttpServletResponse response) {
        logger.info("GET /");
        logger.info("Authentication: " + authentication);
        logger.info("DB USERNAME: " + System.getenv("DB_USERNAME"));

        // Prevent caching of the page so that we always reload it when we reach it (mainly for when you use the browser back button)
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setHeader("Expires", "0"); // Proxies

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return "redirect:/gardens";
        }
        return "login";
    }

}
