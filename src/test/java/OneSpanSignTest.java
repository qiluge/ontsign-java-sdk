import com.ontology.ontsign.bean.OntSigner;
import com.ontology.ontsign.provider.OneSpanSign;
import com.silanis.esl.sdk.EslClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class OneSpanSignTest {
    OneSpanSign api;

    public static final String API_KEY = "T09PT1E2UldaYXdPOmNUS0ZBQjhSc01BUw==";
    public static final String API_URL = "https://sandbox.esignlive.com/api";

    @Before
    public void init() {
        EslClient client = new EslClient(API_KEY, API_URL);
        api = new OneSpanSign(client);
    }

    @Test
    // if signer email is account email, onespan sign couldn't sent email again
    public void testSign() throws Exception {
        List<OntSigner> signers = new ArrayList<>();
        signers.add(new OntSigner("qiluge", "921444844@qq.com"));
        signers.add(new OntSigner("132", "1327055625@qq.com"));
        List<String> cc = new ArrayList<>();
        cc.add("lawyer@hellosign.com");
        cc.add("lawyer@example.com");
        String id = api.signDocByEmail("please sign this doc", "test", "test.pdf",
                signers, cc);
        Assert.assertNotNull(id);
    }

    @Test
    public void testIsCompleted() throws Exception {
        String id = "HTiAvLAKAMU4WTpxy1CLyABg4V8=";
        boolean completed = api.isCompleted(id);
        Assert.assertTrue(completed);
    }
}
