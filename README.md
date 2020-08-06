# OntSign JAVA SDK


Commit document hash to Ontology blockchain after signature completed.

![img](process.png)

### Ontology Contract and Docusign

We will combine [Ontology](https://ont.io) and docusign to further increase the credibility of signed contract.
We will record the hash of signed contract in Ontology chain because of the unforgeable feature of blockchain.

We provide a [contract](contracts/envelope.py) to record file hash, envelope ID, all signers and contract initiator.

The [api document](src/main/java/com/ontology/ontsign/READEME.md) made a description about how to interact
with Ontology blockchain.