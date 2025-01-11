package tests;


import api.pojo.Bpi;
import api.pojo.Root;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import qa.base.Api_BaseTest;

import java.util.HashMap;
import java.util.Map;

public class ApiAutomationTest extends Api_BaseTest {
    private static final String API_URL = "https://api.coindesk.com/v1/bpi/currentprice.json";

    @Test
    public void testApiResponse() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        APIResponse response = sendAnAPIrequest(API_URL, "GET", "", headers);
        verifyAPIStatusCode(response, 200);
        String responseText = response.text();
        JsonParser j = new JsonParser();
        JsonObject json = (JsonObject) j.parse(responseText);

        String responseData = json.get("bpi").toString();

        try {
            // Create an ObjectMapper to map the JSON to POJOs
            ObjectMapper objectMapper = new ObjectMapper();

            // Deserialize the "bpi" part of the response into a Bpi object
            Bpi bpi = objectMapper.readValue(responseData, Bpi.class);
            System.out.println("USD Code: " + bpi.getUSD().getCode());
            System.out.println("GBP Rate: " + bpi.getGBP().getRate());
            System.out.println("EUR Description: " + bpi.getEUR().getDescription());
            Assert.assertTrue(bpi.getGBP().getDescription().equalsIgnoreCase("British Pound Sterling"));
            // Access data from the mapped POJOs

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
