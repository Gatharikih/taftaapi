package org.tafta.taftaapi.utility;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Joseph Kibe
 * Created on Thursday, December 22, 2022,
 * Time 10:38 AM
 */

public class Errors {
    private Map<String, String> error;

    private static Errors instance;

    public static Errors getInstance() {
        if (instance == null) {
            instance = new Errors();
            instance.setErrors();
        }

        return instance;
    }

    public Map<String, String> getError(String errorCode) {

        return new LinkedHashMap<>() {{
            put("code", errorCode);
            put("message", error.get(errorCode));
        }};
    }

    public Map<String, String> getError(String errorCode, String addMsg) {

        return new LinkedHashMap<>() {{
            put("code", errorCode);
            put("message", error.get(errorCode) + " : " + addMsg);
        }};
    }

    public static Map<String, String> get(String error) {
        return Errors.getInstance().getError(error);
    }

    public static Map<String, String> get(String error, String addMsg) {
        return Errors.getInstance().getError(error, addMsg);
    }

    public static String getMsg(String error) {
        return Errors.getInstance().getError(error).get("message");
    }

    private void setErrors() {
        error = new LinkedHashMap<>();
        error.put("000200", "SUCCESS");
        error.put("000201", "Request In process");
        error.put("001400", "Could not communicate with Terminal");
        error.put("001404", "Signature not found");
        error.put("002500", "Failed to process the request");
        error.put("002402", "Force Approval");
        error.put("002400", "Validation error");
        error.put("003500", "Failed to process batch");
        error.put("003403", "Declined by Issuer – Card expired");
        error.put("003400", "Transaction-id required");
        error.put("005402", "Card Suspended");
        error.put("013402", "Transaction Not Permitted To Cardholder");
        error.put("017402", "Card Not Active");
        error.put("019500", "PIN Key sync error – Please call Customer Support");
        error.put("020400", "Receiver Currency not allowed for the transaction type");
        error.put("025402", "Exceeds Maximum Receiver Amount");
        error.put("026402", "Declined by Issuer – Invalid Card Number");
        error.put("028402", "Invalid Terminal – Please call Customer Support");
        error.put("029402", "Inactive Terminal");
        error.put("033402", "Capture Card, Please Call Processor");
        error.put("036200", "Advice Accepted");
        error.put("038402", "Reconciled, In Balance");
        error.put("039402", "Not Reconciled, Totals Provided");
        error.put("040402", "No Opened Batch");
        error.put("041400", "Field Validation");
        error.put("042401", "Unauthorized");
        error.put("043400", "Amount Minimum");
        error.put("044400", "Amount Maximum");
        error.put("0003", "Transactions with the transaction_ref exist, request not saved");
        error.put("046402", "Avs Response Not Accepted");
        error.put("047402", "Csc Response Not Accepted");
        error.put("048402", "Csc Response Not Accepted0");
        error.put("049500", "No Response From Server");
        error.put("050500", "Internal Error");
        error.put("051500", "Could Not Connect");
        error.put("052500", "Exception Condition Contact Support");
        error.put("053500", "Exception Condition Contact Technical Support");
        error.put("054402", "Data Element Error");
        error.put("055500", "Acquirer Not Supported By Switch");
        error.put("056402", "Transaction Destination not allowed");
        error.put("057402", "Card Issuer Timed Out");
        error.put("058402", "Card Issuer Unavailable");
        error.put("059402", "Duplicate Transmission");
        error.put("060500", "System Error, Database");
        error.put("061402", "Aborted, Threshold Exceeded");
        error.put("062500", "System misconfiguration");
        error.put("063500", "System Error, Transaction");
        error.put("064500", "System Error, Hsm");
        error.put("065500", "Configuration Error, Invalid Service");
        error.put("066500", "Configuration Error, Invalid Terminal");
        error.put("067500", "Configuration error, invalid merchant");
        error.put("068401", "Inactive, locked or disabled");
        error.put("069500", "Configuration error, invalid store");
        error.put("070500", "Configuration Error, System Error");
        error.put("071500", "System Error, Other");
        error.put("072500", "System Error Other");
        error.put("073500", "System Error Other");
        error.put("074400", "Invalid Request");
        error.put("081400", "Create or Update entity failed");
        error.put("081500", "Update failed");
        error.put("082400", "error.put entity failed");
        error.put("082404", "Transaction not found");
        error.put("082500", "Get failed");
        error.put("083400", "Get entity failed");
        error.put("083500", "Get failed");
        error.put("083401", "Resource not found");
        error.put("084500", "error.put Token To Customer Failed");
        error.put("084401", "Apikey Authentication. Please check that the service was configured correctly");
        error.put("085404", "Unable to delete setting");
        error.put("086400", "Missing or Invalid Accept Type Header. Accept Type Header must be of type ‘application/json or ‘application/xml");
        error.put("087400", "Metadata can only have a maximum of ten key/value pairs");
        error.put("088400", "No body in request");
        error.put("089200", "Delete Successful");
        error.put("089500", "Error Deleting resource");
        error.put("089400", "Unable to delete Customer token");
        error.put("090400", "Required request body content is missing, malformed, or datatype usage is invalid");
        error.put("091402", "Delete Token Failed. Token Associated With Payment Plan");
        error.put("091204", "No batch found to process");
        error.put("091404", "Object not found");
        error.put("092405", "Http Method not supported");
        error.put("092404", "URL Parameter Invalid not found");
        error.put("093400", "Merchant Opt Out Delete Failed");
        error.put("094400", "Cannot delete Plan that has been used to charge the customer");
        error.put("280400", "Transaction failed");
        error.put("281400", "Void failed. Cannot void ach transactions that are returned or settled.");
        error.put("282400", "Authorization failed");
        error.put("283400", "Business Validation Error");
        error.put("284400", "ACH Account not found");
        error.put("285400", "Cannot process request. Contact Clearance for assistance");
        error.put("286400", "An error occurred we did not account for with Drools rules or otherwise");
        error.put("287404", "Transaction not found");
        error.put("288400", "Request failed");
        error.put("289400", "Provider failed");
        error.put("290500", "Transaction Failed");
        error.put("291400", "Cannot process request. Contact Clearance for assistance");
        error.put("292404", "Transaction not found");
        error.put("293404", "Transaction not found");
        error.put("294500", "Cannot process request. Contact Clearance for assistance");
        error.put("295500", "Cannot process request. Contact Clearance for assistance");
        error.put("880400", "Error parsing request");
        error.put("880500", "Unexpected Error – please contact customer support");
        error.put("881400", "Device communications unsupported");
        error.put("882400", "Transaction type unsupported for the receiver");
        error.put("883500", "Terminal not connected");
        error.put("884400", "Terminal cannot be determined. Configure from Virtual Terminal Settings");
        error.put("885400", "Service disabled");
        error.put("886400", "Service connection error");
        error.put("887400", "Transaction failed");
        error.put("888400", "Failed to serialize request");
        error.put("889400", "Token only request failed");
        error.put("890400", "Transaction endpoint does not match transaction type");
    }
}
