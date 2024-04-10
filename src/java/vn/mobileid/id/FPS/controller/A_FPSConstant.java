 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.controller;

/**
 *
 * @author GiaTK
 */
public class A_FPSConstant {
    //System        
    final public static int STATUS_PENDING = 1;
    final public static int STATUS_UPLOADED = 2;
    final public static int STATUS_ARCHIVED = 3;
    final public static int STATUS_DELETED = 4;
    final public static int STATUS_MARK_DELETED = 5;
    
    final public static String INTERNAL_EXP_MESS = "{[\"Internal server exception\"]}";
    final public static String DEFAULT_MESS = "Message is not defined";
//    final public static int DEFAULT_ROW_COUNT = PolicyConfiguration
//            .getInstant()
//            .getSystemConfig()
//            .getAttributes().get(0)
//            .getDefault_row_count();
    final public static int DEFAULT_ROW_COUNT = 100;
    
    final public static String TOKEN_TYPE_BEARER = "Bearer";
    final public static String TOKEN_TYPE_BASIC = "Basic";
    final public static String TOKEN_TYPE_REFRESH = "Refresh";

    final public static int LANGUAGE_VN = 1;
    final public static int LANGUAGE_EN = 2;        
    
    //AccessToken Data
    final public static String alg = "SHA256withRSA";
//    final public static String alg = PolicyConfiguration
//            .getInstant()
//            .getSystemConfig()
//            .getAttributes().get(0)
//            .getTokenConfig()
//            .getAlg();
    final public static String typ = "JWT";
//    final public static String typ = PolicyConfiguration
//            .getInstant()
//            .getSystemConfig()
//            .getAttributes().get(0)
//            .getTokenConfig()
//            .getTyp();
    final public static long expired_in = 3600;
//    final public static long expired_in = PolicyConfiguration
//            .getInstant()
//            .getSystemConfig()
//            .getAttributes().get(0)
//            .getTokenConfig()
//            .getAccess_token_expired_in();
    final public static long refresh_token_expired_in = 86400;
//    final public static long refresh_token_expired_in = PolicyConfiguration
//            .getInstant()
//            .getSystemConfig()
//            .getAttributes().get(0)
//            .getTokenConfig()
//            .getRefresh_token_expired_in();

    //Default data
    final public static long password_expired_at = 2592000;
//    final public static long password_expired_at = PolicyConfiguration
//            .getInstant()
//            .getSystemConfig()
//            .getAttributes().get(0)
    

    //Internal - HTTP CODE
    final public static int HTTP_CODE_SUCCESS = 200;
    final public static int HTTP_CODE_CREATED = 201;
    final public static int HTTP_CODE_FORBIDDEN = 403;
    final public static int HTTP_CODE_UNAUTHORIZED = 401;
    final public static int HTTP_CODE_BAD_REQUEST = 400;
    final public static int HTTP_CODE_NOT_FOUND = 404;
    final public static int HTTP_CODE_METHOD_NOT_ALLOWED = 405;    
    final public static int HTTP_CODE_INTERNAL_SERVER_ERROR = 500;
    final public static int HTTP_CODE_UNSUPPORTED_MEDIA_TYPE = 415;

    //Code
    final public static int CODE_SUCCESS = 0;
    final public static int CODE_FAIL = 1;    
    final public static int CODE_KEYCLOAK = 2;
    final public static int CODE_FMS = 3;
    final public static int CODE_FIELD = 4;
    final public static int CODE_DOCUMENT = 5;
    final public static int CODE_FIELD_TEXT = 6;
    final public static int CODE_FIELD_SIGNATURE = 7;
    final public static int CODE_FIELD_CHECKBOX = 8;
    final public static int CODE_FIELD_INITIAL = 9;
    final public static int CODE_FIELD_QR = 10;
    final public static int CODE_FIELD_QR_Qrypto = 11;
    final public static int CODE_ERROR_WHILE_CALLING_THREAD = 12;
    final public static int CODE_FIELD_STAMP = 13;
    final public static int CODE_FIELD_CAMERA = 14;
    final public static int CODE_FIELD_RADIO_BOX = 15;
    final public static int CODE_FIELD_ATTACHMENT = 16;
    final public static int CODE_FIELD_HYPERLINK = 17;
    final public static int CODE_FIELD_COMBOBOX = 18;
    final public static int CODE_FIELD_NUMERIC_STEPPER = 19;
    
