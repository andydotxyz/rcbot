/*
 * Copyright 2006-2012 Andrew Williams.
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
 * A module for general bot responses
 *
 * @plexus.component
 *   role="org.headsupdev.irc.IRCCommand"
 *   role-hint="say"
 */
public class Chat extends ModuleImpl {

  /**
   * @plexus.requirement
   *   role="org.headsupdev.irc.IRCServiceManager"
   */
  private IRCServiceManager manager;

  public String getId() {
    return "say";
  }

  public String getTitle() {
    return "Make the bot talk to things";
  }

  public Collection getCommands() {
    return NO_COMMANDS;
  }

  public void onSubCommand(String command, String channel, IRCUser user,
                            String message, IRCConnection conn) {
    conn.sendMessage(channel, message);
  }

  public void onPrivateSubCommand(String command, IRCUser user, String message, IRCConnection conn) {
    String toChannel = StringUtils.getFirstArg(message).trim();
    if (!StringUtils.isChannel(toChannel)) {
      conn.sendMessage(user.getNick(), "Invalid channel \"" + toChannel + "\"");
      return;
    }

    conn.sendMessage(toChannel, message.substring(toChannel.length()).trim());
  }

  public void onMissingCommand(String command, String channel, IRCUser user, IRCConnection conn) {
    /* NO_COMMANDS so cannot use this */
  }
}
