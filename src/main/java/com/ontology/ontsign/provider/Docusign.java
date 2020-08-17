package com.ontology.ontsign.provider;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.*;
import com.ontology.ontsign.bean.OntSigner;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

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
    public void updateOAuthToken(String token) {
        // it seems that this param is not used
        this.getApiClient().updateAccessToken();
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

    // use the simplest feature for the time being
    @Override
    public String signDocByEmail(String subject, String docName, String filePath, List<OntSigner> ontSigners,
                                 List<String> cc) throws Exception {
        // generate signer
        int receiptId = 1;
        List<Signer> docuSigners = new ArrayList<>();
        for (OntSigner signer : ontSigners) {
            Signer docuSigner = new Signer();
            docuSigner.setName(signer.name);
            docuSigner.setEmail(signer.email);
            docuSigner.setRecipientId(String.valueOf(receiptId));
            docuSigner.setRoutingOrder("2");
            docuSigner.setTabs(new Tabs());
            receiptId++;
            docuSigners.add(docuSigner);
        }
        // generate cc
        List<CarbonCopy> docuCC = new ArrayList<>();
        for (String copy : cc) {
            CarbonCopy docuCopy = new CarbonCopy();
            docuCopy.setName(copy);
            docuCopy.setEmail(copy);
            docuCopy.setRecipientId(String.valueOf(receiptId));
            docuCopy.setRoutingOrder("2");
            receiptId++;
            docuCC.add(docuCopy);
        }
        // create document
        // TODO: add new feature, such as document template
        Document document = createDocumentFromFile(filePath, docName, "1");
        // create envelope
        EnvelopeDefinition envelope = new EnvelopeDefinition();
        envelope.setDocuments(Collections.singletonList(document));
        Recipients recipients = new Recipients();
        recipients.setSigners(docuSigners);
        recipients.setCarbonCopies(docuCC);
        envelope.setRecipients(recipients);
        envelope.setEmailSubject(subject);
        envelope.setStatus("sent");
        EnvelopeSummary results = this.createEnvelope(accountId, envelope);
        return results.getEnvelopeId();
    }

    /**
     * Loads document from a file and creates a document object that represents
     * loaded document.
     *
     * @param fileName name of the file to load document; the extension of the
     *                 loading file determines an extension of the created document
     * @param docName  the name of the document; it may be differ from the file
     * @param docId    identifier of the created document
     * @return the {@link Document} object
     * @throws IOException if document cannot be loaded due to some reason
     */
    public Document createDocumentFromFile(String fileName, String docName, String docId) throws IOException {
        File file = new File(fileName);
        InputStream stream = new FileInputStream(file);
        byte[] buffer = StreamUtils.copyToByteArray(stream);
        String extension = FilenameUtils.getExtension(fileName);
        return createDocument(buffer, docName, extension, docId);
    }

    public Document createDocument(byte[] data, String documentName, String fileExtension, String documentId) {
        Document document = new Document();
        document.setDocumentBase64(Base64.getEncoder().encodeToString(data));
        document.setName(documentName);
        document.setFileExtension(fileExtension);
        document.setDocumentId(documentId);
        return document;
    }
}
