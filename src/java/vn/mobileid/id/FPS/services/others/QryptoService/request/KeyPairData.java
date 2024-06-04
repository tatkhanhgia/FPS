/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.QryptoService.request;

import java.security.KeyPair;

/**
 *
 * @author GIATK    
 */
public class KeyPairData {
    private String nameSigAlg;
    private String nameType;
    private KeyPair keypair;

    public KeyPairData(String nameSigAlg, String nameType, KeyPair keypair) {
        this.nameSigAlg = nameSigAlg;
        this.nameType = nameType;
        this.keypair = keypair;
    }

    public String getNameSigAlg() {
        return nameSigAlg;
    }

    public void setNameSigAlg(String nameSigAlg) {
        this.nameSigAlg = nameSigAlg;
    }

    public String getNameType() {
        return nameType;
    }

    public void setNameType(String nameType) {
        this.nameType = nameType;
    }  

    public KeyPair getKeypair() {
        return keypair;
    }

    public void setKeypair(KeyPair keypair) {
        this.keypair = keypair;
    }
    
}
