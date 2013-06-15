/*
 * Copyright 2006-2013 Andrew Williams.
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

package com.rectang.rcbot.module;

import com.rectang.rcbot.RCBot;
import org.headsupdev.irc.IRCConnection;
import org.headsupdev.irc.IRCUser;
import org.headsupdev.irc.IRCServiceManager;

import java.util.*;

/**
 * A module for accessing score features
 *
 */
public class ScoreModule extends RCBotCommand {

  private com.rectang.rcbot.Score scores;

  public ScoreModule(RCBot bot, IRCServiceManager manager) {
    super(bot, manager);

    scores = Score.createInstance();
  }

  public String getId() {
    return "score";
  }

  public String getTitle() {
    return "A central system for tracking users scores";
  }

  public Collection getCommands() {
    return NO_COMMANDS;
  }

  public void onSubCommand(String command, String channel, IRCUser user,
                           String message, IRCConnection conn) {
    try {
      int score = scores.getScore(message);
      conn.sendMessage(channel, message + " has a score of " + score);
    } catch (NumberFormatException e) {
      conn.sendMessage(channel, "No score information for " + message);
    }
  }

  public String getHelp(String parameters) {
    return "Score is a simple module to keep track of peoples global game score";
  }

  public void onMissingCommand(String command, String channel, IRCUser user, IRCConnection conn) {
    /* NO_COMMANDS so cannot use this */
  }
}
