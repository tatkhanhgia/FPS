///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.FPS.fieldAttribute;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import java.util.List;
//import vn.mobileid.id.FPS.object.Signature;
//
//
///**
// *
// * @author GiaTK
// */
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
//public class SignatureFieldAttribute extends BasicFieldAttribute{    
//    private Signature verification;
//    private List<String> levelOfAssurance;
//
//    public SignatureFieldAttribute() {
//    }
//
//    public void setVerification(Signature verification) {
//        this.verification = verification;
//    }
//
//    public void setLevelOfAssurance(List<String> levelOfAssurance) {
//        this.levelOfAssurance = levelOfAssurance;
//    }
//
//    //===========================================================================
//    @JsonProperty("verification")
//    public Signature getVerification() {
//        return verification;
//    }
//
//    @JsonProperty("level_of_assurance")
//    public List<String> getLevelOfAssurance() {
//        return levelOfAssurance;
//    }        
//    
//    public SignatureFieldAttribute clone(){
//        SignatureFieldAttribute clone = new SignatureFieldAttribute();
//        clone.setLevelOfAssurance(this.levelOfAssurance);
//        clone.setVerification(this.verification);
//        return clone;
//    }
//}
