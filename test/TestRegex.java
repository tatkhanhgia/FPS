/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Admin
 */
public class TestRegex {
    public static void main(String[] args){
        String regex = "^/FPS/v1/(authenticate|tokens|info)$";
        String URL = "/FPS/v1/authenticate";
        System.out.println(URL.matches(regex));
    }
}
