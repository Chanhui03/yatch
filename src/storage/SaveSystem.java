package storage;

import game.GameState;

public class SaveSystem {

    private FileIOManager io = new FileIOManager();

    public void save(GameState state) {
        io.save(GameSaveData.fromState(state));
    }

    public GameState load() {
        GameSaveData data = io.load();
        if (data == null) return null;
        return new GameState(data);
    }
}
