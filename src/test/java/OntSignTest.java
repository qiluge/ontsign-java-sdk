import com.alibaba.fastjson.JSON;
import com.docusign.esign.client.ApiClient;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.sdk.wallet.Identity;
import com.ontology.ontsign.OntSign;
import com.ontology.ontsign.SignedFileInfo;
import com.ontology.ontsign.provider.Docusign;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;

public class OntSignTest {

    String accessToken = "eyJ0eXAiOiJNVCIsImFsZyI6IlJTMjU2Iiwia2lkIjoiNjgxODVmZjEtNGU1MS00Y2U5LWFmMWMtNjg5ODEyMjAzMzE3In0.AQsAAAABAAUABwAAkk57ujrYSAgAANJxif062EgCAEDxWRIGnSFKsfKGXlLCefwVAAEAAAAYAAEAAAAFAAAADQAkAAAANjY4YWU2ZmItOGY2Yy00MmU3LTkyMTctMTQ4MjZkNTAxZmJjIgAkAAAANjY4YWU2ZmItOGY2Yy00MmU3LTkyMTctMTQ4MjZkNTAxZmJjEgABAAAACwAAAGludGVyYWN0aXZlMACA-7V6ujrYSDcAIPqR6CZR-USQiX7FnTH-3w.f_cSVgyDZufMn0vDiMAoUt-TeLdB7VgaZtZIA_QNx8rjgNWPHNIZ3Aj0EyzizNiww9WZZpnRPxJw3toCUq4u9g6R1PTI3sStr0KT0C1rNecGBA0ZjyLWDoq1zgpgC0yn4TYpB9yRP6u-IETzQXisP-z6R6_pTUqLWUelS6kKeA5IRav7t_c5kY1Dd3kKhsqdCF77FVKFnT9kyUze-hF1yn8xvNhJuHdYllF40zvkCRy00ZRvwtBb7YusXjznwMoqsyagJEClF8hgYlX2qPdmYViBCiM-AxFCPO61lOUbmx8Dz0kStfB0ufnArBxnDcllEabDJvFePvS4koiho2hPaQ";

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
    public void TestCommitSignedFileInfo() throws Exception {
        String envelopeId = "2f295ec6-dbf1-4a79-a1b5-082cd35ac378";
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
