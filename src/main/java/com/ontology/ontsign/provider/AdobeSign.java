package com.ontology.ontsign.provider;

import com.ontology.ontsign.bean.OntSigner;
import io.swagger.client.api.AgreementsApi;
import io.swagger.client.api.BaseUrisApi;
import io.swagger.client.api.TransientDocumentsApi;
import io.swagger.client.model.ApiClient;
import io.swagger.client.model.ApiException;
import io.swagger.client.model.agreements.*;
import io.swagger.client.model.baseUris.BaseUriInfo;
import io.swagger.client.model.transientDocuments.TransientDocumentResponse;

import java.io.File;
import java.util.List;

// TODO: confirm adobe sign sdk is usable or not

public class AdobeSign implements ESignatureProvider {
    private String authorization;
    private ApiClient apiClient;
    private AgreementsApi api;


    //Default baseUrl to make GET /baseUris API call.
    private final static String baseUrl = "https://api.na2.echosign.com";
    private final static String endpointUrl = "/api/rest/v6";


    public AdobeSign(String authorization) throws ApiException {
        this.authorization = authorization;

        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(baseUrl + endpointUrl);
        BaseUrisApi baseUrisApi = new BaseUrisApi(apiClient);
        BaseUriInfo baseUriInfo = baseUrisApi.getBaseUris(authorization);
        apiClient.setBasePath(baseUriInfo.getApiAccessPoint() + endpointUrl);
        this.apiClient = apiClient;
        api = new AgreementsApi(apiClient);
    }


    @Override
    public void updateOAuthToken(String token) {
        this.authorization = token;
        BaseUrisApi baseUrisApi = new BaseUrisApi(apiClient);
        try {
            BaseUriInfo baseUriInfo = baseUrisApi.getBaseUris(authorization);
            apiClient.setBasePath(baseUriInfo.getApiAccessPoint() + endpointUrl);
            api = new AgreementsApi(apiClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String signDocByEmail(String subject, String docName, String filePath, List<OntSigner> ontSigners,
                                 List<String> cc) throws Exception {
        File file = new File(filePath);
        String xApiUser = null;
        String xOnBehalfOfUser = null;
        String mimeType = "application/pdf";

        //Get the id of the transient document.
        TransientDocumentsApi transientDocumentsApi = new TransientDocumentsApi(apiClient);
        TransientDocumentResponse response = transientDocumentsApi.createTransientDocument(authorization, file,
                xApiUser, xOnBehalfOfUser, filePath, mimeType);
        String transientDocumentId = response.getTransientDocumentId();

        //prepare request body for agreement creation.
        AgreementCreationInfo agreementInfo = new AgreementCreationInfo();
        agreementInfo.setName(subject);
        agreementInfo.setSignatureType(AgreementCreationInfo.SignatureTypeEnum.ESIGN);
        agreementInfo.setState(AgreementCreationInfo.StateEnum.DRAFT);

        FileInfo fileInfo = new FileInfo();
        fileInfo.setTransientDocumentId(transientDocumentId);
        agreementInfo.addFileInfosItem(fileInfo);

        ParticipantSetInfo signerInfo = new ParticipantSetInfo();
        for (OntSigner signer : ontSigners) {
            ParticipantSetMemberInfo participantSetMemberInfo = new ParticipantSetMemberInfo();
            participantSetMemberInfo.setEmail(signer.email);
            signerInfo.addMemberInfosItem(participantSetMemberInfo);
        }
        signerInfo.setRole(ParticipantSetInfo.RoleEnum.SIGNER);
        signerInfo.setOrder(1);
        agreementInfo.addParticipantSetsInfoItem(signerInfo);

        ParticipantSetInfo ccInfo = new ParticipantSetInfo();
        for (String c : cc) {
            ParticipantSetMemberInfo participantSetMemberInfo = new ParticipantSetMemberInfo();
            participantSetMemberInfo.setEmail(c);
            ccInfo.addMemberInfosItem(participantSetMemberInfo);
        }
        ccInfo.setRole(ParticipantSetInfo.RoleEnum.SHARE);
        agreementInfo.addParticipantSetsInfoItem(ccInfo);

        //Create agreement using the transient document.
        AgreementCreationResponse agreementCreationResponse = api.createAgreement(authorization,
                agreementInfo, xApiUser, xOnBehalfOfUser);
        return agreementCreationResponse.getId();
    }

    @Override
    public boolean isCompleted(String requestId) {
        try {
            AgreementInfo info = api.getAgreementInfo(authorization, requestId, "", "",
                    null);
            if (info == null) {
                return false;
            }
            if (requestId.equals(info.getId())) {
                return info.getStatus() == AgreementInfo.StatusEnum.SIGNED;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
