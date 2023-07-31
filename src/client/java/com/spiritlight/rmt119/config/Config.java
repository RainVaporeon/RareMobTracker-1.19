package com.spiritlight.rmt119.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Config extends ConfigSerializable {

    @JsonProperty
    private final List<String> ignoredNames = new LinkedList<>();

    @JsonProperty
    private final List<String> seekNames = new LinkedList<>();

    @JsonProperty
    private boolean seekPlayer = false;

    @JsonProperty
    private boolean modEnabled = true;

    public Config() {}

    public List<String> getIgnoredNames() {
        return ignoredNames;
    }

    public List<String> getSeekNames() {
        return seekNames;
    }

    public boolean doSeekPlayer() {
        return seekPlayer;
    }

    public void setSeekPlayer(boolean seekPlayer) {
        this.seekPlayer = seekPlayer;
    }

    public boolean isModEnabled() {
        return modEnabled;
    }

    public void setModEnabled(boolean modEnabled) {
        this.modEnabled = modEnabled;
    }

    public boolean toggleModEnabled() {
        this.modEnabled = !this.modEnabled;
        return modEnabled;
    }

    public boolean toggleSeekPlayer() {
        this.seekPlayer = !this.seekPlayer;
        return this.seekPlayer;
    }

    @Override
    File file() {
        return new File("config/rmt-1.19.json");
    }
}
