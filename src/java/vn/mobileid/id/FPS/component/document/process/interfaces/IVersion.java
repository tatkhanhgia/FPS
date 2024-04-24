/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.component.document.process.interfaces;

/**
 *
 * @author GiaTK
 */
public class IVersion {
    private Version version;

    public IVersion(Version version) {
        this.version = version;
    }

    public Version getVersion() {
        return version;
    }

    public static enum Version {
        V1,
        V2,
        V3
    }
}
