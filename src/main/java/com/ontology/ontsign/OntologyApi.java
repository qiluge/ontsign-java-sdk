package com.ontology.ontsign;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.Envelope;
import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.EnvelopeSummary;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.smartcontract.neovm.abi.AbiFunction;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;
import com.github.ontio.smartcontract.neovm.abi.Parameter;

import java.util.ArrayList;
import java.util.List;

public class OntologyApi extends EnvelopesApi {

    private OntSdk ontSdk;
    private long gasPrice;
    private long gasLimit;
    private String contractAddr;
    private String nodeRestURL;

    public OntologyApi(String nodeRestURL, String contractAddr, long gasPrice, long gasLimit) throws Exception {
        this.nodeRestURL = nodeRestURL;
        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful(nodeRestURL);
        ontSdk.setDefaultConnect(ontSdk.getRestful());
        this.contractAddr = contractAddr;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
    }

    public OntologyApi(ApiClient apiClient, String nodeRestURL, String contractAddr, long gasPrice, long gasLimit)
            throws Exception {
        super(apiClient);
        this.nodeRestURL = nodeRestURL;
        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful(nodeRestURL);
        ontSdk.setDefaultConnect(ontSdk.getRestful());
        this.contractAddr = contractAddr;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
    }

    public void updateContractAddr(String contractAddr) {
        this.contractAddr = contractAddr;
    }

    public void updateSDKUrl(String nodeRestURL) {
        ontSdk.setRestful(nodeRestURL);
    }

    public String commitSignedFileInfo(Account payer, Account ownerOntIdSigner, long signerPubKeyIndex,
                                       String docusignAccountId, SignedFileInfo signedFileInfo) throws Exception {
        Envelope envelope = this.getEnvelope(docusignAccountId, signedFileInfo.envelopeId);
        if (!"completed".equals(envelope.getStatus())) {
            throw new Exception("envelope is not completed!");
        }
        String name = "commitSignedFileInfo";
        Parameter ownerOntIdParam = new Parameter("ownerOntId", Parameter.Type.String,
                signedFileInfo.ownerOntId);
        Parameter pubKeyIndexParam = new Parameter("signerPubKeyIndex", Parameter.Type.Integer,
                signerPubKeyIndex);
        Parameter contentHashParam = new Parameter("contentHash", Parameter.Type.String,
                signedFileInfo.contentHash);
        Parameter envelopeIdParam = new Parameter("envelopeIdParam", Parameter.Type.String,
                signedFileInfo.envelopeId);
        Parameter signersParam = new Parameter("signers", Parameter.Type.Array, signedFileInfo.signers);
        AbiFunction func = new AbiFunction(name, ownerOntIdParam, pubKeyIndexParam, contentHashParam,
                envelopeIdParam, signersParam);
        byte[] params = BuildParams.serializeAbiFunction(func);
        Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(Helper.reverse(contractAddr), null, params,
                payer.getAddressU160().toBase58(), gasLimit, gasPrice);
        ontSdk.addSign(tx, payer);
        ontSdk.addSign(tx, ownerOntIdSigner);
        boolean success = ontSdk.getConnect().sendRawTransaction(tx.toHexString());
        if (success) {
            return tx.hash().toHexString();
        }
        return "";
    }

    public SignedFileInfo getSignedFileInfo(String contentHash) throws Exception {
        String name = "getSignedFileInfo";
        Parameter contentHashParam = new Parameter("contentHash", Parameter.Type.String, contentHash);
        AbiFunction func = new AbiFunction(name, contentHashParam);
        Object obj = ontSdk.neovm().sendTransaction(Helper.reverse(contractAddr), null, null, 0,
                0, func, true);
        JSONArray res = ((JSONObject) obj).getJSONArray("Result");
        if (res.size() != 4) {
            throw new Exception("illegal envelope");
        }
        String hexOwner = (String) res.get(0);
//        String hexContentHash = (String) res.get(1);
        String hexEnvelopeId = (String) res.get(2);
        JSONArray hexSigners = (JSONArray) res.get(3);
        String owner = new String(Helper.hexToBytes(hexOwner));
//        String contentHash = new String(Helper.hexToBytes(hexContentHash));
        String envelopeId = new String(Helper.hexToBytes(hexEnvelopeId));
        List<String> signers = new ArrayList<>();
        for (Object hexSigner : hexSigners) {
            String signerOntId = (String) hexSigner;
            signers.add(new String(Helper.hexToBytes(signerOntId)));
        }
        return new SignedFileInfo(owner, contentHash, envelopeId, signers);
    }

    public String deleteSignedFileInfo(Account payer, Account ownerOntIdSigner, String ownerOntId, long signerPubKeyIndex,
                                       String contentHash) throws Exception {
        String name = "deleteSignedFileInfo";
        Parameter ownerOntIdParam = new Parameter("ownerOntId", Parameter.Type.String, ownerOntId);
        Parameter pubKeyIndexParam = new Parameter("signerPubKeyIndex", Parameter.Type.Integer,
                signerPubKeyIndex);
        Parameter contentHashParam = new Parameter("contentHash", Parameter.Type.String, contentHash);
        AbiFunction func = new AbiFunction(name, ownerOntIdParam, pubKeyIndexParam, contentHashParam);
        byte[] params = BuildParams.serializeAbiFunction(func);
        Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(Helper.reverse(contractAddr), null, params,
                payer.getAddressU160().toBase58(), gasLimit, gasPrice);
        ontSdk.addSign(tx, payer);
        ontSdk.addSign(tx, ownerOntIdSigner);
        boolean success = ontSdk.getConnect().sendRawTransaction(tx.toHexString());
        if (success) {
            return tx.hash().toHexString();
        }
        return "";
    }

    public OntologyEnvelopeSummary createSignRequest(String accountId, EnvelopeDefinition envelopeDefinition)
            throws ApiException {
        return this.createSignRequest(accountId, envelopeDefinition, null);
    }

    public OntologyEnvelopeSummary createSignRequest(String accountId, EnvelopeDefinition envelopeDefinition,
                                                     EnvelopesApi.CreateEnvelopeOptions options) throws ApiException {

        EnvelopeSummary envelopeSummary = super.createEnvelope(accountId, envelopeDefinition, options);
        return (OntologyEnvelopeSummary) envelopeSummary;
    }
}
