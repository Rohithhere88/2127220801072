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

public class Registration {
    private static final String REGISTRATION_URL = "http://20.244.56.144/evaluation-service/register";

    public static void main(String[] args) throws IOException {
        String accessCode = "ZRsYXx";
        String universityEmail = "2022it0093@svce.ac.in";
        String rollNumber = "2127220801072";

        Map<String, String> payload = new HashMap<>();
        payload.put("accessCode", accessCode);
        payload.put("name", "Rohith Kumaar P");
        payload.put("email", universityEmail);
        payload.put("rollNo", rollNumber);
        payload.put("mobileNo", "9489633250");
        payload.put("githubUsername", "Rohithhere88");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(payload);

        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(REGISTRATION_URL);
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