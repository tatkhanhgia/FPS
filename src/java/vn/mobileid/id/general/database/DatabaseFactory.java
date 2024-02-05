/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.database;

/**
 *
 * @author GiaTK
 */
public class DatabaseFactory {
    public static DatabaseImpl getDatabaseImpl(){
        return new DatabaseImpl_();
    }
    
    public static DatabaseImpl_authorize getDatabaseImpl_authorize(){
        return new DatabaseImpl_authorize_();
    }
    
    public static DatabaseImpl_enterprise getDatabaseImpl_enterprise(){
        return new DatabaseImpl_enterprise_();
    }
    
    public static DatabaseImpl_document getDatabaseImpl_document(){
        return new DatabaseImpl_document_();
    }
    
    public static DatabaseImpl_field getDatabaseImpl_field(){
        return new DatabaseImpl_field_();
    }
}
