package com.ontology.ontsign;

import java.util.List;

public class SignedFileInfo {
    public String ownerOntId;
    public String contentHash;
    public String envelopeId;
    public List<String> signers;

    public SignedFileInfo(String ownerOntId, String contentHash, String envelopeId, List<String> signers) {
        this.ownerOntId = ownerOntId;
        this.contentHash = contentHash;
        this.envelopeId = envelopeId;
        this.signers = signers;
    }
}
