/*
 * This file is part of ViaVersion - https://github.com/ViaVersion/ViaVersion
 * Copyright (C) 2016-2021 ViaVersion and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.viaversion.viaversion.api.platform;

import com.google.gson.JsonObject;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.configuration.ConfigurationProvider;
import com.viaversion.viaversion.api.configuration.ViaVersionConfig;
import com.viaversion.viaversion.api.connection.UserConnection;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * ViaPlatform represents a platform ViaVersion runs on
 *
 * @param <T> - The player type for the platform, used for API related methods
 */
public interface ViaPlatform<T> {

    /**
     * Get the logger for this platform
     *
     * @return Java Logger (may be a wrapper)
     */
    Logger getLogger();

    /**
     * Get the platform name
     *
     * @return Platform Name (simply its name)
     */
    String getPlatformName();

    /**
     * Get the platform version
     *
     * @return Platform version
     */
    String getPlatformVersion();

    /**
     * Returns true if the server Via is running on is a proxy server.
     *
     * @return true if the platform is a proxy
     */
    default boolean isProxy() {
        return false;
    }

    /**
     * Get the plugin version
     *
     * @return Plugin version as a semver string
     */
    String getPluginVersion();

    /**
     * Run a task Async
     *
     * @param runnable The task to run
     * @return The Task ID
     */
    PlatformTask runAsync(Runnable runnable);

    /**
     * Run a task Sync
     *
     * @param runnable The task to run
     * @return The Task ID
     */
    PlatformTask runSync(Runnable runnable);

    /**
     * Run a task Sync after a interval
     * This must be only used after plugin enable.
     *
     * @param runnable The task to run
     * @param ticks    The interval to run it after
     * @return The Task ID
     */
    PlatformTask runSync(Runnable runnable, Long ticks);

    /**
     * Run a task at a repeating interval.
     * Initial interval is the same as repeat.
     *
     * @param runnable The task to run
     * @param ticks    The interval to run it at
     * @return The Task ID
     */
    PlatformTask runRepeatingSync(Runnable runnable, Long ticks);

    /**
     * Cancels a task.
     *
     * @param task task to cancel
     */
    default void cancelTask(PlatformTask task) {
        if (task.getObject() != null) {
            task.cancel();
        }
    }

    /**
     * Get the online players
     *
     * @return Array of ViaCommandSender
     */
    ViaCommandSender[] getOnlinePlayers();

    /**
     * Send a message to a player
     *
     * @param uuid    The player's UUID
     * @param message The message to send
     */
    void sendMessage(UUID uuid, String message);

    /**
     * Kick a player for a reason
     *
     * @param uuid    The player's UUID
     * @param message The message to kick them with
     * @return True if it was successful
     */
    boolean kickPlayer(UUID uuid, String message);

    /**
     * Disconnects an UserConnection for a reason
     *
     * @param connection The UserConnection
     * @param message    The message to kick them with
     * @return True if it was successful
     */
    default boolean disconnect(UserConnection connection, String message) {
        if (connection.isClientSide()) return false;
        UUID uuid = connection.getProtocolInfo().getUuid();
        if (uuid == null) return false;
        return kickPlayer(uuid, message);
    }

    /**
     * Check if the plugin is enabled.
     *
     * @return True if it is enabled
     */
    boolean isPluginEnabled();

    /**
     * Get the API for this platform
     *
     * @return The API for the platform
     */
    ViaAPI<T> getApi();

    /**
     * Get the config API for this platform
     *
     * @return The config API
     */
    ViaVersionConfig getConf();

    /**
     * Get the backend configuration provider for this platform.
     * (On some platforms this returns the same as getConf())
     *
     * @return The configuration provider
     */
    ConfigurationProvider getConfigurationProvider();

    /**
     * Get ViaVersions's data folder.
     *
     * @return data folder
     */
    File getDataFolder();

    /**
     * Called when a reload happens
     */
    void onReload();

    /**
     * Get the JSON data required for /viaversion dump
     *
     * @return The json data
     */
    JsonObject getDump();

    /**
     * Get if older clients are allowed to be used using ViaVersion.
     * (Only 1.9 on 1.9.2 server is supported by ViaVersion alone)
     *
     * @return True if allowed
     */
    boolean isOldClientsAllowed();

    /**
     * Returns an immutable collection of classes to be checked as unsupported software with their software name.
     * If any of the classes exist at runtime, a warning about their potential instability will be given to the console.
     *
     * @return immutable collection of unsupported software to be checked
     */
    default Collection<UnsupportedSoftware> getUnsupportedSoftwareClasses() {
        return Collections.emptyList();
    }
}