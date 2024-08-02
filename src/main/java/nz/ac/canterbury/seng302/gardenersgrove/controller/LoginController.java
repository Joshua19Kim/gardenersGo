package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.net.URISyntaxException;


/**
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
     * If user is authenticated, redirects to the main page.
     * If not authenticated, renders the login page.
     */
    @GetMapping("/login")
    public String login(Authentication authentication, HttpServletResponse response, HttpServletRequest request) throws IOException, URISyntaxException {
        logger.info("GET /");
        logger.info("Authentication: " + authentication);
        logger.info("DB USERNAME: " + System.getenv("DB_USERNAME"));
        HttpSession session = request.getSession();
        if (session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION") != null ) {
            if (session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION").toString().contains("Email not verified")) {
                session.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", new BadCredentialsException(""));
                return "redirect:/signup";
            }
        }
        // Prevent caching of the page so that we always reload it when we reach it (mainly for when you use the browser back button)
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setHeader("Expires", "0"); // Proxies

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return "redirect:/home";
        }

        return "loginForm";
    }

}
