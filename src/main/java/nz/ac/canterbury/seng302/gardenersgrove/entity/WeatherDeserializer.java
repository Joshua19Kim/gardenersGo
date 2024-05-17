package nz.ac.canterbury.seng302.gardenersgrove.entity;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;


public class WeatherDeserializer extends StdDeserializer<Weather> {

    public WeatherDeserializer() {
        this(null);
    }

    public WeatherDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Weather deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException {
        Weather weather = new Weather();
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        // try catch block
        JsonNode location = node.get("location");
        weather.setLocation(location);
        return weather;
    }
}
