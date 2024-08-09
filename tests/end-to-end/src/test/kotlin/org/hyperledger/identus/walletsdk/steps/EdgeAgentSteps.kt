package org.hyperledger.identus.walletsdk.steps

import io.cucumber.java.After
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.actors.OnStage
import org.hyperledger.identus.walletsdk.abilities.UseWalletSdk
import org.hyperledger.identus.walletsdk.workflow.CloudAgentWorkflow
import org.hyperledger.identus.walletsdk.workflow.EdgeAgentWorkflow
import javax.inject.Inject

class EdgeAgentSteps {

    @Inject
    private lateinit var edgeAgentWorkflow: EdgeAgentWorkflow

    @Inject
    private lateinit var cloudAgentWorkflow: CloudAgentWorkflow

    @Given("{actor} has created a backup")
    fun `Edge Agent has created a backup`(edgeAgent: Actor) {
        edgeAgentWorkflow.createBackup(edgeAgent)
    }

    @When("{actor} connects through the invite")
    fun `Edge Agent connects through the invite`(edgeAgent: Actor) {
        edgeAgentWorkflow.connect(edgeAgent)
    }

    @When("{actor} creates '{}' peer DIDs")
    fun `Edge Agent creates Peer DIDs`(edgeAgent: Actor, numberOfDids: Int) {
        edgeAgentWorkflow.createPeerDids(edgeAgent, numberOfDids)
    }

    @When("{actor} creates '{}' prism DIDs")
    fun `Edge Agent creates Prism DIDs`(edgeAgent: Actor, numberOfDids: Int) {
        edgeAgentWorkflow.createPrismDids(edgeAgent, numberOfDids)
    }

    @When("{actor} has '{}' jwt credentials issued by {actor}")
    fun `Edge Agent has {} jwt issued credential`(edgeAgent: Actor, numberOfCredentialsIssued: Int, cloudAgent: Actor) {
        val recordIdList = mutableListOf<String>()
        repeat(numberOfCredentialsIssued) {
            cloudAgentWorkflow.offerJwtCredential(cloudAgent)
            edgeAgentWorkflow.waitForCredentialOffer(edgeAgent, 1)
            edgeAgentWorkflow.acceptCredential(edgeAgent)
            val recordId = cloudAgent.recall<String>("recordId")
            recordIdList.add(recordId)
            cloudAgentWorkflow.verifyCredentialState(cloudAgent, recordId, "CredentialSent")
            edgeAgentWorkflow.waitToReceiveCredentialIssuance(edgeAgent, 1)
            edgeAgentWorkflow.processSpecificIssuedCred(edgeAgent, recordId)
        }
        cloudAgent.remember("recordIdList", recordIdList)
    }

    @When("{actor} has '{}' anonymous credentials issued by {actor}")
    fun `Edge Agent has {} anonymous issued credential`(
        edgeAgent: Actor,
        numberOfCredentialsIssued: Int,
        cloudAgent: Actor
    ) {
        repeat(numberOfCredentialsIssued) {
            cloudAgentWorkflow.offerAnonymousCredential(cloudAgent)
            edgeAgentWorkflow.waitForCredentialOffer(edgeAgent, 1)
            edgeAgentWorkflow.acceptCredential(edgeAgent)
            cloudAgentWorkflow.verifyCredentialState(cloudAgent, cloudAgent.recall("recordId"), "CredentialSent")
            edgeAgentWorkflow.waitToReceiveCredentialIssuance(edgeAgent, 1)
            edgeAgentWorkflow.processIssuedCredential(edgeAgent, 1)
        }
    }

    @When("{actor} accepts {} jwt credential offers sequentially from {actor}")
    fun `Edge Agent accepts multiple credential offers sequentially from Cloud Agent`(
        edgeAgent: Actor,
        numberOfCredentials: Int,
        cloudAgent: Actor
    ) {
        val recordIdList = mutableListOf<String>()
        repeat(numberOfCredentials) {
            cloudAgentWorkflow.offerJwtCredential(cloudAgent)
            edgeAgentWorkflow.waitForCredentialOffer(edgeAgent, 1)
            edgeAgentWorkflow.acceptCredential(edgeAgent)
            cloudAgentWorkflow.verifyCredentialState(cloudAgent, cloudAgent.recall("recordId"), "CredentialSent")
            recordIdList.add(cloudAgent.recall("recordId"))
        }
        cloudAgent.remember("recordIdList", recordIdList)
    }

