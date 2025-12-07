package save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import game.GameState;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * GameState 전체를 JSON으로 저장/로드하는 간단한 세이브 시스템.
 * save.json은 프로젝트 실행 디렉토리에 생성됨.
 */
public class SaveSystem {

    private static final String PATH = "save.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    //세이브 파일 존재 여부 확인
    public boolean hasSave() {
        File f = new File(PATH);
        return f.exists() && f.isFile();
    }

    // 현재 GameState를 json파일로 저장
    public void save(GameState state) {
        if (state == null) return;
        try (FileWriter writer = new FileWriter(PATH)) {
            gson.toJson(state, writer);
            System.out.println("Game saved to " + PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // save.json파일에서 저장 정보를 로드 (없으면 null 반환) 
    public GameState load() {
        File f = new File(PATH);
        if (!f.exists()) {
            System.out.println("No save file found");
            return null;
        }
        try (FileReader reader = new FileReader(f)) {
            GameState state = gson.fromJson(reader, GameState.class);
            System.out.println("Game loaded from " + PATH);
            return state;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
