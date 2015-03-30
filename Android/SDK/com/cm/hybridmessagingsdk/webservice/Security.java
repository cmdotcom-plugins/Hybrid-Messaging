package com.cm.hybridmessagingsdk.webservice;


import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Dion on 21/10/14.
 */
public class Security {

    protected static String buildRequestMac(String secret, String timestamp, String method, String uri) {
        String protocol = "HTTP/1.1";

        String request_uri = String.format("%s /%s %s", method, uri, protocol);
        String base_url = String.format("hybridmessagingapi.cm.nl");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(request_uri)                       // example: GET /v1/apps/your-key HTTP/1.1\n
                .append(System.getProperty("line.separator"))
                .append(base_url)                               // hybridmessagingapi.cm.nl\n
                .append(System.getProperty("line.separator"))
                .append(timestamp)                             // 1379333669
                .append(System.getProperty("line.separator"));

        String output = stringBuilder.toString();

        try {
            return calcShaHash(output, secret);
        }
        catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        catch (InvalidKeyException e) { e.printStackTrace(); }

        return null;
    }

    /**
     *
     * @param data
     * @param secret
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.InvalidKeyException
     * @return
     */
    private static String calcShaHash(String data, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        if(data == null) throw new IllegalArgumentException("Missing data to calculate the new hash");
        if(secret == null) throw new IllegalArgumentException("Missing secret to calculate mac");
        String macType = "HmacSHA1";

        try {
            Mac mac = Mac.getInstance(macType);
            Key signingKey = new SecretKeySpec(secret.getBytes(), macType);
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(data.getBytes());
            return Base64.encodeToString(rawHmac, Base64.NO_WRAP);
        }
        catch (Exception e) { e.printStackTrace(); }

        return null;
    }
}
