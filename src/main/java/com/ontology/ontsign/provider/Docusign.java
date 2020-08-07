package com.ontology.ontsign.provider;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.Envelope;

public class Docusign extends EnvelopesApi implements ESignatureProvider {
    private String accountId;

    public Docusign() {
        super();
    }

    public Docusign(ApiClient client) {
        super(client);
    }

    public Docusign(ApiClient client, String accountId) {
        super(client);
        this.accountId = accountId;
    }

    @Override
    public boolean isCompleted(String envelopeId) {
        try {
            Envelope envelope = getEnvelope(accountId, envelopeId);
            return "completed".equals(envelope.getStatus());
        } catch (ApiException e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
    }
}