    @When("{actor} accepts {} jwt credential offers at once from {actor}")
    fun `Edge Agent accepts multiple jwt credential offers at once from Cloud Agent`(
        edgeAgent: Actor,
        numberOfCredentials: Int,
        cloudAgent: Actor
    ) {
        val recordIdList = mutableListOf<String>()

        // offer multiple credentials
        repeat(numberOfCredentials) {
            cloudAgentWorkflow.offerJwtCredential(cloudAgent)
            recordIdList.add(cloudAgent.recall("recordId"))
        }
        cloudAgent.remember("recordIdList", recordIdList)

        // wait to receive
        edgeAgentWorkflow.waitForCredentialOffer(edgeAgent, numberOfCredentials)

        // accept all
        repeat(numberOfCredentials) {
            edgeAgentWorkflow.acceptCredential(edgeAgent)
        }
    }

    @When("{actor} accepts the credential")
    fun `Edge Agent accepts the credential`(edgeAgent: Actor) {
        edgeAgentWorkflow.acceptCredential(edgeAgent)
    }

    @When("{actor} sends the present-proof")
    fun `Edge Agent sends the present-proof`(edgeAgent: Actor) {
        edgeAgentWorkflow.waitForProofRequest(edgeAgent)
        edgeAgentWorkflow.presentProof(edgeAgent)
    }

    @Then("{actor} should receive the credential")
    fun `Edge Agent should receive the credential`(edgeAgent: Actor) {
        edgeAgentWorkflow.waitForCredentialOffer(edgeAgent, 1)
    }

    @Then("{actor} should receive the anonymous credential")
    fun `Edge Agent should receive the anonymous credential`(edgeAgent: Actor) {
        edgeAgentWorkflow.waitToReceiveAnonymousCredential(edgeAgent, 1)
    }

    @Then("{actor} wait to receive {} issued credentials")
    fun `Edge Agent wait to receive issued credentials`(edgeAgent: Actor, expectedNumberOfCredentials: Int) {
        edgeAgentWorkflow.waitToReceiveCredentialIssuance(edgeAgent, expectedNumberOfCredentials)
    }

    @Then("{actor} process {} issued credentials")
    fun `Edge Agent process multiple issued credentials`(edgeAgent: Actor, numberOfCredentials: Int) {
        edgeAgentWorkflow.processIssuedCredential(edgeAgent, numberOfCredentials)
    }

    @Then("{actor} should have {} credentials")
    fun `Edge Agent should have N credential`(actor: Actor, numberOfCredentials: Int) {
        //edgeAgentWorkflow.creden
    }

    @Then("{actor} waits to receive the revocation notifications from {actor}")
    fun `Edge Agent waits to receive the revocation notifications from Cloud Agent`(
        edgeAgent: Actor,
        cloudAgent: Actor
    ) {
        val revokedRecordIdList = cloudAgent.recall<MutableList<String>>("revokedRecordIdList")
        edgeAgentWorkflow.waitForCredentialRevocationMessage(edgeAgent, revokedRecordIdList.size)
    }

    @Then("{actor} should see the credentials were revoked by {actor}")
    fun `Edge Agent should see the credentials were revoked by Cloud Agent`(edgeAgent: Actor, cloudAgent: Actor) {
        val revokedRecordIdList = cloudAgent.recall<MutableList<String>>("revokedRecordIdList")
        edgeAgentWorkflow.waitUntilCredentialIsRevoked(edgeAgent, revokedRecordIdList)
    }

    @Then("a new SDK can be restored from {actor}")
    fun `A new SDK can be restored from Edge Agent`(edgeAgent: Actor) {
        edgeAgentWorkflow.createANewWalletFromBackup(edgeAgent)
    }

    @Then("a new SDK cannot be restored from {actor} with wrong seed")
    fun `A new SDK cannot be restored from Edge Agent with wrong seed`(edgeAgent: Actor) {
        edgeAgentWorkflow.createNewWalletFromBackupWithWrongSeed(edgeAgent)
    }

    @Then("a new {actor} is restored from {actor}")
    fun `A new Agent is restored from Edge Agent`(newAgent: Actor, originalAgent: Actor) {
        edgeAgentWorkflow.backupAndRestoreToNewAgent(newAgent, originalAgent)
    }

    @Then("{actor} should have the expected values from {actor}")
    fun `Restored Agent should have the expected values from Original Edge Agent`(
        restoredEdgeAgent: Actor,
        originalEdgeAgent: Actor
    ) {
        edgeAgentWorkflow.copyAgentShouldMatchOriginalAgent(restoredEdgeAgent, originalEdgeAgent)
    }

    @Then("{actor} is dismissed")
    fun `Edge Agent is dismissed`(edgeAgent: Actor) {
        edgeAgent.wrapUp()
    }

    @After
    fun stopAgent() {
        OnStage.theActor("Edge Agent").attemptsTo(
            UseWalletSdk.stop()
        )
    }
}
