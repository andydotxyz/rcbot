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
import org.headsupdev.irc.IRCServiceManager;
import org.headsupdev.irc.IRCUser;

import java.util.*;

/**
 * A module for keeping track of people's/thing's karma
 *
 */
public class Karma extends RCBotCommand {

  public Karma(RCBot bot, IRCServiceManager manager) {
    super(bot, manager);
  }

  public String getId() {
    return "karma";
  }

  public String getTitle() {
    return "A module to keep track of users karma";
  }

  public Collection getCommands() {
    return NO_COMMANDS;
  }

  public void onSubCommand(String command, String channel, IRCUser user,
                           String message, IRCConnection conn) {
    try {
      int karma = getStore(bot).getInt(message);
      if (karma == 0)
        conn.sendMessage(channel, message + " has neutral karma");
      else
        conn.sendMessage(channel, message + " has a karma of " + karma);
    } catch (NumberFormatException e) {
      conn.sendMessage(channel, "No karma information for " + message);
    }
  }

  public String getHelp(String channel) {
    return "Karma is a module for tracking how favourable a subject is\n" +
        "use the ++ or -- commands at the end of any phrase to change it's karma\n" +
        "and use the karma command, followed by the phrase to query";
  }

  public void onMissingCommand( String command, String channel, IRCUser user, IRCConnection conn ) {
    /* cannot be called, we accept NO_COMMANDS */
  }
}
