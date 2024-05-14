package nz.ac.canterbury.seng302.gardenersgrove.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class RequestService {

    /**
     * Gets the current URI and removes the contextPath to ensure it works on deployed instances.
     * This URI is used to redirect to the previous page from the create garden form.
     * @param request the request made by the application
     * @return the current URI used to know what page to go back to in the create garden form
     */
    public String getRequestURI(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null) {
            requestUri = requestUri + "?" + queryString;
        }
        String contextPath = request.getContextPath();
        requestUri = requestUri.replace(contextPath + "/", "/");
        return requestUri;
    }
}
