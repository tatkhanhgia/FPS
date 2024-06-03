/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.database;

import vn.mobileid.id.FPS.database.implement.AuthorizeDBImpl;
import vn.mobileid.id.FPS.database.implement.DatabaseImpl;
import vn.mobileid.id.FPS.database.implement.DocumentDBImpl;
import vn.mobileid.id.FPS.database.implement.EnterpriseDBImpl;
import vn.mobileid.id.FPS.database.implement.FieldDBImpl;
import vn.mobileid.id.FPS.database.interfaces.IAuthorizeDB;
import vn.mobileid.id.FPS.database.interfaces.IDatabase;
import vn.mobileid.id.FPS.database.interfaces.IDocumentDB;
import vn.mobileid.id.FPS.database.interfaces.IEnterpriseDB;
import vn.mobileid.id.FPS.database.interfaces.IFieldDB;


/**
 *
 * @author GiaTK
 */
public class DatabaseFactory {
    public static IDatabase getDatabaseImpl(){
        return new DatabaseImpl();
    }
    
    public static IAuthorizeDB getDatabaseImpl_authorize(){
        return new AuthorizeDBImpl();
    }
    
    public static IEnterpriseDB getDatabaseImpl_enterprise(){
        return new EnterpriseDBImpl();
    }
    
    public static IDocumentDB getDatabaseImpl_document(){
        return new DocumentDBImpl();
    }
    
    public static IFieldDB getDatabaseImpl_field(){
        return new FieldDBImpl();
    }
}
