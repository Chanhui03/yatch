package storage;

import game.GameManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileIOManager {

    private static final String SAVE_FILE = "save.json";
    private Gson gson;

    public FileIOManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    // 게임 저장
    public void save(GameManager manager) {
        try (FileWriter writer = new FileWriter(SAVE_FILE)) {
            String json = gson.toJson(manager.toSaveData());
            writer.write(json);
            System.out.println("게임 저장 완료!");
        } catch (IOException e) {
            System.out.println("저장 중 오류 발생: " + e.getMessage());
        }
    }

    // 게임 불러오기
    public GameSaveData load() {
        try (FileReader reader = new FileReader(SAVE_FILE)) {
            return gson.fromJson(reader, GameSaveData.class);
        } catch (IOException e) {
            System.out.println("저장된 파일이 없거나 읽기 오류.");
            return null;
        }
    }
}
