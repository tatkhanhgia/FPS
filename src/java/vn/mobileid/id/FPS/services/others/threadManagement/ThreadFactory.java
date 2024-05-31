/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.others.threadManagement;

import vn.mobileid.id.FPS.services.others.threadManagement.impls.CachedThreadPool;

/**
 *
 * @author GiaTK
 */
public class ThreadFactory {
    public static ThreadManagement newCachedThreadPool(int numberTask){
        return new ThreadManagement(new CachedThreadPool(), numberTask);
    }
}
