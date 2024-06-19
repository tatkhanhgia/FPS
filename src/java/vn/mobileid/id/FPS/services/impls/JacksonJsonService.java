package vn.mobileid.id.FPS.services.impls;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static jdk.nashorn.internal.objects.NativeArray.map;
import vn.mobileid.id.FPS.services.interfaces.IJsonService;
import vn.mobileid.id.FPS.services.objects.JSArray;
import vn.mobileid.id.FPS.services.objects.JSAtomic;
import vn.mobileid.id.FPS.services.objects.JSObject;

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
     * <p>
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

    /**
     * Deserializes the given JSON string into a Java object of the specified type.
     * <p>
     * Chuyển đổi chuỗi JSON đã cho thành một đối tượng Java thuộc kiểu được chỉ định.
     *
     * @param json The JSON string to deserialize.
     * Chuỗi JSON cần chuyển đổi.
     * @return The deserialized Java object.
     * Đối tượng Java đã được chuyển đổi.
     * @throws JsonProcessingException If there is an error during deserialization.
     * Nếu có lỗi xảy ra trong quá trình chuyển đổi.
     */
    @Override
    public JSAtomic readTree(String json) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(json);

//        JSObject root = null;
//        if (node.isArray()) {
//            root = new JSArray();
//        } else {
//            root = new JSObject();
//        }
        if (!node.fields().hasNext()) {
            JSArray jsonArray = new JSArray();
            Iterator<JsonNode> iterators = node.elements();
            while (iterators.hasNext()) {
//                JSObject jsonObject = new JSObject();
//                List<JSAtomic> listAtomic = new ArrayList<>();
//                Iterator<Map.Entry<String, JsonNode>> nodes = iterators.next().fields();
//
//                while (nodes.hasNext()) {
//                    JSAtomic jsonAtomic = new JSAtomic();
//                    Map.Entry<String, JsonNode> object = nodes.next();
//                    String key = object.getKey();
//                    if (object.getValue().isBoolean()) {
//                        jsonAtomic.setName(key);
//                        jsonAtomic.setData(object.getValue().asBoolean());
//
//                        listAtomic.add(jsonAtomic);
//                        continue;
//                    }
//                    if (object.getValue().isFloat()) {
//                        jsonAtomic.setName(key);
//                        jsonAtomic.setData(object.getValue().asDouble());
//
//                        listAtomic.add(jsonAtomic);
//                        continue;
//                    }
//                    if (object.getValue().isLong()) {
//                        jsonAtomic.setName(key);
//                        jsonAtomic.setData(object.getValue().asLong());
//
//                        listAtomic.add(jsonAtomic);
//                        continue;
//                    }
//                    if (object.getValue().isInt()) {
//                        jsonAtomic.setName(key);
//                        jsonAtomic.setData(object.getValue().asInt());
//
//                        listAtomic.add(jsonAtomic);
//                        continue;
//                    }
//                    if (object.getValue().isTextual()) {
//                        jsonAtomic.setName(key);
//                        jsonAtomic.setData(object.getValue().asText());
//
//                        listAtomic.add(jsonAtomic);
//                        continue;
//                    }
//                    if (object.getValue().isArray()) {
//                        jsonAtomic.setName(key);
//                        jsonAtomic.setData(object.getValue().asToken().asByteArray());
//
//                        listAtomic.add(jsonAtomic);
//                        continue;
//                    }
//                    if (object.getValue().isContainerNode()) {
//                        Object temp = object.getValue().toPrettyString();
//                        if (temp == null) {
//                            continue;
//                        }
//                    }
//                    if (object.getValue().isDouble()) {
//                        jsonAtomic.setName(key);
//                        jsonAtomic.setData(object.getValue().asDouble());
//
//                        listAtomic.add(jsonAtomic);
//                        continue;
//                    }
//                }
//                jsonObject.setData(listAtomic);

                jsonArray.addIntoArray(readObject(iterators.next()));
            }
            return jsonArray;
        } else {
            return readObject(node);
        }
//        return null;
    }

    private JSObject readObject(JsonNode node) {
        JSObject jsonObject = new JSObject();
        List<? super JSAtomic> listAtomic = new ArrayList<>();
        Iterator<Map.Entry<String, JsonNode>> nodes = node.fields();

        while (nodes.hasNext()) {
            JSAtomic jsonAtomic = new JSAtomic();
            Map.Entry<String, JsonNode> object = nodes.next();
            String key = object.getKey();
            if (object.getValue().isBoolean()) {
                jsonAtomic.setName(key);
                jsonAtomic.setData(object.getValue().asBoolean());

                listAtomic.add(jsonAtomic);
                continue;
            }
            if (object.getValue().isFloat()) {
                jsonAtomic.setName(key);
                jsonAtomic.setData(object.getValue().asDouble());

                listAtomic.add(jsonAtomic);
                continue;
            }
            if (object.getValue().isLong()) {
                jsonAtomic.setName(key);
                jsonAtomic.setData(object.getValue().asLong());

                listAtomic.add(jsonAtomic);
                continue;
            }
            if (object.getValue().isInt()) {
                jsonAtomic.setName(key);
                jsonAtomic.setData(object.getValue().asInt());

                listAtomic.add(jsonAtomic);
                continue;
            }
            if (object.getValue().isTextual()) {
                jsonAtomic.setName(key);
                jsonAtomic.setData(object.getValue().asText());

                listAtomic.add(jsonAtomic);
                continue;
            }
            if (object.getValue().isArray()) {
                jsonAtomic.setName(key);
                jsonAtomic.setData(object.getValue().asToken().asByteArray());

                listAtomic.add(jsonAtomic);
                continue;
            }
            if (object.getValue().isContainerNode()) {
                listAtomic.add(readObject(object.getValue()));
                continue;
            }
            if (object.getValue().isDouble()) {
                jsonAtomic.setName(key);
                jsonAtomic.setData(object.getValue().asDouble());

                listAtomic.add(jsonAtomic);
                continue;
            }
        }
        jsonObject.setData(listAtomic);

        return jsonObject;
    }

    public static void main(String[] args) throws Exception {
//        String json = "[{\"uuid\":\"CBFC9BF3924F769390EC2C2436866167\",\"time\":86400,\"timeStamp\":\"12/06/2024 03:33:44\"}]";
//        String json = "{\"name\":\"Tất Khánh Gia\",\"age\":24}";
        String json = "{\"name\":\"Tất Khánh Gia\",\"age\":24,\"object\":{\"name2\":\"name2Ne\",\"age2\":24}}";
        JacksonJsonService temp = new JacksonJsonService();
        JSAtomic map = temp.readTree(json);
        System.out.println(map instanceof JSObject);
        System.out.println(map instanceof JSArray);
        System.out.println(((JSObject) map).getData().get(0).getName());
        System.out.println((String) ((JSObject) map).getData().get(0).getData());
        if(((JSObject) map).getData().get(2) instanceof JSObject){
            JSObject tempp = (JSObject)((JSObject) map).getData().get(2);
            System.out.println(tempp.getData().get(0).getName());
            System.out.println((String)tempp.getData().get(0).getData());
            System.out.println(tempp.getData().get(1).getName());
            System.out.println((Integer)tempp.getData().get(1).getData());
        }
    }
}
