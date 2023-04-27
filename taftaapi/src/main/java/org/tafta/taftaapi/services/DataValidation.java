package org.tafta.taftaapi.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sla.pegpayapi.repo.DBFunctionImpl;

/**
 * @author Gathariki Ngigi
 * Created on April 27, 2023.
 * Time 0905h
 */

@Slf4j
@Service
public class DataValidation {
    @Autowired
    private DBFunctionImpl dbFunction;
//    @Autowired
//    BusinessValidation   businessValidation;

    final String validationErrCode = "041400";

//    public boolean isRequestValid(TransactionEntries transactionEntries, CustomResponse customResponse) {
//        List<Map<String, String>> errors = new ArrayList<>();
//
//        try {
//            if (transactionEntries.getEntries() == null) {
//                errors.add(Errors.get(validationErrCode,"Transaction entries cannot be null"));
//            }
//
//            for (Transaction transaction : transactionEntries.getEntries()){
//                // partnerID
//                if (transaction.getPartnerId() == null || transaction.getPartnerId().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Partner ID cannot be null/empty"));
//                }
//
//                // transactionRef
//                if (transaction.getTransactionRef() == null || transaction.getTransactionRef().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Transaction Ref cannot be null/empty"));
//                }
//
//                // transactionDate
//                if (transaction.getTransactionDate() != null){
//                    // check date format
//                    Date transactionDate = Dates.getDateValue(transaction.getTransactionDate());
//
//                    if(transactionDate == null){
//                        errors.add(Errors.get(validationErrCode, "Wrong format for transaction date"));
//                    }
//                }
//
//                // transactionType
//                if (transaction.getTransactionType() == null || transaction.getTransactionType().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Transaction type cannot be null/empty"));
//                }
//
//                // senderAddress
//                if (transaction.getSenderAddress() == null || transaction.getSenderAddress().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Sender address cannot be null/empty"));
//                }
//
//                // senderCity
//                if (transaction.getSenderCity() == null || transaction.getSenderCity().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Sender city cannot be null/empty"));
//                }
//
//                // senderCountryCode
//                if (transaction.getSenderCountryCode() == null || transaction.getSenderCountryCode().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Sender country code cannot be null/empty"));
//                }
//
//                // senderCurrencyCode
//                if (transaction.getSenderCurrencyCode() == null || transaction.getSenderCurrencyCode().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Sender currency code cannot be null/empty"));
//                }
//
//                // senderDob
//                if (transaction.getSenderDob() == null || transaction.getSenderDob().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Sender date of birth cannot be null/empty"));
//                }else{
//                    // check date format
//                    Date senderDob = Dates.getDateValue(transaction.getSenderDob());
//
//                    if(senderDob == null){
//                        errors.add(Errors.get(validationErrCode, "Sender date of birth cannot be null/empty"));
//                    }
//                }
//
//                // senderFullName
//                if (transaction.getSenderFullName() == null || transaction.getSenderFullName().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Sender full name cannot be null/empty"));
//                }
//
//                // senderIdExpiryDate - if available then check format
//                if (transaction.getSenderIdExpiryDate() != null && !transaction.getSenderIdExpiryDate().isEmpty()){
//                    // check date format
//                    Date senderIdExpiryDate = Dates.getDateValue(transaction.getSenderIdExpiryDate());
//
//                    if(senderIdExpiryDate == null){
//                        errors.add(Errors.get(validationErrCode, "Wrong format for Sender ID expiry date"));
//                    }
//                }
//
//                // senderIdNumber
//                if (transaction.getSenderIdNumber() == null || transaction.getSenderIdNumber().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Sender ID number cannot be null/empty"));
//                }
//
//                // senderIdType
//                if (transaction.getSenderIdType() == null || transaction.getSenderIdType().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Sender ID type cannot be null/empty"));
//                }
//
//                // senderMobile
//                if (transaction.getSenderMobile() == null || transaction.getSenderMobile().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Sender mobile number cannot be null/empty"));
//                }
//
//                // senderSourceOfFunds - default to Family support if empty
//                if (transaction.getSenderSourceOfFunds() == null || transaction.getSenderSourceOfFunds().isEmpty()){
//                    transaction.setSenderSourceOfFunds("Family support");
//                }
//
//                // senderType
//                if (transaction.getSenderType() == null || transaction.getSenderType().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Sender type cannot be null/empty"));
//                }
//
//                // sendAmount
//                if (transaction.getSendAmount() == null || transaction.getSendAmount().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Amount to send cannot be null/empty"));
//                }
//
//                // sendAmount
//                if (transaction.getSendAmount() == null || transaction.getSendAmount().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Amount to send cannot be null/empty"));
//                }
//
//                // optional for M - mobile
//                if (transaction.getTransactionType().equalsIgnoreCase("B") || transaction.getTransactionType().equalsIgnoreCase("CRDB-EFT")){
//                    // if Bank or CRDB-EFT
//
//                    // receiverAccount
//                    if (transaction.getReceiverAccount() == null || transaction.getReceiverAccount().isEmpty()){
//                        errors.add(Errors.get(validationErrCode, "Receiver account cannot be null/empty"));
//                    }
//
//                    // receiverBank
//                    if (transaction.getReceiverBank() == null || transaction.getReceiverBank().isEmpty()){
//                        errors.add(Errors.get(validationErrCode, "Receiver bank number cannot be null/empty"));
//                    }
//
//                    // receiverBankCode
//                   if (transaction.getReceiverBankCode() == null || transaction.getReceiverBankCode().isEmpty()){
//                        errors.add(Errors.get(validationErrCode, "Receiver bank code cannot be null/empty"));
//                    }else{
//                        // check from DB whether bank exists
//                        Map response = isBankPresent(transaction.getReceiverBankCode(), transaction.getReceiverSwiftcode());
//
//                        if(!Boolean.parseBoolean(String.valueOf(response.get("exists")))){
//                            errors.add(Errors.get("283400", "Receiver bank code is not maintained on the system/or does not clear on pesalink"));
//                        }
//                    }
//                }
//
//                // receiverAmount
//                if (transaction.getReceiverAmount() == null || transaction.getReceiverAmount().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Receiver amount cannot be null/empty"));
//                }
//
//                // receiverCity
//                if (transaction.getReceiverCity() == null || transaction.getReceiverCity().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Receiver city cannot be null/empty"));
//                }
//
//                // receiverCountryCode
//                if (transaction.getReceiverCountryCode() == null || transaction.getReceiverCountryCode().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Receiver country code cannot be null/empty"));
//                }
//
//                // receiverCurrencyCode
//                if (transaction.getReceiverCurrencyCode() == null || transaction.getReceiverCurrencyCode().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Receiver currency code cannot be null/empty"));
//                }
//
//                // receiverFullName
//                if (transaction.getReceiverFullName() == null || transaction.getReceiverFullName().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Receiver full name cannot be null/empty"));
//                }
//
//                // receiverIdType
//                if (transaction.getReceiverIdType() == null || transaction.getReceiverIdType().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Receiver ID type cannot be null/empty"));
//                }
//
//                // receiverType
//                if (transaction.getReceiverType() == null || transaction.getReceiverType().isEmpty()){
//                    errors.add(Errors.get(validationErrCode, "Receiver type cannot be null/empty"));
//                }
//
//                // if exchange rate is present, ensure it's not equal or below zero
//                if (transaction.getExchangeRate() != null && !transaction.getExchangeRate().isEmpty()){
//                    if(Float.parseFloat(transaction.getExchangeRate()) <= 0) {
//                        errors.add(Errors.get(validationErrCode, "Exchange rate must be greater than zero"));
//                    }
//                }
//
//                // Business validation
//                businessValidation.validateRequestPerCorridor(transaction, errors);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//
//            errors.add(Errors.get(validationErrCode, "Something went wrong. " + e.getMessage()));
//        }
//
//        if(errors.size() > 0){
//            customResponse.setResponseCode("400");
//            customResponse.setResource(null);
//            customResponse.setTransaction(null);
//            customResponse.setErrors(errors);
//        }
//
//        return errors.size() == 0;
//    }

