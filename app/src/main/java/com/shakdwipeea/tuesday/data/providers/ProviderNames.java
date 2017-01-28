package com.shakdwipeea.tuesday.data.providers;

import com.shakdwipeea.tuesday.data.entities.user.Provider;

import java.util.Arrays;
import java.util.List;

/**
 * Created by akash on 21/1/17.
 */

public class ProviderNames {
    final public static String Google = "Google";
    final public static String Github = "Github";
    final public static String Twitter = "Twitter";
    final public static String LinkedIn = "LinkedIn";
    final public static String WhatsApp = "WhatsApp";
    final public static String StackOverflow = "StackOverflow";
    final public static String Facebook = "Facebook";
    final public static String Email = "Email";
    final public static String Call = "Call";

    public static String[] getAll() {
        return new String[]{
          Google, Github, Twitter, LinkedIn, WhatsApp, StackOverflow, Facebook, Email, Call
        };
    }
}
