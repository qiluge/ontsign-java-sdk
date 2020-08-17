package com.ontology.ontsign.provider;

import com.ontology.ontsign.bean.OntSigner;
import com.silanis.esl.sdk.*;
import com.silanis.esl.sdk.builder.DocumentBuilder;
import com.silanis.esl.sdk.builder.PackageBuilder;
import com.silanis.esl.sdk.builder.SignatureBuilder;
import com.silanis.esl.sdk.builder.SignerBuilder;

import java.util.List;

import static com.silanis.esl.sdk.builder.PackageBuilder.newPackageNamed;

public class OneSpanSign implements ESignatureProvider {

    private EslClient client;

    public OneSpanSign(EslClient client) {
        this.client = client;
    }

    @Override
    public void updateOAuthToken(String token) {
        // TODO:
    }

    @Override
    // no use cc param
    public String signDocByEmail(String subject, String docName, String filePath, List<OntSigner> ontSigners,
                                 List<String> cc) throws Exception {
        PackageBuilder builder = newPackageNamed(docName);
        DocumentBuilder documentBuilder = DocumentBuilder.newDocumentWithName(docName);
        documentBuilder.fromFile(filePath);
        int positionX = 175;
        for (OntSigner signer : ontSigners) {
            builder.withSigner(SignerBuilder.newSignerWithEmail(signer.email)
                    .withFirstName(signer.name)
                    .withLastName(signer.name));
            documentBuilder.withSignature(SignatureBuilder.signatureFor(signer.email)
                    .onPage(0)
                    .atPosition(positionX, 165));
            positionX += 375;
        }
        builder.withDocument(documentBuilder);
        DocumentPackage documentPackage = builder.build();
        PackageId id = client.createPackageOneStep(documentPackage);
        client.sendPackage(id);
        return id.getId();
    }

    @Override
    public boolean isCompleted(String requestId) {
        PackageId pId = new PackageId(requestId);
        DocumentPackage p = this.client.getPackage(pId);
        List<Document> documents = p.getDocuments();
        List<Signer> signers = p.getSigners();
        for (Document d : documents) {
            for (Signer s : signers) {
                SignerId sId = new SignerId(s.getId());
                SigningStatus status = this.client.getSigningStatus(pId, sId, d.getId());
                if (status != SigningStatus.COMPLETE) {
                    return false;
                }
            }
        }
        return true;
    }
}
