package com.spiritlight.rmt119.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spiritlight.fishutils.action.ActionResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class Configuration {

    abstract File file();

    public ActionResult<Void> serialize() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file(), this);
            return ActionResult.success();
        } catch (IOException ex) {
            return ActionResult.fail(ex);
        }
    }

    public ActionResult<Void> deserialize() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.readerForUpdating(this).readValue(file());
            return ActionResult.success();
        } catch (IOException ex) {
            if(ex instanceof FileNotFoundException) {
                ActionResult.tryAction(() -> file().createNewFile());
            }
            return ActionResult.fail(ex);
        }
    }
}
