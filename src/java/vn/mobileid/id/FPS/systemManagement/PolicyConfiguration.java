package vn.mobileid.id.FPS.systemManagement;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import vn.mobileid.id.FPS.controller.A_FPSConstant;
import vn.mobileid.id.FPS.database.DatabaseFactory;
import vn.mobileid.id.FPS.object.policy.PolicyConstant;
import vn.mobileid.id.FPS.object.policy.PolicyResponse;
import vn.mobileid.id.FPS.object.policy.PolicySystemConfiguration;
import vn.mobileid.id.helper.ORM_JPA.database.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
public class PolicyConfiguration {

    private static PolicyConfiguration instance;

    private PolicySystemConfiguration systemConfig;

    public static PolicyConfiguration getInstant()  {
        if (instance == null) {
            instance = new PolicyConfiguration();
        }
        return instance;
    }

    private PolicyConfiguration() {
        try {
            DatabaseResponse response = DatabaseFactory.getDatabaseImpl().getPolicies(
                    PolicyConstant.SYSTEMPOLICY, 
                    "transactionId");
                        
            if (response.getStatus() != A_FPSConstant.CODE_SUCCESS) {
                LogHandler.getInstance().fatal(
                        PolicyConfiguration.class,
                        "Init transaction",
                        "Cannot Init Policy !!!");
            } else {
                systemConfig = (PolicySystemConfiguration) convertPolicyResponseToObject(
                    (PolicyResponse) response.getObject(),
                    PolicySystemConfiguration.class);
            }
            

           
        } catch (Exception ex) {
            LogHandler.getInstance().fatal(
                    PolicyConfiguration.class,
                    "Init transaction",
                    "Cannot Init Policy !!!");
            
        }
    }

    /**
     * Convert value in PolicyResposne to Object
     *
     * @param response
     * @param type
     * @return
     */
    private Object convertPolicyResponseToObject(
            PolicyResponse response,
            Class type) {
        Object object = null;
        try {
            object = new ObjectMapper()
                    .enable(DeserializationFeature.UNWRAP_ROOT_VALUE)                    
                    .readValue(response.getValue(), type);
            return object;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }   

    public PolicySystemConfiguration getSystemConfig() {
        return systemConfig;
    }
    
    
    public static void main(String[] args) throws Exception{
        
        PolicySystemConfiguration systemConfig = PolicyConfiguration
                .getInstant()
                .systemConfig;
        System.out.println(systemConfig.getAttributes().get(0).getDateFormat());
        System.out.println(systemConfig.getRemark());        
                
    }
    
}
