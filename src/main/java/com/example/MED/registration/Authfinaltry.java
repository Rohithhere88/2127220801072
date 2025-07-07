package com.example.MED.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ClassicHttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Authfinaltry {
    private static final String AUTH_URL = "http://20.244.56.144/evaluation-service/auth";

    public static void main(String[] args) throws IOException {
        Map<String, String> payload = new HashMap<>();
        payload.put("clientID", "75764f38-fd44-42a5-aacb-d3846819c690");
        payload.put("clientSecret", "AagTGDTsFQqrYhwZ");
        payload.put("email", "2022it0093@svce.ac.in");
        payload.put("name", "rohith kumaar p");
        payload.put("rollNo", "2127220801072");
        payload.put("accessCode", "ZRsYXx");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(payload);

        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(AUTH_URL);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(json));

        ClassicHttpResponse response = (ClassicHttpResponse) client.execute(post);
        int status = response.getCode();
        System.out.println("Status: " + status);
        String responseBody = new String(response.getEntity().getContent().readAllBytes());
        System.out.println("Response: " + responseBody);
        response.close();
    }
}