package com.example.drops.utils;

import android.content.Context;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;

import static org.junit.Assert.*;

import com.google.android.gms.common.api.Api;

@RunWith(RobolectricTestRunner.class)
public class AuthInterceptorTest {
    public MockWebServer mockWebServer;
    private Context context;
    private SessionManager sessionManager;

    @Before
    public void setUp() throws IOException {
        context = ApplicationProvider.getApplicationContext();
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        sessionManager = new SessionManager(context);
        sessionManager.clearPreferences();
        String httpUrl = mockWebServer.url("/").toString();
        RetrofitClient.setInstance(context, httpUrl);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void testNoToken() {
        MockResponse mockedResponse = new MockResponse()
                .setBody("{\"name\": \"test\"}")
                .setResponseCode(200);

        TestApi api = RetrofitClient.getInstance(context).create(TestApi.class);
        Call<Void> testCall = api.test();

        try {
            mockWebServer.enqueue(mockedResponse);
            testCall.execute();
            RecordedRequest request = mockWebServer.takeRequest();
            String header = request.getHeader("user_access_token");
            assertNull(sessionManager.getAuthToken());
            assertNull(header);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testHasToken() {
        MockResponse mockedResponse = new MockResponse()
                .setBody("{\"name\":\"test\"}")
                .setResponseCode(200);

        TestApi api = RetrofitClient.getInstance(context).create(TestApi.class);
        Call<Void> testCall = api.test();

        sessionManager.saveAuthToken("test");

        try {
            mockWebServer.enqueue(mockedResponse);
            testCall.execute();
            RecordedRequest request = mockWebServer.takeRequest();
            String header = request.getHeader("user_access_token");
            assertEquals(sessionManager.getAuthToken(), header);
        } catch (Exception e) {
            fail();
        }
    }

    private interface TestApi {
        @GET("/test")
        Call<Void> test();
    }
}