    //SubCode
    final public static int SUBCODE_NO_PAYLOAD_FOUND = 1;
    final public static int SUBCODE_INVALID_PAYLOAD_STRUCTURE = 2;
    final public static int SUBCODE_MISSING_FILE_DATA = 3;
    final public static int SUBCODE_MISSING_AUTHORIZATION_HEADER = 4;
    final public static int SUBCODE_CANNOT_ANALYSIS_FILE = 5;
    final public static int SUBCODE_BASE64_IS_INVALID_SCHEME = 6;
    final public static int SUBCODE_MISSING_UUID = 7;
    final public static int SUBCODE_MISSING_DOCUMENT_ID = 8;
    
    //SubCode Keycloak 
    final public static int SUBCODE_INVALID_TOKEN = 1;
    final public static int SUBCODE_TOKEN_EXPIRED = 2;
    final public static int SUBCODE_INVALID_CLIENT_ID = 3;
    final public static int SUBCODE_INVALID_CLIENT_SECRET = 4;
    final public static int SUBCODE_MISSING_GRANT_TYPE = 5;
    final public static int SUBCODE_UNSUPPORTED_GRANT_TYPE = 6;
    final public static int SUBCODE_MISSING_CLIENT_SECRET = 7;
    final public static int SUBCODE_MISSING_CLIENT_ID = 8;
    final public static int SUBCODE_MISSING_ACCESS_TOKEN = 9;
    
    //SubCode FMS
    final public static int SUBCODE_ERROR_WHILE_UPLOAD_FMS = 1;
    final public static int SUBCODE_FMS_REJECT_UPLOAD = 2;
    final public static int SUBCODE_ERROR_WHILE_DOWNLOAD_FMS = 3;
    final public static int SUBCODE_FMS_REJECT_DOWNLOAD = 4;
    
    //Subcode FIELD - 4
    final public static int SUBCODE_MISSING_FIELD_NAME = 1;
    final public static int SUBCODE_MISSING_PAGE = 2;
    final public static int SUBCODE_MISSING_DIMENSION = 3;
    final public static int SUBCODE_FIELD_ALREADY_PROCESS = 4;
    final public static int SUBCODE_THIS_TYPE_OF_FIELD_IS_NOT_VALID_FOR_THIS_PROCESSION = 5;
    final public static int SUBCODE_INVALID_FIELD_NAME = 102106;
    final public static int SUBCODE_THIS_DOCUMENT_IS_ALREADY_CHANGES_GETHASH_AGAIN = 6;
    final public static int SUBCODE_CANNOT_FILL_ALL_FORM_FIELD = 7;
    final public static int SUBCODE_PAGE_IN_FIELD_NEED_TO_BE_LOWER_THAN_DOCUMENT = 8;
    
    //Subcode DOCUMENT - 5
    final public static int SUBCODE_DOCUMENT_STATSUS_IS_DISABLE = 1;
    final public static int SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD = 2;
    final public static int SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_UPDATE_FIELD_DETAILS = 3;
    final public static int SUBCODE_CANNOT_VERIFY_THIS_DOCUMENT = 4;
    final public static int SUBCODE_THE_DOCUMENT_STATUS_IS_PENDING = 5;
    final public static int SUBCODE_THE_DOCUMENT_STATUS_IS_DELETED = 6;
    final public static int SUBCODE_THE_DOCUMENT_STATUS_IS_PROCESSING = 7;
    final public static int SUBCODE_CANNOT_ANNALYSIS_THE_DOCUMENT = 8;
    final public static int SUBCODE_THIS_DOCUMENT_DOES_NOT_HAVE_ANY_FIELD = 9;
    final public static int SUBCODE_PROCESS_SUCCESSFUL_BUT_CANNOT_CREATE_NEW_REVISION_OF_DOCUMENT = 10;
    
