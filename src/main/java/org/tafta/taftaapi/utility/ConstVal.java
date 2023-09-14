package org.tafta.taftaapi.utility;

/**
 * @author Gathariki Ngigi
 * Created on July 24, 2023.
 * Time 1454h
 */

public class ConstVal {
    public static final String TOKEN_TYPE_BEARER = "Bearer";
    public static final String TOKEN_TYPE_BASIC = "Basic";
    public static final String LOG_BLOCK_MSG = "{Response body blocked due to log size or sensitive data contact tech}";
    public static final String REPORTS = "report";
    public static final String OAUTH = "oauth";
    public static final String BANK = "bank";
    public static final String GRANT_CRED = "client_credentials";
    public static final String GRANT_PASS = "password";
    public static final String INVALID_GRANT = "Invalid grant type";
    public static final String UNAUTHORIZED = "Unauthorised";
    public static final String AUTH_GET_TOKEN = "/api/v1/oauth/token";
    public static final String AUTH_VERIFY_TOKEN = "/api/v1/oauth/token/verify";
    public static final String SWAGGER_DOC = "/api/v1/doc";
    public static final String LOGIN_ENDPOINT = "/login";
    public static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";

    /* CRDB TIPS URLS */
    public static final String MNP_INQUIRY_URL = "/common/mnp-inquiry/1.0.0/mobile/";
    public static final String FSP_IDS_LOOKUP_URL = "/common/other-bank-transfer/tips/lookup/1.0.0/fsp-ids";
    public static final String DEBITOR_AND_CREDITOR_ACCOUNTS_LOOKUP_URL = "/common/other-bank-transfer/tips/lookup/1.0.0";
    public static final String ACCOUNT_DETAILS_LOOKUP_URL = "/common/account-details/1.0.0/accounts/AccountId/";
    public static final String CUSTOMER_DETAILS_LOOKUP_URL = "/common/account-details/1.0.0/accounts/CustomerId/";
    public static final String TIPS_FUNDS_TRANSFER_URL = "/common/fund-transfer/internal/1.0.0";
    public static final String TIPS_FUNDS_TRANSFER_QUERY_URL = "/other-bank-transfer/tips/3.0.0/?";

    public static final String TIPS_FUNDS_TRANSFER_CALLBACK_URL = "/common/other-bank-transfer/tips/1.0.0/callback/transfers/";
}