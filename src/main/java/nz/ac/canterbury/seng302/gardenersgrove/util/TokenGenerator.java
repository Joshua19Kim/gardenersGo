package nz.ac.canterbury.seng302.gardenersgrove.util;

import java.util.HashSet;
import java.util.Set;
import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
// referenced chatgpt to make https://chat.openai.com/share/703cdffb-d8ec-44b1-9673-013470739051
public class TokenGenerator {
    private static final int TOKEN_LENGTH = 6;
    public static String generateToken() {
        SecureRandom random = new SecureRandom(); // Compliant for security-sensitive use cases - as per SonarQube
        StringBuilder token = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Set<String> generatedTokens = new HashSet<>();

        while (generatedTokens.size() < TOKEN_LENGTH) {
            int index = random.nextInt(characters.length());
            token.append(characters.charAt(index));
            generatedTokens.add(token.toString());
        }
        return token.toString();
    }
}
