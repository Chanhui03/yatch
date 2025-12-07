package save;

import com.google.gson.*;
import game.ScoreCategory;

import java.lang.reflect.Type;

public class ScoreCategoryAdapter implements JsonSerializer<ScoreCategory>, JsonDeserializer<ScoreCategory> {

    @Override
    public JsonElement serialize(ScoreCategory src, Type typeOfSrc, JsonSerializationContext context) {
        // 저장 시 displayName만 넣기
        return new JsonPrimitive(src.getDisplayName());
    }

    @Override
    public ScoreCategory deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        String name = json.getAsString();

        // displayName이 같은 ScoreCategory 객체 찾아 반환
        return ScoreCategory.ALL.stream()
                .filter(c -> c.getDisplayName().equals(name))
                .findFirst()
                .orElseThrow(() -> new JsonParseException("Unknown ScoreCategory: " + name));
    }
}
