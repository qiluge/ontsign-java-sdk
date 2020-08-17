import com.alibaba.fastjson.JSON;
import com.docusign.esign.client.ApiClient;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.sdk.wallet.Identity;
import com.ontology.ontsign.OntSign;
import com.ontology.ontsign.SignedFileInfo;
import com.ontology.ontsign.bean.OntSigner;
import com.ontology.ontsign.provider.Docusign;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;

public class DocuSignTest {

    String accessToken = "eyJ0eXAiOiJNVCIsImFsZyI6IlJTMjU2Iiwia2lkIjoiNjgxODVmZjEtNGU1MS00Y2U5LWFmMWMtNjg5ODEyMjAzMzE3In0.AQsAAAABAAUABwCANYh4TkLYSAgAgHWrhpFC2EgCAEDxWRIGnSFKsfKGXlLCefwVAAEAAAAYAAEAAAAFAAAADQAkAAAANjY4YWU2ZmItOGY2Yy00MmU3LTkyMTctMTQ4MjZkNTAxZmJjIgAkAAAANjY4YWU2ZmItOGY2Yy00MmU3LTkyMTctMTQ4MjZkNTAxZmJjEgABAAAACwAAAGludGVyYWN0aXZlMAAAn-93TkLYSDcAIPqR6CZR-USQiX7FnTH-3w.R7BmEZyil2-TW9UHntl-MAy06oPcgjptYnSYO4dBqZ67TnER7kQk60PaMEHf9dUNRUCh0b6c3FO6gw4ymRDAyTUqLjgZO7ZrYWLdjjZZ2KqpnQg_Jyewu3TZf_ftf0CPR18SxxhdW2eA8WW7wF05GkjvAh-MIf6WuQmnDH9WNmhrPFw3ILWnmh2k2RjOSyD4dAhqrhOxg5KeM0Ta9gj2PVRTMjHcusChF9uoSbTERUUzyHM_ScJSizVNivEvRU9c3ry7WTDbbc1H_OuhrJytw9HX0afKm9WdQ-webNGt4FKADwgonfQ1GXMiM8j4kFCgg0v07kSeDjdq9yP-tSRQcA";

    String BEARER_AUTHENTICATION = "Bearer ";
    String basePath = "https://demo.docusign.net/restapi";
    String ip = "http://polaris1.ont.io";
    String restUrl = ip + ":" + "20334";
    String contractAddr = "dc05523d0f451e11290e5505292dfabff3e9a4de";
    String password = "passwordtest";
    String accountId = "11126583";
    String contentHash = "aaa";

    Account payer;
    Identity ownerDID;
    Account owner;
    OntSign api;

    @Before
    public void init() throws Exception {
        ApiClient apiClient = new ApiClient(basePath);
        apiClient.addDefaultHeader(HttpHeaders.AUTHORIZATION, BEARER_AUTHENTICATION + accessToken);
        apiClient.addAuthorization("docusignAccessCode", null);
        Docusign docusign = new Docusign(apiClient, accountId);
        api = new OntSign(docusign, restUrl, contractAddr, 2500, 5000000);
        OntSdk sdk = OntSdk.getInstance();
        sdk.openWalletFile("wallet.json");
        payer = sdk.getWalletMgr().getAccount("ATqpnrgVjzmkeHEqPiErnsxTEgi5goor2e", password);
        ownerDID = sdk.getWalletMgr().getDefaultIdentity();
        owner = sdk.getWalletMgr().getAccount(ownerDID.ontid, password, ownerDID.controls.get(0).getSalt());
    }

    @Test
    public void TestSignFile() throws Exception {
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
    public void TestCommitSignedFileInfo() throws Exception {
        String envelopeId = "08f4d501-d75a-4449-973a-4b21883e1018";
        List<String> signers = new ArrayList<>();
        signers.add("did:ont:ATqpnrgVjzmkeHEqPiErnsxTEgi5goor2e");
        signers.add("did:ont:AVwVTBTzztbrwY5ByJfjmN6WFHpxtTYgFN");
        SignedFileInfo signedFileInfo = new SignedFileInfo(ownerDID.ontid, contentHash,
                envelopeId, signers);
        String txHash = api.commitSignedFileInfo(payer, owner, 1, accountId, signedFileInfo);
        Assert.assertNotNull(txHash);
    }

    @Test
    public void TestGetSignedFileInfo() throws Exception {
        SignedFileInfo signedFileInfo = api.getSignedFileInfo(contentHash);
        String jsonSignedInfo = JSON.toJSONString(signedFileInfo);
        Assert.assertNotNull(jsonSignedInfo);
    }

    @Test
    public void TestDeleteSignedFileInfo() throws Exception {
        String txHash = api.deleteSignedFileInfo(payer, owner, ownerDID.ontid, 1, contentHash);
        Assert.assertNotNull(txHash);
    }
}
