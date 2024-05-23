package vn.mobileid.id.FPS.services.impls;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import vn.mobileid.id.FPS.services.interfaces.IJsonService;

/**
 * JsonService provides utility methods for serializing and deserializing Java objects
 * to and from JSON format using the Jackson library.
 * <p>
 * JacksonJsonService cung cấp các phương thức tiện ích để chuyển đổi các đối tượng Java thành chuỗi JSON
 * và chuyển đổi chuỗi JSON thành các đối tượng Java bằng thư viện Jackson.
 *
 * @author GiaTK
 */
public class JacksonJsonService implements IJsonService {

    private final ObjectMapper objectMapper;

    /**
     * Constructs a JsonService instance with the given ObjectMapper.
     * <p>
     * Khởi tạo một đối tượng IJsonService với ObjectMapper được cung cấp.
     *
     * @param objectMapper The ObjectMapper to use for serialization and deserialization.
     * ObjectMapper được sử dụng để chuyển đổi giữa đối tượng và JSON.
     */
    public JacksonJsonService() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Constructs a JsonService instance with the given ObjectMapper.
     * <p>
     * Khởi tạo một đối tượng IJsonService với ObjectMapper được cung cấp.
     *
     * @param objectMapper The ObjectMapper to use for serialization and deserialization.
     * ObjectMapper được sử dụng để chuyển đổi giữa đối tượng và JSON.
     */
    public JacksonJsonService(ObjectMapper objectMapper) {
        if (objectMapper == null) {
            throw new IllegalArgumentException("ObjectMapper cannot be null");
        }
        this.objectMapper = objectMapper;
    }

    /**
     * Serializes the given Java object into a JSON string.
     * <p>
     * Chuyển đổi đối tượng Java đã cho thành một chuỗi JSON.
     *
     * @param object The Java object to serialize.
     * Đối tượng Java cần chuyển đổi.
     * @return The serialized JSON string.
     * Chuỗi JSON được chuyển đổi.
     * @throws JsonProcessingException If there is an error during serialization.
     * Nếu có lỗi xảy ra trong quá trình chuyển đổi.
     */
    @Override
    public String writeValueAsString(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    /**
     * Deserializes the given JSON string into a Java object of the specified type.
     * <p>
     * Chuyển đổi chuỗi JSON đã cho thành một đối tượng Java thuộc kiểu được chỉ định.
     *
     * @param json The JSON string to deserialize.
     * Chuỗi JSON cần chuyển đổi.
     * @param type The class representing the type of the Java object to create.
     * Kiểu lớp đại diện cho kiểu của đối tượng Java cần tạo.
     * @param <T> The type of the Java object to create.
     * Kiểu của đối tượng Java cần tạo.
     * @return The deserialized Java object.
     * Đối tượng Java đã được chuyển đổi.
     * @throws JsonProcessingException If there is an error during deserialization.
     * Nếu có lỗi xảy ra trong quá trình chuyển đổi.
     */
    public <T> T readValue(String json, Class<T> type) throws JsonProcessingException {
        return objectMapper.readValue(json, type);
    }
}
