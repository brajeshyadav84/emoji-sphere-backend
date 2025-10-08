package com.emojisphere.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@RestController
@RequestMapping("/onlinemeeting")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class OnlineMeetingController {

    @Value("${zoom.sdk.key}")
    private String sdkKey;

    @Value("${zoom.sdk.secret}")
    private String sdkSecret;

    @PostMapping("/join")
    public ResponseEntity<Map<String, Object>> joinMeeting(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String meetingUrl = (String) request.get("meetingUrl");
            String userName = (String) request.get("userName");
            String userEmail = (String) request.get("userEmail");
            Integer role = (Integer) request.get("role"); // 0 for participant, 1 for host
            String password = (String) request.get("password");
            
            // Extract meeting number from URL
            String meetingNumber = extractMeetingNumber(meetingUrl);
            if (meetingNumber == null) {
                response.put("success", false);
                response.put("error", "Invalid meeting URL format");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Extract password from URL if not provided
            if (password == null || password.isEmpty()) {
                password = extractPasswordFromUrl(meetingUrl);
            }
            
            // Generate signature for Zoom SDK
            String signature = generateSignature(sdkKey, sdkSecret, meetingNumber, role != null ? role : 0);
            
            response.put("success", true);
            response.put("sdkKey", sdkKey);
            response.put("signature", signature);
            response.put("meetingNumber", meetingNumber);
            response.put("password", password);
            response.put("userName", userName);
            response.put("userEmail", userEmail);
            response.put("role", role != null ? role : 0);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/meeting-info")
    public ResponseEntity<Map<String, Object>> getMeetingInfo(@RequestParam String meetingUrl) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String meetingNumber = extractMeetingNumber(meetingUrl);
            String password = extractPasswordFromUrl(meetingUrl);
            
            if (meetingNumber == null) {
                response.put("success", false);
                response.put("error", "Invalid meeting URL format");
                return ResponseEntity.badRequest().body(response);
            }
            
            response.put("success", true);
            response.put("meetingNumber", meetingNumber);
            response.put("password", password);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    private String extractMeetingNumber(String meetingUrl) {
        // Pattern for both types of Zoom URLs
        // https://us04web.zoom.us/j/9900769545?pwd=...
        // https://app.zoom.us/wc/9900769545/start?...
        
        Pattern pattern1 = Pattern.compile("zoom\\.us/j/(\\d+)");
        Pattern pattern2 = Pattern.compile("zoom\\.us/wc/(\\d+)");
        
        Matcher matcher1 = pattern1.matcher(meetingUrl);
        Matcher matcher2 = pattern2.matcher(meetingUrl);
        
        if (matcher1.find()) {
            return matcher1.group(1);
        } else if (matcher2.find()) {
            return matcher2.group(1);
        }
        
        return null;
    }
    
    private String extractPasswordFromUrl(String meetingUrl) {
        // Extract password from URL parameters
        Pattern pattern = Pattern.compile("pwd=([^&]+)");
        Matcher matcher = pattern.matcher(meetingUrl);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return "";
    }
    
    // JWT HS256 signature generation
    private String generateSignature(String sdkKey, String sdkSecret, String meetingNumber, int role) throws Exception {
        long ts = (System.currentTimeMillis() / 1000) - 30;
        long exp = ts + 2 * 60 * 60; // 2 hours expiration
        
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