    public boolean isEmailValid(String emailAddress) {
        String regexPattern = "^(.+)@(.+)[.]\\\\S{2,}(.+)$";

        return emailAddress.matches(regexPattern);
    }

//    public Map<String, Object> areFieldsValid(Map<String, Object> fieldsMap, List<String> requiredFields){
//        Map<String, Object> response = new HashMap<>();
//        List<Map<String, String>> errors = new ArrayList<>();
//
//        // specific field to be checked
//        for (String eachRequiredField : requiredFields) {
//            log.error(eachRequiredField + " : " + fieldsMap.get(eachRequiredField));
//
//            String firstCharOfFirstFieldName;
//            String restOfCharsOfFirstFieldName;
//            String firstStr;
//            StringBuilder secondStr = new StringBuilder();
//            String fieldName;
//            String[] splitFields = eachRequiredField.split("_");
//
//            if (splitFields.length > 1) {
//                firstCharOfFirstFieldName = String.valueOf(eachRequiredField.split("_")[0].charAt(0)).toUpperCase();
//                restOfCharsOfFirstFieldName = splitFields[0].substring(1).toLowerCase();
//
//                firstStr = firstCharOfFirstFieldName + restOfCharsOfFirstFieldName;
//
//                for (int i = 1; i < splitFields.length; i++) {
//                    secondStr.append(splitFields[i].toLowerCase()).append(" ");
//                }
//
//                fieldName = firstStr + " " + secondStr;
//            }else {
//                firstCharOfFirstFieldName = String.valueOf(eachRequiredField.split("_")[0].charAt(0)).toUpperCase();
//                restOfCharsOfFirstFieldName = eachRequiredField.split("_")[0].substring(1).toLowerCase();
//
//                firstStr = firstCharOfFirstFieldName + restOfCharsOfFirstFieldName;
//
//                fieldName = firstStr;
//            }
//
//            if (Optional.ofNullable(fieldsMap.get(eachRequiredField)).orElse("").toString().isEmpty()) {
//                errors.add(Errors.get(validationErrCode, fieldName + " cannot be null/empty"));
//
//                response.put("valid", "false");
//                response.put("errors", Errors.get(validationErrCode, fieldName + "cannot be null/empty"));
//
//                break;
//            }else{
//                // if field not empty - check format for paybill - c2b, b2c
//                if(eachRequiredField.equalsIgnoreCase("paybill_type")){
//                    String c2b = PaybillType.C2B.toString().toLowerCase();
//                    String b2c = PaybillType.B2C.toString().toLowerCase();
//
//                    if(!(eachRequiredField.toLowerCase().equalsIgnoreCase(c2b) || eachRequiredField.toLowerCase().equalsIgnoreCase(b2c))){
//                        response.put("valid", "false");
//                        response.put("errors", Errors.get(validationErrCode, "Paybill is in wrong format - [C2B, B2C]"));
//                    }
//                }
//            }
//        }
//
//        if(!(errors.size() > 0)){
//            response.put("valid", "true");
//            response.put("data", null);
//        }
//
//        return response;
//    }

}
