package nz.ac.canterbury.seng302.gardenersgrove.controller;

import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * Controller for displaying Open Street Maps through JavaFX webview
 * @author Morgan English
 */
@Controller
public class LeafletOSMController {
    private static final Logger log = LogManager.getLogger(LeafletOSMController.class);
//    private JavaScriptBridge javaScriptBridge;
    private JSObject javaScriptConnector;


    @GetMapping("/map")
    public String map() {
        return "mapTemplate";
    }

}

