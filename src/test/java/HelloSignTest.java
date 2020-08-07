import com.hellosign.sdk.HelloSignClient;
import com.hellosign.sdk.resource.SignatureRequest;
import com.hellosign.sdk.resource.support.OauthData;
import com.ontology.ontsign.provider.HelloSign;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.net.URI;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HelloSignTest {
    String apiKey = "4f22d2ac21c4435a245d91f779cd0a2bf0ba14ff32c8e592dde89c42c7c11dfd";
    HelloSignClient client;

    @Before
    public void init() {
        client = new HelloSign(apiKey);
    }

    @Test
    public void TestHelloSignOAuth() throws Exception {
        String state = "randomValue";
        String clientID = "17f5be021e47b2628ee367a3d79a3f39";
//        String url = "https://app.hellosign.com/oauth/authorize?response_type=code" +
//                "&client_id=" + clientID +
//                "&state=" + state;
//        URI oauthUri = new URI(url);
//        Desktop.getDesktop().browse(oauthUri);
        String code = "569e7eff88558726";
        String secret = "32284fd8e49a4e794d5a8072c08e89f2";
        OauthData data = client.getOauthData(code, clientID, secret, state, true);
        assertNotNull(data);
        assertNotNull(data.getAccessToken());
    }

    @Test
    public void TestHelloSign() throws Exception {
        SignatureRequest request = new SignatureRequest();
        request.setTitle("NDA with Acme Co.");
        request.setSubject("The NDA we talked about");
        request.setMessage("Please sign this NDA and then we can discuss more. Let me know if you have any questions.");
        request.addSigner("wangcheng@onchain.com", "wangcheng");
        request.addSigner("921444844@qq.com", "qiluge");
        request.addCC("lawyer@hellosign.com");
        request.addCC("lawyer@example.com");
        request.addFile(new File("test.pdf"));
        request.setTestMode(true);

        SignatureRequest newRequest = client.sendSignatureRequest(request);
        assertNotNull(newRequest);
    }

    @Test
    public void TestIsCompleted() throws Exception {
        String requestId = "b59353d9c3b6a4f5c8a96658787cefb317ef6e40";
        SignatureRequest request = client.getSignatureRequest(requestId);
        assertTrue(request.isComplete());
    }
}
