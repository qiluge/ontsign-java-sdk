import com.alibaba.fastjson.JSON;
import com.docusign.esign.client.ApiClient;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.sdk.wallet.Identity;
import com.ontology.ontsign.OntologyApi;
import com.ontology.ontsign.SignedFileInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;

public class OntologyApiTest {

    String accessToken = "eyJ0eXAiOiJNVCIsImFsZyI6IlJTMjU2Iiwia2lkIjoiNjgxODVmZjEtNGU1MS00Y2U5LWFmMWMtNjg5ODEyMjAzMzE3In0.AQsAAAABAAUABwCAR7Vr7TnYSAgAgIfYeTA62EgCAEDxWRIGnSFKsfKGXlLCefwVAAEAAAAYAAEAAAAFAAAADQAkAAAANjY4YWU2ZmItOGY2Yy00MmU3LTkyMTctMTQ4MjZkNTAxZmJjIgAkAAAANjY4YWU2ZmItOGY2Yy00MmU3LTkyMTctMTQ4MjZkNTAxZmJjEgABAAAACwAAAGludGVyYWN0aXZlMAAAsRxr7TnYSDcAIPqR6CZR-USQiX7FnTH-3w.svK420OnjbSa7dPRNrq2FBRYFjEP1pMmH666MIRIWx6vPHq6Ib0P09YVr8ylgR4dByPC1T5VhwJ0lXvPLU4hYWldo-1eKjZbJRQq4DTqYk9Fbu6RjKf440GEtoSB1O3CV4SYNu9dY2ikBRpSqhfeKot9Y1Nwp3CfXTl8OrumEzv8xuUWj0ajauVSpaDMVUcROrOU6Zz_JjK2TqUQANiJeRQlhr2QibzhXQyjbYT36GvGs7yYDT_IDF8gnirUxOKFke_wqmYrd4R_dvTT6cDAmkmTUyis-beO2mEvbhGiAWIpLa2OIe34jNwNUn3fRtLfJVYzc_MyFZNEr7unKQEVeg";

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
    OntologyApi api;

    @Before
    public void init() throws Exception {
        ApiClient apiClient = new ApiClient(basePath);
        apiClient.addDefaultHeader(HttpHeaders.AUTHORIZATION, BEARER_AUTHENTICATION + accessToken);
        apiClient.addAuthorization("docusignAccessCode", null);
        api = new OntologyApi(apiClient, restUrl, contractAddr, 2500, 5000000);
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
