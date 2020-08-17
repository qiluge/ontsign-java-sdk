import com.ontology.ontsign.bean.OntSigner;
import com.ontology.ontsign.provider.AdobeSign;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AdobeSignTest {
    private AdobeSign api;

    String Bearer = "Bearer ";

    @Before
    public void init() throws Exception {
        api = new AdobeSign(Bearer +
                "3AAABLblqZhBxj6L3wmqYv1RdIA689X-9WMag9S30lzCVgBJmfwL8YWDNTjgj4ZbDCyMyxGA7W2VPQouIa91I2L3tAsBQETTL");
    }

    @Test
    public void TestSignFileByEmail() throws Exception {
        List<OntSigner> signers = new ArrayList<>();
        signers.add(new OntSigner("qiluge", "921444844@qq.com"));
        signers.add(new OntSigner("wangcheng", "wangcheng@onchain.com"));
        List<String> cc = new ArrayList<>();
        cc.add("lawyer@hellosign.com");
        cc.add("lawyer@example.com");
        String id = api.signDocByEmail("please sign this doc", "test", "test.pdf",
                signers, cc);
        Assert.assertNotNull(id);
    }

    @Test
    public void TestIsCompleted() {
        String id = "CBJCHBCAABAAP-9wSJXd-PUJVqjnNJVV-aSL9ERVem50";
        boolean completed = api.isCompleted(id);
        Assert.assertTrue(completed);
    }
}
