# API Document of Record Contract of Signed Pact

[OntologyApi](OntologyApi.java) extends [EnvelopesApi](../../api/EnvelopesApi.java). So you can use OntologyApi
just like usage of EnvelopesApi.

## Initialize

There are 2 constructors that one need a ApiClient and another not.

### Initialize Without ApiClient

**public OntologyApi(String nodeRestURL, String contractAddr, long gasPrice, long gasLimit)**

| | name | desc |
| --- | --- | --- |
| 1 | nodeRestURL | restful url of ontology node |
| 2 | contractAddr | record contract address |
| 3 | gasPrice | gas price, recommend 2500 currently |
| 4 | gasLimit | gas limit |

Without ApiClient, you should provide some necessary ontology blockchain param.

In this case, you couldn't access docusign.

### Initialize With ApiClient

**public OntologyApi(ApiClient apiClient, String nodeRestURL, String contractAddr, long gasPrice, long gasLimit)**

| | name | desc |
| --- | --- | --- |
| 1 | apiClient | a client abstraction contaied authentication of docusign |
| 2 | nodeRestURL | restful url of ontology node |
| 3 | contractAddr | record contract address |
| 4 | gasPrice | gas price, recommend 2500 currently |
| 5 | gasLimit | gas limit |

## Commit Signed File Info

**public String commitSignedFile(Account payer, Account ownerOntIdSigner, int signerPubKeyIndex,
                                   String docusignAccountId, SignedFileInfo signedFileInfo)**

| | name | desc |
| --- | --- | --- |
| 1 | payer | tx payer, should sign this tx and pay handling fee |
| 2 | ownerOntIdSigner | sponsor of pact, should be an ontID signer |
| 3 | signerPubKeyIndex | the index of the sponsor ONTID public key used to sign |
| 4 | docusignAccountId | docusign account ID |
| 5 | signedFileInfo | signed file info |

## Delete Signed File Info

**public String deleteSignedFileInfo(Account payer, Account ownerOntIdSigner, String ownerOntId, String signerPubKeyIndex,
                                   String contentHash)**

| | name | desc |
| --- | --- | --- |
| 1 | payer | tx payer, should sign this tx and pay handling fee |
| 2 | ownerOntIdSigner | sponsor of pact, should be an ontID signer |
| 3 | ownerOntId | ontId of sponsor of pact |
| 4 | signerPubKeyIndex | the index of the sponsor ONTID public key used to sign |
| 5 | contentHash | hash of signed file |

## Get Signed File Info

**public SignedFileInfo getSignedFileInfo(String contentHash)**

| | name | desc |
| --- | --- | --- |
| 1 | contentHash | hash of signed file |