    //Subcode Field text - 6
    final public static int SUBCODE_INVALID_TEXT_FIELD_TYPE = 1;
    final public static int SUBCODE_MISSING_TEXT_FIELD_TYPE = 2;
    final public static int SUBCODE_CANNOT_CREATE_FORM_FIELD = 3;
    
    //Subcode Field Signature - 7
    final public static int SUBCODE_MISSING_SIGNATURE_VALUE = 1;
    final public static int SUBCODE_MISSING_OR_EMTYPE_CERTIFICATES_CHAIN = 2;
    final public static int SUBCODE_MISSING_SIGNATURE_ALGORITHM = 3;
    final public static int SUBCODE_MISSING_SIGNED_HASH = 4;
    final public static int SUBCODE_THE_HAND_IMAGE_SIGNATURE_CANNOT_PARSE = 5;
    final public static int SUBCODE_CANNOT_PROCESS_THIS_DOCUMENT = 6;
    final public static int SUBCODE_CANNOT_GENERATE_HASH_OF_THIS_SIGNATURE_FIELD = 7;
    
    //Subcode Field CheckBox - 8
    final public static int SUBCODE_INVALID_CHECKBOX_FIELD_TYPE = 1;
    
    //Subcode Field Initial - 9 
    final public static int SUBCODE_MISSING_OR_EMPTY_IMAGE_OF_INITIAL = 1;
    final public static int SUBCODE_CANNOT_FILL_INITIALS = 2;
    final public static int SUBCODE_ERROR_WHILE_PROCESSING_MULTI_THREAD = 3;
    
    //Subcode Field QR - 10
    final public static int SUBCODE_INVALID_QR_TYTPE = 1;
    final public static int SUBCODE_MISSING_ENCODE_STRING_OF_QR = 2;
    final public static int SUBCODE_CANNOT_GENERATE_QR = 3;
    
    //CODE_FIELD_QR_Qrypto - 11
    final public static int SUBCODE_INVALID_FORMAT_OF_ITEM = 1;
    final public static int SUBCODE_CANNOT_CREATE_QR = 2;
    final public static int SUBCODE_FILE_TOKEN_NOT_FOUND = 3;
    final public static int SUBCODE_CANNOT_DOWNLOAD_FILE_FROM_QRYPTO = 4;
    final public static int SUBCODE_MISSING_ITEMS = 5;
    final public static int SUBCODE_INVALID_TYPE_OF_ITEM = 6;
    
    //Subcode Error while calling thread - 12
    final public static int SUBCODE_THREAD_GET_FIELDS_IN_UPDATE_INITIALFIELD = 1;
    
    //Subcode Field Stamp - 13
    final public static int SUBCODE_INVALID_STAMP_FIELD_TYPE = 1;
    final public static int SUBCODE_MISSING_IMAGE = 2;
    final public static int SUBCODE_VALUE_MUST_BE_ENCODE_BASE64_FORMAT = 3;
    
    //Subcode Camera - 14
    final public static int SUBCODE_INVALID_CAMERA_FIELD_TYPE = 1;
    
    //Subcode Radio - 15
    final public static int SUBCODE_INVALID_TYPE_OF_RADIO = 1;
    
    //Subcode Attachment - 16
    final public static int SUBCODE_INVALID_ATTACHMENT_FIELD_TYPE = 1;
    final public static int SUBCODE_MISSING_EXTENSION = 2;
    final public static int SUBCODE_MISSING_FILE_DATA_OF_ATTACHMENT = 3;
    
    //Subcode hyperlink - 17
    final public static int SUBCODE_INVALID_HYPERLINK_TYPE = 1;
    final public static int SUBCODE_VALUE_MUST_BE_A_STRING = 2;
    
    //Subcode ComboBox - 18
    final public static int SUBCODE_INVALID_COMBOBOX_FIELD_TYPE = 1;
    final public static int SUBCODE_MISSING_DEFAULT_ITEMS_FOR_PROCESS = 2;
    
    //Subcode Numeric Stepper - 19
    final public static int SUBCODE_MISSING_DEFAULT_VALUE_FOR_PROCESS = 1;
}
