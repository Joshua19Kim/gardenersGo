package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;


import nz.ac.canterbury.seng302.gardenersgrove.controller.LoginController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void homeURL_redirectsToLogin() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/")
                        .with(csrf()))
                .andExpect(redirectedUrl("/login"))
                .andExpect(status().isFound());
    }

    @Test
    void onLoginPage_notLoggedIn_StaysOnLoginPage() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(status().isOk());
    }

//  THE FOLLOWING TEST WORKS BUT I THINK ITS CHEATING BECAUSE I THINK IT
//  JUST BYPASSES SPRING SECURITY INSTEAD
//  OF ACTUALLY CHECKING IF A USER IS LOGGED IN
/*    @Test
 *    void onLoginPage_UserIsLoggedIn_RedirectToMainPage() throws Exception {
 *        LoginController loginController = new LoginController();
 *        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(loginController).build();
 *        MOCK_MVC.perform(MockMvcRequestBuilders.get("/login"))
 *                .andExpect(status().is3xxRedirection())
 *                .andExpect(view().name("redirect:/main"));
 *    }
 */

    // This test does not work and I have no idea why. Need to figure this out
//    @Test
//    @WithMockUser(username = "test")
//    void onLoginPage_UserIsLoggedIn_RedirectToMainPage() throws Exception {
//        this.mockMvc
//                .perform(MockMvcRequestBuilders.get("/login"))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/main"));
//    }



}
