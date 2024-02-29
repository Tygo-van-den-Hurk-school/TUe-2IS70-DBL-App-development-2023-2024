package com.example.drops.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.core.app.ApplicationProvider;

import org.checkerframework.checker.units.qual.C;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;

@RunWith(RobolectricTestRunner.class)

public class RetrofitClientTest {
    private Context context;
    private int successTestNum = 0;
    private int endTestNum = 0;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void testGetCallbackOnFailure() {
        endTestNum = 0;
        successTestNum = 0;

        Callback<Void> callback = RetrofitClient.getCallback(context, "Test error",
                this::incrementSuccessTest, this::incrementEndTest);

        callback.onFailure(getTestCall(), new Throwable());

        assertEquals(successTestNum, 0);
        assertEquals(endTestNum, 1);
    }

    @Test
    public void testGetCallbackOnSuccess() {
        endTestNum = 0;
        successTestNum = 0;

        Callback<Void> callback = RetrofitClient.getCallback(context, "Test error",
                this::incrementSuccessTest, this::incrementEndTest);

        callback.onResponse(getTestCall(), Response.success(null));

        assertEquals(successTestNum, 1);
        assertEquals(endTestNum, 1);
    }

    @Test
    public void testGetCallbackOnError() {
        endTestNum = 0;
        successTestNum = 0;

        Callback<Void> callback = RetrofitClient.getCallback(context, "Test error",
                this::incrementSuccessTest, this::incrementEndTest);

        callback.onResponse(getTestCall(), Response.error(500, getEmptyResponseBody()));

        assertEquals(successTestNum, 0);
        assertEquals(endTestNum, 1);
    }

    private void incrementSuccessTest(Void v) {
        successTestNum++;
    }

    private void incrementEndTest() {
        endTestNum++;
    }

    private ResponseBody getEmptyResponseBody() {
        return new ResponseBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public long contentLength() {
                return 0;
            }

            @NonNull
            @Override
            public BufferedSource source() {
                return null;
            }
        };
    }

    private Call<Void> getTestCall() {
        return new Call<Void>() {
            @Override
            public Response<Void> execute() throws IOException {
                return null;
            }

            @Override
            public void enqueue(Callback<Void> callback) {}

            @Override
            public boolean isExecuted() {
                return false;
            }

            @Override
            public void cancel() {

            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Override
            public Call<Void> clone() {
                return null;
            }

            @Override
            public Request request() {
                return null;
            }
        };
    }
}
