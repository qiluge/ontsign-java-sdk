# OntSign API Document

[OntSign](src/main/java/com/ontology/ontsign/OntSign.java) is the interface of OntSign.

[ESignatureProvider](src/main/java/com/ontology/ontsign/provider/ESignatureProvider.java) is the unified interface of every
e-signature service.

## OntSign

### Initialize

There are 2 constructors that one need a ESignatureProvider and another not.

#### Initialize Without ApiClient

**public OntSign(String nodeRestURL, String contractAddr, long gasPrice, long gasLimit)**

| | name | desc |
| --- | --- | --- |
| 1 | nodeRestURL | restful url of ontology node |
| 2 | contractAddr | record contract address |
| 3 | gasPrice | gas price, recommend 2500 currently |
| 4 | gasLimit | gas limit |

#### Initialize With ApiClient

**public OntSign(ESignatureProvider provider, String nodeRestURL, String contractAddr, long gasPrice, long gasLimit)**

| | name | desc |
| --- | --- | --- |
| 1 | provider | a client abstraction of e-signature service provider |
| 2 | nodeRestURL | restful url of ontology node |
| 3 | contractAddr | record contract address |
| 4 | gasPrice | gas price, recommend 2500 currently |
| 5 | gasLimit | gas limit |

### Commit Signed File Info

**public String commitSignedFileInfo(Account payer, Account ownerOntIdSigner, long signerPubKeyIndex,
                                         SignedFileInfo signedFileInfo)**

| | name | desc |
| --- | --- | --- |
| 1 | payer | tx payer, should sign this tx and pay handling fee |
| 2 | ownerOntIdSigner | sponsor of pact, should be an ontID signer |
| 3 | signerPubKeyIndex | the index of the sponsor ONTID public key used to sign |
| 4 | signedFileInfo | signed file info |

### Delete Signed File Info

**public String deleteSignedFileInfo(Account payer, Account ownerOntIdSigner, String ownerOntId, long signerPubKeyIndex,
                                         String contentHash)**

| | name | desc |
| --- | --- | --- |
| 1 | payer | tx payer, should sign this tx and pay handling fee |
| 2 | ownerOntIdSigner | sponsor of pact, should be an ontID signer |
| 3 | ownerOntId | ontId of sponsor of pact |
| 4 | signerPubKeyIndex | the index of the sponsor ONTID public key used to sign |
| 5 | contentHash | hash of signed file |

### Get Signed File Info

**public SignedFileInfo getSignedFileInfo(String contentHash)**

| | name | desc |
| --- | --- | --- |
| 1 | contentHash | hash of signed file |

### Sign Document by Email

**public String signDocByEmail(String subject, String docName, String filePath, List<OntSigner> ontSigners,
                                 List<String> cc)**

| | name | desc |
| --- | --- | --- |
| 1 | subject | signature email subject |
| 2 | docName | name of signature document |
| 3 | filePath | full path of signature file |
| 4 | ontSigners | list of signers |
| 5 | cc | list of  carbon copy |

## ESignatureProvider

### Update OAuthToken

Each e-signature use oauth to access user permission, so there need to update oauth token every once in a while.

**public void updateOAuthToken(String token)**

| | name | desc |
| --- | --- | --- |
| 1 | token | oauth token |

### Sign Document by Email

Same with [OntSign](#Sign-Document-by-Email)

**public String signDocByEmail(String subject, String docName, String filePath, List<OntSigner> ontSigners,
                                 List<String> cc)**

| | name | desc |
| --- | --- | --- |
| 1 | subject | signature email subject |
| 2 | docName | name of signature document |
| 3 | filePath | full path of signature file |
| 4 | ontSigners | list of signers |
| 5 | cc | list of  carbon copy |

### Judge Signature is Completed

**public boolean isCompleted(String requestId)**


| | name | desc |
| --- | --- | --- |
| 1 | requestId | id of each signature |