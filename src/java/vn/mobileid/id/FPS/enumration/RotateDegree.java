/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.enumration;

/**
 *
 * @author GiaTK
 */
public enum RotateDegree {
    Rotate_0(0),
    Rotate_90(90),
    Rotate_180(180),
    Rotate_270(270);
    
    private int degree;

    private RotateDegree(int degree) {
        this.degree = degree;
    }

    public int getDegree() {
        return degree;
    }
    
    public static RotateDegree getRotateDegree(int degree){
        for(RotateDegree temp : values()){
            if(degree == temp.degree)
                return temp;
        }
        return RotateDegree.Rotate_0;
    }
}
