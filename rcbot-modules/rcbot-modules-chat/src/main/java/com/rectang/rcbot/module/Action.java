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

import java.util.Collection;

import com.rectang.rcbot.StringUtils;

/**
 * A module for general bot responses
 *
 */
public class Action extends RCBotCommand {

  public Action(RCBot bot, IRCServiceManager manager) {
    super(bot, manager);
  }

  public String getId() {
    return "action";
  }

  public String getTitle() {
    return "Make the bot issue actions";
  }

  public Collection getCommands() {
    return NO_COMMANDS;
  }

  public void onSubCommand(String command, String channel, IRCUser user,
                           String message, IRCConnection conn) {
    conn.sendAction(channel, message);
  }

  public void onPrivateSubCommand(String command, IRCUser user,
                                  String message, IRCConnection conn) {
    String toChannel = StringUtils.getFirstArg(message).trim();
    if (!StringUtils.isChannel(toChannel)) {
      conn.sendMessage(user.getNick(), "Invalid channel \"" + toChannel + "\"");
      return;
    }

    conn.sendAction(toChannel, message.substring(toChannel.length()).trim());
  }

  public void onMissingCommand(String command, String channel, IRCUser user, IRCConnection conn) {
    /* NO_COMMANDS so cannot use this */
  }
}
