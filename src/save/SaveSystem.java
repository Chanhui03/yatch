package save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import game.GameState;
import game.ScoreCategory;

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

    // ScoreCategory 커스텀 어댑터를 등록한 Gson
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ScoreCategory.class, new ScoreCategoryAdapter())
            .enableComplexMapKeySerialization()  // Map<ScoreCategory, Integer> 지원
            .setPrettyPrinting()
            .create();

    // 세이브 파일 존재 여부 확인
    public boolean hasSave() {
        File file = new File(PATH);
        return file.exists() && file.isFile();
    }

    // 현재 GameState를 json파일로 저장
    public void save(GameState state) {
        if (state == null) return;
        try (FileWriter writer = new FileWriter(PATH)) {
            gson.toJson(state, writer);
            System.out.println("저장 경로: " + PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // save.json파일에서 저장 정보를 로드 (없으면 null 반환)
    public GameState load() {
        File file = new File(PATH);
        if (!file.exists()) {
            System.out.println("파일을 찾을 수 없습니다.");
            return null;
        }
        try (FileReader reader = new FileReader(file)) {
            GameState state = gson.fromJson(reader, GameState.class);
            System.out.println("로딩 경로: " + PATH);
            return state;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
