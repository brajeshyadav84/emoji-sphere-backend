package com.emojisphere.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import java.util.HashMap;
import java.util.Map;
import com.emojisphere.dto.ApiResponse;
import org.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@RestController
@RequestMapping("/api")
public class ZoomSignatureController {

    @Value("${zoom.sdk.key}")
    private String sdkKey;

    @Value("${zoom.sdk.secret}")
    private String sdkSecret;

    @GetMapping("/zoom-signature")
    public ResponseEntity<ApiResponse<Object>> getSignature(@RequestParam String meetingNumber, @RequestParam int role) {
        Map<String, String> response = new HashMap<>();
        try {
            String signature = generateSignature(sdkKey, sdkSecret, meetingNumber, role);
            response.put("signature", signature);
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    // JWT HS256 signature generation
    private String generateSignature(String sdkKey, String sdkSecret, String meetingNumber, int role) throws Exception {
        long ts = (System.currentTimeMillis() / 1000) - 30;
        long exp = ts + 2 * 60 * 60;

        JSONObject header = new JSONObject();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        JSONObject payload = new JSONObject();
        payload.put("sdkKey", sdkKey);
        payload.put("mn", meetingNumber);
        payload.put("role", role);
        payload.put("iat", ts);
        payload.put("exp", exp);
        payload.put("appKey", sdkKey);
        payload.put("tokenExp", exp);

        String headerBase64 = Base64.encodeBase64URLSafeString(header.toString().getBytes("UTF-8"));
        String payloadBase64 = Base64.encodeBase64URLSafeString(payload.toString().getBytes("UTF-8"));
        String toSign = headerBase64 + "." + payloadBase64;

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(sdkSecret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signatureBytes = mac.doFinal(toSign.getBytes("UTF-8"));
        String signatureBase64 = Base64.encodeBase64URLSafeString(signatureBytes);

        return toSign + "." + signatureBase64;
    }
}
