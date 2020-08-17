package com.ontology.ontsign.provider;

import com.docusign.esign.client.ApiException;
import com.hellosign.sdk.HelloSignException;
import com.ontology.ontsign.bean.OntSigner;

import java.io.IOException;
import java.util.List;

public interface ESignatureProvider {

    public void updateOAuthToken(String token);

    public boolean isCompleted(String requestId);

    public String signDocByEmail(String subject, String docName, String filePath, List<OntSigner> ontSigners,
                                 List<String> cc) throws Exception;
}
