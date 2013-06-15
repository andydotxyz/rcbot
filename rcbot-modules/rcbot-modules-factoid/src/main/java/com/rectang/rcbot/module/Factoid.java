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

import com.rectang.rcbot.*;

import java.util.*;

import org.headsupdev.irc.IRCConnection;
import org.headsupdev.irc.IRCServiceManager;
import org.headsupdev.irc.IRCUser;

/**
 * A module for managing small facts etc
 *
 */
public class Factoid extends RCBotCommand {

  public String getId() {
    return "factoid";
  }

  public String getTitle() {
    return "A module for storing random information";
  }

  private List commands;

  public Factoid(RCBot bot, IRCServiceManager manager) {
    super(bot, manager);

    commands = new Vector();
    commands.add("forget");
  }

  public Collection getCommands() {
    return commands;
  }

  public void onSubCommand(String command, String channel, IRCUser user,
                           String message, IRCConnection conn) {
    if (command.equals("forget")) {
      if (!bot.isOwner(user)) {
        conn.sendMessage(channel, "Sorry, only my owner can remove facts");
        return;
      }

      String key = message.trim();
      String ret1 = getIsStore().remove(key);
      String ret2 = getAreStore().remove(key);
      if ((ret1 == null || ret1.equals("")) && (ret2 == null || ret2.equals("")))
        conn.sendMessage(channel, "I know nothing of this " + key + " you speak of");
      else
        conn.sendMessage(channel, "forgotten " + key);
    }
  }

  private Storage getIsStore() {
    return StorageImpl.getInstance("factoid-is", bot);
  }

  private Storage getAreStore() {
    return StorageImpl.getInstance("factoid-are", bot);
  }

  public String getHelp(String channel) {
    return "Factoid is a module to learn simple things from open talk.\n" +
        "say a statement containing \"is\" or \"are\" to store\n" +
        "and say a phrase ending in \"?\" to retrieve";
  }

  public void onMissingCommand(String command, String channel, IRCUser user, IRCConnection conn) {
    conn.sendMessage(channel, "Factoid module does not understand command \"" +
        command + "\"\nTry help");
  }
}
