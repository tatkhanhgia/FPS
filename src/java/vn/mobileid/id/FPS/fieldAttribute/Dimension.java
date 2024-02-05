/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.fieldAttribute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dimension {

    private float x;
    private float y;
    private float width;
    private float height;

    public Dimension(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Dimension() {
    }

    @JsonProperty("x")
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    @JsonProperty("y")
    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @JsonProperty("width")
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    @JsonProperty("height")
    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
