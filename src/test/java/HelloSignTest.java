import com.hellosign.sdk.HelloSignClient;
import com.hellosign.sdk.resource.SignatureRequest;
import com.hellosign.sdk.resource.support.OauthData;
import com.ontology.ontsign.bean.OntSigner;
import com.ontology.ontsign.provider.HelloSign;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HelloSignTest {
    String apiKey = "4f22d2ac21c4435a245d91f779cd0a2bf0ba14ff32c8e592dde89c42c7c11dfd";
    HelloSign client;

    @Before
    public void init() throws Exception {
        client = new HelloSign(apiKey);
    }

    @Test
    public void oauthHelloSign() throws Exception {
        String state = "randomValue";
        String clientID = "17f5be021e47b2628ee367a3d79a3f39";
//        String url = "https://app.hellosign.com/oauth/authorize?response_type=code" +
//                "&client_id=" + clientID +
//                "&state=" + state;
//        URI oauthUri = new URI(url);
//        Desktop.getDesktop().browse(oauthUri);
        String code = "1cf392ca56b2a88f";
        String secret = "32284fd8e49a4e794d5a8072c08e89f2";
        OauthData data = client.getOauthData(code, clientID, secret, state, true);
        assertNotNull(data);
        assertNotNull(data.getAccessToken());
    }

    @Test
    public void TestHelloSign() throws Exception {
        java.util.List<OntSigner> signers = new ArrayList<>();
        signers.add(new OntSigner("qiluge", "921444844@qq.com"));
        signers.add(new OntSigner("wangcheng", "wangcheng@onchain.com"));
        List<String> cc = new ArrayList<>();
        cc.add("lawyer@hellosign.com");
        cc.add("lawyer@example.com");

        String id = client.signDocByEmail("please sign this doc", "test",
                "test.pdf", signers, cc);
        assertNotNull(id);
    }

    @Test
    public void TestIsCompleted() throws Exception {
        String requestId = "98a8152118c36b676aa519072b86888fa3118be9";
        SignatureRequest request = client.getSignatureRequest(requestId);
        assertTrue(request.isComplete());
    }
}
