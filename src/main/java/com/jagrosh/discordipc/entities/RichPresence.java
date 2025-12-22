/*
 * Copyright 2017 John Grosh (john.a.grosh@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.discordipc.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.OffsetDateTime;

/**
 * An encapsulation of all data needed to properly construct a JSON RichPresence payload.
 *
 * <p>These can be built using {@link RichPresence.Builder}.
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class RichPresence
{
    private final String state;
    private final String details;
    private final OffsetDateTime startTimestamp;
    private final OffsetDateTime endTimestamp;
    private final String largeImageKey;
    private final String largeImageText;
    private final String smallImageKey;
    private final String smallImageText;
    private final String partyId;
    private final int partySize;
    private final int partyMax;
    private final String matchSecret;
    private final String joinSecret;
    private final String spectateSecret;
    private final boolean instance;
    private final String buttonLabel1;
    private final String buttonUrl1;
    private final String buttonLabel2;
    private final String buttonUrl2;
    
    public RichPresence(String state, String details, OffsetDateTime startTimestamp, OffsetDateTime endTimestamp, 
            String largeImageKey, String largeImageText, String smallImageKey, String smallImageText, 
            String partyId, int partySize, int partyMax, String matchSecret, String joinSecret, 
            String spectateSecret, boolean instance, String buttonLabel1, String buttonUrl1, String buttonLabel2, String buttonUrl2)
    {
        this.state = state;
        this.details = details;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.largeImageKey = largeImageKey;
        this.largeImageText = largeImageText;
        this.smallImageKey = smallImageKey;
        this.smallImageText = smallImageText;
        this.partyId = partyId;
        this.partySize = partySize;
        this.partyMax = partyMax;
        this.matchSecret = matchSecret;
        this.joinSecret = joinSecret;
        this.spectateSecret = spectateSecret;
        this.instance = instance;
        this.buttonLabel1 = buttonLabel1;
        this.buttonUrl1 = buttonUrl1;
        this.buttonLabel2 = buttonLabel2;
        this.buttonUrl2 = buttonUrl2;
    }

    /**
     * Constructs a {@link JsonObject} representing a payload to send to discord
     * to update a user's Rich Presence.
     *
     * <p>This is purely internal, and should not ever need to be called outside of
     * the library.
     *
     * @return A JSONObject payload for updating a user's Rich Presence.
     */
    public JsonObject toJson()
    {
        JsonObject payload = new JsonObject();
        payload.addProperty("state", state);
        payload.addProperty("details", details);

        JsonObject timeStamp = new JsonObject();
        if (startTimestamp != null)
            timeStamp.addProperty("start", startTimestamp.toEpochSecond());
        if (endTimestamp != null)
            timeStamp.addProperty("end", endTimestamp.toEpochSecond());
        payload.add("timestamps", timeStamp);

        JsonObject assets = new JsonObject();
        assets.addProperty("large_image", largeImageKey);
        assets.addProperty("large_text", largeImageText);
        assets.addProperty("small_image", smallImageKey);
        assets.addProperty("small_text", smallImageText);
        payload.add("assets", assets);

        if (partyId != null)
        {
            JsonArray size = new JsonArray();
            size.add(partySize);
            size.add(partyMax);

            JsonObject party = new JsonObject();
            party.addProperty("id", partyId);
            party.add("size", size);

            payload.add("party", party);
        }

        JsonObject secrets = new JsonObject();
        secrets.addProperty("join", joinSecret);
        secrets.addProperty("spectate", spectateSecret);
        secrets.addProperty("match", matchSecret);
        payload.add("secrets", secrets);
        payload.addProperty("instance", instance);

        JsonArray buttonsObj = new JsonArray();
        if (buttonLabel1 != null && buttonUrl1 != null) {
            JsonObject btn1 = new JsonObject();
            btn1.addProperty("label", buttonLabel1);
            btn1.addProperty("url", buttonUrl1);
            buttonsObj.add(btn1);
        }
        if (buttonLabel2 != null && buttonUrl2 != null) {
            JsonObject btn2 = new JsonObject();
            btn2.addProperty("label", buttonLabel2);
            btn2.addProperty("url", buttonUrl2);
            buttonsObj.add(btn2);
        }

        if (!buttonsObj.isEmpty()) {
            payload.add("buttons", buttonsObj);
        }

        return payload;
    }

    /**
     * A chain builder for a {@link RichPresence} object.
     *
     * <p>An accurate description of each field and it's functions can be found
     * <a href="https://discordapp.com/developers/docs/rich-presence/how-to#updating-presence-update-presence-payload-fields">here</a>
     */
    public static class Builder
    {
        private String state;
        private String details;
        private OffsetDateTime startTimestamp;
        private OffsetDateTime endTimestamp;
        private String largeImageKey;
        private String largeImageText;
        private String smallImageKey;
        private String smallImageText;
        private String partyId;
        private int partySize;
        private int partyMax;
        private String matchSecret;
        private String joinSecret;
        private String spectateSecret;
        private boolean instance;
        private String buttonLabel1;
        private String buttonUrl1;
        private String buttonLabel2;
        private String buttonUrl2;

        /**
         * Builds the {@link RichPresence} from the current state of this builder.
         *
         * @return The RichPresence built.
         */
        public RichPresence build()
        {
            return new RichPresence(state, details, startTimestamp, endTimestamp, 
                    largeImageKey, largeImageText, smallImageKey, smallImageText, 
                    partyId, partySize, partyMax, matchSecret, joinSecret, 
                    spectateSecret, instance, buttonLabel1, buttonUrl1, buttonLabel2, buttonUrl2);
        }

        /**
         * Sets the state of the user's current party.
         *
         * @param state The state of the user's current party.
         *
         * @return This Builder.
         */
        public Builder setState(String state)
        {
            this.state = state;
            return this;
        }

        /**
         * Sets details of what the player is currently doing.
         *
         * @param details The details of what the player is currently doing.
         *
         * @return This Builder.
         */
        public Builder setDetails(String details)
        {
            this.details = details;
            return this;
        }

        /**
         * Sets the time that the player started a match or activity.
         *
         * @param startTimestamp The time the player started a match or activity.
         *
         * @return This Builder.
         */
        public Builder setStartTimestamp(OffsetDateTime startTimestamp)
        {
            this.startTimestamp = startTimestamp;
            return this;
        }

        /**
         * Sets the time that the player's current activity will end.
         *
         * @param endTimestamp The time the player's activity will end.
         *
         * @return This Builder.
         */
        public Builder setEndTimestamp(OffsetDateTime endTimestamp)
        {
            this.endTimestamp = endTimestamp;
            return this;
        }

        /**
         * Sets the key of the uploaded image for the large profile artwork, as well as
         * the text tooltip shown when a cursor hovers over it.
         *
         * <p>These can be configured in the <a href="https://discordapp.com/developers/applications/me">applications</a>
         * page on the discord website.
         *
         * @param largeImageKey A key to an image to display.
         * @param largeImageText Text displayed when a cursor hovers over the large image.
         *
         * @return This Builder.
         */
        public Builder setLargeImage(String largeImageKey, String largeImageText)
        {
            this.largeImageKey = largeImageKey;
            this.largeImageText = largeImageText;
            return this;
        }

        /**
         * Sets the key of the uploaded image for the large profile artwork.
         *
         * <p>These can be configured in the <a href="https://discordapp.com/developers/applications/me">applications</a>
         * page on the discord website.
         *
         * @param largeImageKey A key to an image to display.
         *
         * @return This Builder.
         */
        public Builder setLargeImage(String largeImageKey)
        {
            return setLargeImage(largeImageKey, null);
        }

        /**
         * Sets the key of the uploaded image for the small profile artwork, as well as
         * the text tooltip shown when a cursor hovers over it.
         *
         * <p>These can be configured in the <a href="https://discordapp.com/developers/applications/me">applications</a>
         * page on the discord website.
         *
         * @param smallImageKey A key to an image to display.
         * @param smallImageText Text displayed when a cursor hovers over the small image.
         *
         * @return This Builder.
         */
        public Builder setSmallImage(String smallImageKey, String smallImageText)
        {
            this.smallImageKey = smallImageKey;
            this.smallImageText = smallImageText;
            return this;
        }

        /**
         * Sets the key of the uploaded image for the small profile artwork.
         *
         * <p>These can be configured in the <a href="https://discordapp.com/developers/applications/me">applications</a>
         * page on the discord website.
         *
         * @param smallImageKey A key to an image to display.
         *
         * @return This Builder.
         */
        public Builder setSmallImage(String smallImageKey)
        {
            return setSmallImage(smallImageKey, null);
        }

        /**
         * Sets party configurations for a team, lobby, or other form of group.
         *
         * <p>The {@code partyId} is ID of the player's party.
         * <br>The {@code partySize} is the current size of the player's party.
         * <br>The {@code partyMax} is the maximum number of player's allowed in the party.
         *
         * @param partyId The ID of the player's party.
         * @param partySize The current size of the player's party.
         * @param partyMax The maximum number of player's allowed in the party.
         *
         * @return This Builder.
         */
        public Builder setParty(String partyId, int partySize, int partyMax)
        {
            this.partyId = partyId;
            this.partySize = partySize;
            this.partyMax = partyMax;
            return this;
        }

        /**
         * Sets the unique hashed string for Spectate and Join.
         *
         * @param matchSecret The unique hashed string for Spectate and Join.
         *
         * @return This Builder.
         */
        public Builder setMatchSecret(String matchSecret)
        {
            this.matchSecret = matchSecret;
            return this;
        }

        /**
         * Sets the unique hashed string for chat invitations and Ask to Join.
         *
         * @param joinSecret The unique hashed string for chat invitations and Ask to Join.
         *
         * @return This Builder.
         */
        public Builder setJoinSecret(String joinSecret)
        {
            this.joinSecret = joinSecret;
            return this;
        }

        /**
         * Sets the unique hashed string for Spectate button.
         *
         * @param spectateSecret The unique hashed string for Spectate button.
         *
         * @return This Builder.
         */
        public Builder setSpectateSecret(String spectateSecret)
        {
            this.spectateSecret = spectateSecret;
            return this;
        }

        /**
         * Sets the first button's label and URL.
         *
         * @param label The button's label.
         * @param url The button's URL.
         *
         * @return This Builder.
         */
        public Builder setButton1(String label, String url) {
            // If the label is greater than 32 characters, it will be truncated.
            if (label.length() > 32) {
                // This is a failsafe
                label = label.substring(0, 32);
            }

            this.buttonLabel1 = label;
            this.buttonUrl1 = url;
            return this;
        }

        /**
         * Sets the second button's label and URL.
         *
         * @param label The button's label.
         * @param url The button's URL.
         *
         * @return This Builder.
         */
        public Builder setButton2(String label, String url) {
            // If the label is greater than 32 characters, it will be truncated.
            if (label.length() > 32) {
                // This is a failsafe
                label = label.substring(0, 32);
            }

            this.buttonLabel2 = label;
            this.buttonUrl2 = url;
            return this;
        }

        /**
         * Marks the {@link #setMatchSecret(String) matchSecret} as a game
         * session with a specific beginning and end.
         *
         * @param instance Whether the {@code matchSecret} is a game
         *                 with a specific beginning and end.
         *
         * @return This Builder.
         */
        public Builder setInstance(boolean instance)
        {
            this.instance = instance;
            return this;
        }
    }
}
