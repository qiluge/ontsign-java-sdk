package com.ontology.ontsign.provider;

import com.hellosign.sdk.HelloSignClient;
import com.hellosign.sdk.HelloSignException;
import com.hellosign.sdk.http.Authentication;
import com.hellosign.sdk.http.HttpClient;
import com.hellosign.sdk.resource.SignatureRequest;

public class HelloSign extends HelloSignClient implements ESignatureProvider {

    /**
     * Default constructor for injection of dependencies (testing).
     *
     * @param client HttpClient
     * @param auth   Authentication
     * @see #HelloSign(String)
     */
    protected HelloSign(HttpClient client, Authentication auth) {
        super(client, auth);
    }

    /**
     * Creates a new HelloSign client using your API key.
     *
     * @param apiKey String API key
     * @see <a href= "https://app.hellosign.com/home/myAccount/current_tab/api">Account Settings</a>
     */
    public HelloSign(String apiKey) {
        super(apiKey);
    }

    /**
     * Creates a new HelloSign client using then given Authentication object.
     *
     * @param auth Authentication used primarily for setting OAuth token/secret
     */
    public HelloSign(Authentication auth) {
        super(auth);
    }

    @Override
    public boolean isCompleted(String requestId) {
        try {
            SignatureRequest request = this.getSignatureRequest(requestId);
            return request.isComplete();
        } catch (HelloSignException e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
    }
}
