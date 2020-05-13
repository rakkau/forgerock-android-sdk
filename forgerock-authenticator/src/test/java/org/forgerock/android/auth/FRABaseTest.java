/*
 * Copyright (c) 2020 ForgeRock. All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package org.forgerock.android.auth;

import android.os.Bundle;
import android.util.Base64;

import com.google.firebase.messaging.RemoteMessage;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public abstract class FRABaseTest {

    public static final String ISSUER = "issuer1";
    public static final String OTHER_ISSUER = "issuer2";
    public static final String ACCOUNT_NAME = "user1";
    public static final String OTHER_ACCOUNT_NAME = "user2";
    public static final String IMAGE_URL = "http://forgerock.com/logo.jpg";
    public static final String BACKGROUND_COLOR = "032b75";
    public static final String MECHANISM_UID = "b162b325-ebb1-48e0-8ab7-b38cf341da95";
    public static final String OTHER_MECHANISM_UID = "013be51a-8c14-356d-b0fc-b3660cc8a101";
    public static final String SECRET = "JMEZ2W7D462P3JYBDG2HV7PFBM";
    public static final String CORRECT_SECRET = "2afd55692b492e60df7e9c0b4f55b0492afd55692b492e60df7e9c0b4f55b049";
    public static final String INCORRECT_SECRET = "INVALID-52e2563abe7d27f3476117ba2bc802a952e2563abe7d27f3476117ba2bc802a9";
    public static final String ALGORITHM = "sha1";
    public static final int DIGITS = 6;
    public static final int PERIOD = 30;
    public static final int COUNTER = 0;
    public static final String REGISTRATION_ENDPOINT = "http://openam.forgerock.com:8080/openam/json/push/sns/message?_action=register";
    public static final String OTHER_REGISTRATION_ENDPOINT = "http://develop.openam.forgerock.com:8080/openam/json/push/sns/message?_action=register";
    public static final String AUTHENTICATION_ENDPOINT = "http://openam.forgerock.com:8080/openam/json/push/sns/message?_action=authenticate";
    public static final String OTHER_AUTHENTICATION_ENDPOINT = "http://develop.openam.forgerock.com:8080/openam/json/push/sns/message?_action=authenticate";
    public static final String MESSAGE_ID = "AUTHENTICATE:63ca6f18-7cfb-4198-bcd0-ac5041fbbea01583798229441";
    public static final String CHALLENGE = "fZl8wu9JBxdRQ7miq3dE0fbF0Bcdd+gRETUbtl6qSuM=";
    public static final String AMLB_COOKIE = "ZnJfc3NvX2FtbGJfcHJvZD0wMQ==";
    public static final long TTL = 120;


    public static Map<String, String> generateBaseMessage() {
        Map<String, String> baseMessage;
        baseMessage = new HashMap<>();
        baseMessage.put(PushParser.MESSAGE_ID, MESSAGE_ID);
        baseMessage.put(PushParser.CHALLENGE, CHALLENGE);
        baseMessage.put(PushParser.MECHANISM_UID, MECHANISM_UID);
        baseMessage.put(PushParser.AM_LOAD_BALANCER_COOKIE, AMLB_COOKIE);
        baseMessage.put(PushParser.TTL, String.valueOf(TTL));
        return baseMessage;
    }

    public static Push generateMockMechanism(String mechanismUid) {
        final Push push = mock(Push.class);
        given(push.getAccountName()).willReturn(ACCOUNT_NAME);
        given(push.getIssuer()).willReturn(ISSUER);
        given(push.getType()).willReturn(Mechanism.PUSH);
        given(push.getMechanismUID()).willReturn(mechanismUid);
        given(push.getSecret()).willReturn(CORRECT_SECRET);
        given(push.getAuthenticationEndpoint()).willReturn(AUTHENTICATION_ENDPOINT);
        given(push.getRegistrationEndpoint()).willReturn(REGISTRATION_ENDPOINT);
        return push;
    }

    public static Push generateMockMechanism(String mechanismUid, String serverUrl) {
        final Push push = mock(Push.class);
        given(push.getAccountName()).willReturn(ACCOUNT_NAME);
        given(push.getIssuer()).willReturn(ISSUER);
        given(push.getType()).willReturn(Mechanism.PUSH);
        given(push.getMechanismUID()).willReturn(mechanismUid);
        given(push.getSecret()).willReturn(CORRECT_SECRET);
        given(push.getAuthenticationEndpoint()).willReturn(serverUrl+"authenticate");
        given(push.getRegistrationEndpoint()).willReturn(serverUrl+"register");
        return push;
    }

    public static RemoteMessage generateMockRemoteMessage(String messageId, String base64Secret, Map<String, String> map) throws JSONException {
        Bundle mockBundle = mock(Bundle.class);
        if(base64Secret != null) {
            String jwt = generateJwt(base64Secret, map);
            given(mockBundle.get("message")).willReturn(jwt);
        } else {
            JSONObject message = new JSONObject();
            for (String key : map.keySet()) {
                message.put(key, map.get(key));
            }
            given(mockBundle.get("message")).willReturn(message.toString());
        }
        given(mockBundle.get("messageId")).willReturn(messageId);
        given(mockBundle.keySet()).willReturn(new HashSet<String>(Arrays.asList("message", "messageId")));

        return new RemoteMessage(mockBundle);
    }

    private static String generateJwt(String base64Secret, Map<String, String> data) {
        JWTClaimsSet.Builder claimBuilder = new JWTClaimsSet.Builder();
        for (String key : data.keySet()) {
            claimBuilder.claim(key, data.get(key));
        }
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
                .type(JOSEObjectType.JWT)
                .build();
        SignedJWT signedJWT = new SignedJWT(header, claimBuilder.build());

        byte[] secret = Base64.decode(base64Secret, Base64.NO_WRAP);
        JWSSigner signer = null;
        try {
            signer = new MACSigner(secret);
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            e.printStackTrace();
        }
        return signedJWT.serialize();
    }

}
