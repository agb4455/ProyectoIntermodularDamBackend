package com.tfm.db_back.infrastructure.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * Documento de MongoDB que representa una instantánea (snapshot) del estado del juego.
 * Captura el estado completo de todos los jugadores en un momento determinado para analíticas.
 *
 * @author Adriana Cabaleiro Álvarez
 */
@Document(collection = "game_snapshots")
public class GameSnapshotDocument {

    @Id
    private String id;
    private String gameId;
    private Instant snapshotAt;
    private String phase;
    private List<PlayerSnapshot> players;

    public GameSnapshotDocument() {
    }

    public GameSnapshotDocument(String gameId, Instant snapshotAt, String phase, List<PlayerSnapshot> players) {
        this.gameId = gameId;
        this.snapshotAt = snapshotAt;
        this.phase = phase;
        this.players = players;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public Instant getSnapshotAt() {
        return snapshotAt;
    }

    public void setSnapshotAt(Instant snapshotAt) {
        this.snapshotAt = snapshotAt;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public List<PlayerSnapshot> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerSnapshot> players) {
        this.players = players;
    }

    public static class PlayerSnapshot {
        private String characterId;
        private String clanId;
        private int economicCredits;
        private int researchCredits;
        private int capitalHealth;
        private List<TroopSnapshot> troops;
        private List<String> unlockedResearches;
        private boolean eliminated;

        public PlayerSnapshot() {
        }

        public PlayerSnapshot(String characterId, String clanId, int economicCredits, int researchCredits,
                              int capitalHealth, List<TroopSnapshot> troops, List<String> unlockedResearches,
                              boolean eliminated) {
            this.characterId = characterId;
            this.clanId = clanId;
            this.economicCredits = economicCredits;
            this.researchCredits = researchCredits;
            this.capitalHealth = capitalHealth;
            this.troops = troops;
            this.unlockedResearches = unlockedResearches;
            this.eliminated = eliminated;
        }

        public String getCharacterId() {
            return characterId;
        }

        public void setCharacterId(String characterId) {
            this.characterId = characterId;
        }

        public String getClanId() {
            return clanId;
        }

        public void setClanId(String clanId) {
            this.clanId = clanId;
        }

        public int getEconomicCredits() {
            return economicCredits;
        }

        public void setEconomicCredits(int economicCredits) {
            this.economicCredits = economicCredits;
        }

        public int getResearchCredits() {
            return researchCredits;
        }

        public void setResearchCredits(int researchCredits) {
            this.researchCredits = researchCredits;
        }

        public int getCapitalHealth() {
            return capitalHealth;
        }

        public void setCapitalHealth(int capitalHealth) {
            this.capitalHealth = capitalHealth;
        }

        public List<TroopSnapshot> getTroops() {
            return troops;
        }

        public void setTroops(List<TroopSnapshot> troops) {
            this.troops = troops;
        }

        public List<String> getUnlockedResearches() {
            return unlockedResearches;
        }

        public void setUnlockedResearches(List<String> unlockedResearches) {
            this.unlockedResearches = unlockedResearches;
        }

        public boolean isEliminated() {
            return eliminated;
        }

        public void setEliminated(boolean eliminated) {
            this.eliminated = eliminated;
        }
    }

    public static class TroopSnapshot {
        private String troopId;
        private String typeId;
        private int currentPoints;
        private boolean deployed;

        public TroopSnapshot() {
        }

        public TroopSnapshot(String troopId, String typeId, int currentPoints, boolean deployed) {
            this.troopId = troopId;
            this.typeId = typeId;
            this.currentPoints = currentPoints;
            this.deployed = deployed;
        }

        public String getTroopId() {
            return troopId;
        }

        public void setTroopId(String troopId) {
            this.troopId = troopId;
        }

        public String getTypeId() {
            return typeId;
        }

        public void setTypeId(String typeId) {
            this.typeId = typeId;
        }

        public int getCurrentPoints() {
            return currentPoints;
        }

        public void setCurrentPoints(int currentPoints) {
            this.currentPoints = currentPoints;
        }

        public boolean isDeployed() {
            return deployed;
        }

        public void setDeployed(boolean deployed) {
            this.deployed = deployed;
        }
    }
}
