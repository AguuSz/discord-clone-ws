package com.aguusz.discord.auth.filters;

public class AuthConstants {
    public static final long EXPIRATION_TIME = (60 * 60 * 99999);
    public static final String SECRET = "#web3.secada*";

    public static final String AUTH_HEADER_NAME = "Authorization";
    public static final String AUTH_PARAM_NAME = "authtoken";
    public static final String TOKEN_PREFIX = "Bearer ";
}
