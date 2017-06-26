package vn.viettel.browser.ultils;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * Created by quytx on 4/24/2017.
 * Project: vn.viettel.browser.ultils:Social_Login
 */
public class VerifyIdToken {
    public JSONObject verifyIdToken(String idTokenString) throws GeneralSecurityException, IOException {
        JSONObject jsonObject = new JSONObject();
        String clientId = "420119237558-bauatbc6qo972ekhon2232rc4f8k2jsb.apps.googleusercontent.com";
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(clientId))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        // (Receive idTokenString by HTTPS POST)

        GoogleIdToken idToken = null;

        idToken = verifier.verify(idTokenString);

        if (idToken != null) {
            Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            jsonObject.put("id", userId);
            System.out.println("User ID: " + userId);
            // Get profile information from payload
            String email = payload.getEmail();
            jsonObject.put("email", email);
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            jsonObject.put("locale", locale);
            String familyName = (String) payload.get("family_name");
            jsonObject.put("family_name", familyName);
            String givenName = (String) payload.get("given_name");
            jsonObject.put("given_name", givenName);

            // Use or store profile information
            // ...

        } else {
            System.out.println("Invalid ID token.");
        }
        return jsonObject;
    }
}
