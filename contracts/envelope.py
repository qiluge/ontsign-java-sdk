OntCversion = '2.0.0'

from ontology.interop.System.Action import RegisterAction
from ontology.interop.System.Runtime import Notify, CheckWitness, Serialize, Deserialize
from ontology.interop.System.Storage import GetContext, Get, Put, Delete
from ontology.interop.Ontology.Runtime import Base58ToAddress
from ontology.interop.Ontology.Contract import Migrate
from ontology.interop.Ontology.Native import Invoke

CommitEvent = RegisterAction("Commit", "ownerOntId", "contentHash", "envelopeId", "signers")
DeleteEvent = RegisterAction("Delete", "ownerOntId", "contentHash", "envelopeId")

OWNER = Base58ToAddress("ATqpnrgVjzmkeHEqPiErnsxTEgi5goor2e")
ONTID_CONTRACT_ADDRESS = bytearray(b'\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x03')

ctx = GetContext()


def Main(operation, args):
    if operation == 'commitSignedFileInfo':
        return commitSignedFileInfo(args[0], args[1], args[2], args[3], args[4])
    if operation == 'deleteSignedFileInfo':
        return deleteSignedFileInfo(args[0], args[1], args[2])
    if operation == 'getSignedFileInfo':
        return getSignedFileInfo(args[0])
    if operation == 'migrate':
        if len(args) != 7:
            return False
        code = args[0]
        needStorage = args[1]
        name = args[2]
        version = args[3]
        author = args[4]
        email = args[5]
        description = args[6]
        return Upgrade(code, needStorage, name, version, author, email, description)


def commitSignedFileInfo(ownerOntId, signerPubKeyIndex, contentHash, envelopeId, signers):
    """
    : param signers: all signed ontId
    """
    # verify ownerOntId signature
    param = state(ownerOntId, signerPubKeyIndex)
    res = Invoke(0, ONTID_CONTRACT_ADDRESS, "verifySignature", param)
    if res != b'\x01':
        raise Exception("commiterId verifySignature error.")
    # get envelope
    envelopeData = Get(ctx, contentHash)
    if len(envelopeData) != 0:
        raise Exception("envelope already existed")
    envelope = [ownerOntId, contentHash, envelopeId, signers]
    envelopeData = Serialize(envelope)
    Put(ctx, contentHash, envelopeData)
    CommitEvent(ownerOntId, contentHash, envelopeId, signers)


def getSignedFileInfo(contentHash):
    # get envelope
    envelopeData = Get(ctx, contentHash)
    if len(envelopeData) != 0:
        return Deserialize(envelopeData)
    return ''


def deleteSignedFileInfo(ownerOntId, signerPubKeyIndex, contentHash):
    # verify ownerOntId signature
    param = state(ownerOntId, signerPubKeyIndex)
    res = Invoke(0, ONTID_CONTRACT_ADDRESS, "verifySignature", param)
    if res != b'\x01':
        raise Exception("commiterId verifySignature error.")
    # get envelope
    envelopeData = Get(ctx, contentHash)
    if len(envelopeData) != 0:
        envelope = Deserialize(envelopeData)
        if len(envelope) != 4:
            raise Exception('illegal envelope')
        if ownerOntId != envelope[0]:
            raise Exception('from ONT ID is wrong')
        Delete(ctx, contentHash)
        DeleteEvent(ownerOntId, contentHash, envelope[2])


def Upgrade(code, needStorage, name, version, author, email, description):
    if not CheckWitness(OWNER):
        raise Exception('CheckWitness failed')
    res = Migrate(code, needStorage, name, version, author, email, description)
    if not res:
        raise Exception('Migrate failed')
    return True
