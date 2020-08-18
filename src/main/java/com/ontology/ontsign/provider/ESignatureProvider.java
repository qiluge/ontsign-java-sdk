package com.ontology.ontsign.provider;

import com.ontology.ontsign.bean.OntSigner;

import java.util.List;

public interface ESignatureProvider {

    public void updateOAuthToken(String token);

    public String signDocByEmail(String subject, String docName, String filePath, List<OntSigner> ontSigners,
                                 List<String> cc) throws Exception;

    public boolean isCompleted(String requestId);
}
