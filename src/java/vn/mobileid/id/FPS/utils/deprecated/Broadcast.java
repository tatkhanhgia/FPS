package vn.mobileid.id.FPS.utils.deprecated;

///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.utils;
//
//import java.lang.reflect.Method;
//import vn.mobileid.id.FPS.object.InternalResponse;
//
///**
// *
// * @author GiaTK
// */
//public abstract class Broadcast {
//
//    public Method getMethod(String methodName, Class classType) throws Exception {
//        Method[] methods = this.getClass().asSubclass(classType).getDeclaredMethods();
//        for (Method method : methods) {
//            if (method.getName().equalsIgnoreCase(methodName)
//                    && method.getName().contains(methodName)) {
//                method.setAccessible(true);
//                return method;
//            }
//        }
//        return null;
//    }
//
//    public InternalResponse call(Method method, Object... data) throws Exception {
//        try {
//            return (InternalResponse) method.invoke(this, data);
//        } catch (Exception ex) {
//            throw new Exception("Method:"+method.getName(), ex);
//        }
//    }
//;
//}
