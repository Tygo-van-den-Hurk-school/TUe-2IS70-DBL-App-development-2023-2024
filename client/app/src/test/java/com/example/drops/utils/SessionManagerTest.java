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
import java.util.ArrayList;
import java.util.List;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Retrofit;

import static org.junit.Assert.*;


@RunWith(RobolectricTestRunner.class)
public class SessionManagerTest {
    private SessionManager sessionManager;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        sessionManager = new SessionManager(context);
        sessionManager.clearPreferences();
    }

    @Test
    public void testSetAccount() {
        Account account = new Account();
        assertNull(sessionManager.getAccount());
        sessionManager.setAccount(account);
        assertNotNull(sessionManager.getAccount());
        assertEquals(account.getId(), sessionManager.getAccount().getId());
    }

    @Test
    public void testSetGroup() {
        int groupId = 5;
        assertNotEquals(groupId, sessionManager.getGroup());
        sessionManager.setGroup(groupId);
        assertEquals(groupId, sessionManager.getGroup());
    }

    @Test
    public void testSetModeratorMode() {
        boolean moderator = true;
        assertNotEquals(moderator, sessionManager.getModeratorMode());
        sessionManager.setModeratorMode(moderator);
        assertEquals(moderator, sessionManager.getModeratorMode());
    }

    @Test
    public void testSaveAuthToken() {
        String authToken = "test_string";
        assertNotEquals(authToken, sessionManager.getAuthToken());
        sessionManager.saveAuthToken(authToken);
        assertEquals(authToken, sessionManager.getAuthToken());
    }

    @Test
    public void testSetDrops() {
        List<Drop> drops = new ArrayList<>();
        drops.add(new Drop());
        assertNull(sessionManager.getDrops());
        sessionManager.setDrops(drops);
        assertNotNull(sessionManager.getDrops());
        assertEquals(drops.get(0).getId(), sessionManager.getDrops().get(0).getId());
    }

    @Test
    public void testSetGroups() {
        List<Group> groups = new ArrayList<>();
        groups.add(new Group());
        assertNull(sessionManager.getGroups());
        sessionManager.setGroups(groups);
        assertNotNull(sessionManager.getGroups());
        assertEquals(groups.get(0).getId(), sessionManager.getGroups().get(0).getId());
    }

    @Test
    public void testClearPreferences() {
        sessionManager.setAccount(new Account());
        assertNotNull(sessionManager.getAccount());
        sessionManager.clearPreferences();
        assertNull(sessionManager.getAccount());
    }
}

