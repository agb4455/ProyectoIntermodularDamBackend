package com.tfm.db_back.infrastructure.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * Documento de MongoDB que representa un evento de batalla.
 * Almacena detalles de los enfrentamientos entre clanes para análisis histórico.
 *
 * @author Adriana Cabaleiro Álvarez
 */
@Document(collection = "battle_events")
public class BattleEventDocument {

    @Id
    private String id;
    
    private String gameId;
    private Instant timestamp;
    private String attackerCharacterId;
    private String attackerClanId;
    private String defenderCharacterId;
    private String defenderClanId;
    private int attackerTotalPoints;
    private int defenderTotalPoints;
    private String outcome;
    private boolean advantageApplied;
    private double advantageMultiplier;
    private List<String> attackerTroopsLost;
    private List<String> defenderTroopsLost;

    public BattleEventDocument() {
    }

    public BattleEventDocument(String gameId, Instant timestamp, String attackerCharacterId, String attackerClanId,
                               String defenderCharacterId, String defenderClanId, int attackerTotalPoints,
                               int defenderTotalPoints, String outcome, boolean advantageApplied,
                               double advantageMultiplier, List<String> attackerTroopsLost, List<String> defenderTroopsLost) {
        this.gameId = gameId;
        this.timestamp = timestamp;
        this.attackerCharacterId = attackerCharacterId;
        this.attackerClanId = attackerClanId;
        this.defenderCharacterId = defenderCharacterId;
        this.defenderClanId = defenderClanId;
        this.attackerTotalPoints = attackerTotalPoints;
        this.defenderTotalPoints = defenderTotalPoints;
        this.outcome = outcome;
        this.advantageApplied = advantageApplied;
        this.advantageMultiplier = advantageMultiplier;
        this.attackerTroopsLost = attackerTroopsLost;
        this.defenderTroopsLost = defenderTroopsLost;
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

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getAttackerCharacterId() {
        return attackerCharacterId;
    }

    public void setAttackerCharacterId(String attackerCharacterId) {
        this.attackerCharacterId = attackerCharacterId;
    }

    public String getAttackerClanId() {
        return attackerClanId;
    }

    public void setAttackerClanId(String attackerClanId) {
        this.attackerClanId = attackerClanId;
    }

    public String getDefenderCharacterId() {
        return defenderCharacterId;
    }

    public void setDefenderCharacterId(String defenderCharacterId) {
        this.defenderCharacterId = defenderCharacterId;
    }

    public String getDefenderClanId() {
        return defenderClanId;
    }

    public void setDefenderClanId(String defenderClanId) {
        this.defenderClanId = defenderClanId;
    }

    public int getAttackerTotalPoints() {
        return attackerTotalPoints;
    }

    public void setAttackerTotalPoints(int attackerTotalPoints) {
        this.attackerTotalPoints = attackerTotalPoints;
    }

    public int getDefenderTotalPoints() {
        return defenderTotalPoints;
    }

    public void setDefenderTotalPoints(int defenderTotalPoints) {
        this.defenderTotalPoints = defenderTotalPoints;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public boolean isAdvantageApplied() {
        return advantageApplied;
    }

    public void setAdvantageApplied(boolean advantageApplied) {
        this.advantageApplied = advantageApplied;
    }

    public double getAdvantageMultiplier() {
        return advantageMultiplier;
    }

    public void setAdvantageMultiplier(double advantageMultiplier) {
        this.advantageMultiplier = advantageMultiplier;
    }

    public List<String> getAttackerTroopsLost() {
        return attackerTroopsLost;
    }

    public void setAttackerTroopsLost(List<String> attackerTroopsLost) {
        this.attackerTroopsLost = attackerTroopsLost;
    }

    public List<String> getDefenderTroopsLost() {
        return defenderTroopsLost;
    }

    public void setDefenderTroopsLost(List<String> defenderTroopsLost) {
        this.defenderTroopsLost = defenderTroopsLost;
    }
}
