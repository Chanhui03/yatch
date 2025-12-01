package storage;

import java.io.FileReader;
import java.io.FileWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileIOManager {

    private static final String PATH = "save.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void save(GameSaveData data) {
        try (FileWriter writer = new FileWriter(PATH)) {
            gson.toJson(data, writer);
            System.out.println("[✔] Game saved");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GameSaveData load() {
        try (FileReader reader = new FileReader(PATH)) {
            return gson.fromJson(reader, GameSaveData.class);
        } catch (Exception e) {
            System.out.println("[!] Save file not found");
            return null;
        }
    }
}
