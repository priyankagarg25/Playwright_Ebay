package qa.base;

import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.util.Map;

public class Api_BaseTest {
    protected static Playwright playwright;
    protected static APIRequestContext apiRequestContext;

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        playwright = Playwright.create();
    }

    public APIResponse sendAnAPIrequest(String url, String requestMethod, String RequestParameter, Map<String, String> headers) {


        APIRequestContext apiRequestContext = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(url).setExtraHTTPHeaders(headers));
        switch (requestMethod) {
            case "GET":
                return apiRequestContext.get(RequestParameter);
            case "POST":
                return apiRequestContext.post(RequestParameter);
            case "PUT":
                return apiRequestContext.put(RequestParameter);
            case "DELETE":
                return apiRequestContext.delete(RequestParameter);
            case "FETCH":
                return apiRequestContext.fetch(RequestParameter);
            default:
                return null;
        }
    }

    public void verifyAPIStatusCode(APIResponse response,int StatusCode){
        Assert.assertEquals(response.status(),StatusCode);
    }
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if(apiRequestContext!=null)
        apiRequestContext.dispose();
        playwright.close();
    }
}

