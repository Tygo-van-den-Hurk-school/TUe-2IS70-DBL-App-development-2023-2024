package com.example.drops.utils;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import android.content.Context;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
public class CheckPasswordTest {
    private Context context;

    // @Before => JUnit 4 annotation that specifies this method should run before each test is run
    // Useful to do setup for objects that are needed in the test
    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void testCheckPasswordContainsAccount() {
        assertFalse(CheckPassword.checkPassword(context, "Password1#", "Password"));
        assertTrue(CheckPassword.checkPassword(context, "Password1#", "NotPassword"));
    }
    @Test
    public void testCheckPasswordLengthTooShort() {
        assertFalse(CheckPassword.checkPassword(context, "Pass1#", "Steve"));
        assertTrue(CheckPassword.checkPassword(context, "Password1#", "Steve"));
    }
    @Test
    public void testCheckPasswordNoUppercase() {
        assertFalse(CheckPassword.checkPassword(context, "password1#", "Steve"));
        assertTrue(CheckPassword.checkPassword(context, "Password1#", "Steve"));
    }
    @Test
    public void testCheckPasswordNoLowercase() {
        assertFalse(CheckPassword.checkPassword(context, "PASSWORD#", "Steve"));
        assertTrue(CheckPassword.checkPassword(context, "Password1#", "Steve"));
    }
    @Test
    public void testCheckPasswordNoDigits() {
        assertFalse(CheckPassword.checkPassword(context, "Password#", "Steve"));
        assertTrue(CheckPassword.checkPassword(context, "Password1#", "Steve"));
    }

    @Test
    public void testCheckPasswordNoSpecialCharacter() {
        assertFalse(CheckPassword.checkPassword(context, "Password1", "Steve"));
        assertTrue(CheckPassword.checkPassword(context, "Password1#", "Steve"));
        assertTrue(CheckPassword.checkPassword(context, "Password1$", "Steve"));
        assertTrue(CheckPassword.checkPassword(context, "Password1!", "Steve"));
        assertTrue(CheckPassword.checkPassword(context, "Password1@", "Steve"));
        assertTrue(CheckPassword.checkPassword(context, "Password1%", "Steve"));
        assertTrue(CheckPassword.checkPassword(context, "Password1^", "Steve"));
        assertTrue(CheckPassword.checkPassword(context, "Password1&", "Steve"));
        assertTrue(CheckPassword.checkPassword(context, "Password1*", "Steve"));
        assertTrue(CheckPassword.checkPassword(context, "Password1(", "Steve"));
        assertTrue(CheckPassword.checkPassword(context, "Password1)", "Steve"));
    }
}
