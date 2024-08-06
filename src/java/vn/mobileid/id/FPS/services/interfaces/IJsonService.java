/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.FPS.services.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Collection;
import java.util.Map;
import vn.mobileid.id.FPS.services.impls.json.objects.JSAtomic;
import vn.mobileid.id.FPS.services.impls.json.objects.JSObject;

/**
 * Provides methods for serializing objects to JSON strings and deserializing JSON strings to objects.
 * <p>
 * Cung cấp các phương thức để chuyển đổi đối tượng thành chuỗi JSON và chuyển đổi chuỗi JSON thành đối tượng.
 *
 * @author GiaTK
 */
public interface IJsonService {

    /**
     * Serializes an object to a JSON string.
     * <p>
     * Chuyển đổi một đối tượng thành chuỗi JSON.
     *
     * @return A JSON string representation of the object.
     *         Chuỗi JSON đại diện cho đối tượng.
     * @throws JsonProcessingException If an error occurs during serialization.
     *                                 Nếu có lỗi xảy ra trong quá trình chuyển đổi.
     */
    String writeValueAsString(Object object) throws JsonProcessingException;

    /**
     * Deserializes a JSON string to an object of the specified type.
     * <p>
     * Chuyển đổi một chuỗi JSON thành một đối tượng thuộc kiểu được chỉ định.
     *
     * @param json The JSON string to deserialize.
     *             Chuỗi JSON cần chuyển đổi.
     * @param type The class of the object to deserialize to.
     *             Kiểu lớp của đối tượng cần chuyển đổi thành.
     * @param <T>  The type of the object to deserialize to.
     *             Kiểu của đối tượng cần chuyển đổi thành.
     * @return The deserialized object.
     *         Đối tượng đã được chuyển đổi.
     * @throws JsonProcessingException If an error occurs during deserialization.
     *                                 Nếu có lỗi xảy ra trong quá trình chuyển đổi.
     */
    <T> T readValue(String json, Class<T> type) throws JsonProcessingException;
    
    /**
     * Deserializes a JSON string to an object of the specified type.
     * <p>
     * Chuyển đổi một chuỗi JSON thành một đối tượng thuộc kiểu được chỉ định.
     *
     * @param json The JSON string to deserialize.
     *             Chuỗi JSON cần chuyển đổi.
     * @return The deserialized object.
     *         Đối tượng đã được chuyển đổi.
     * @throws JsonProcessingException If an error occurs during deserialization.
     *                                 Nếu có lỗi xảy ra trong quá trình chuyển đổi.
     */
    JSAtomic readTree(String json) throws JsonProcessingException;

}
