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
import org.headsupdev.irc.IRCUser;
import org.headsupdev.irc.IRCServiceManager;

/**
 * A module used to leave messages for people offline
 *
 * @plexus.component
 *   role="org.headsupdev.irc.IRCCommand"
 *   role-hint="tell"
 */
public class Tell extends ModuleImpl {

  /**
   * @plexus.requirement
   *   role="org.headsupdev.irc.IRCServiceManager"
   */
  private IRCServiceManager manager;

  /**
   * @plexus.requirement
   */
  private com.rectang.rcbot.RCBot bot;

  public String getId() {
    return "tell";
  }

  public String getTitle() {
    return "Store memos for other users that the bot will pass on";
  }

  public Collection getCommands() {
    return NO_COMMANDS;
  }

  public void onSubCommand(String command, String channel, IRCUser user,
                           String message, IRCConnection conn) {
    String toTell = StringUtils.getFirstArg(message).trim();
    String tellMessage =
        message.substring(StringUtils.getFirstArg(message).length()).trim();

    if (message.trim().length() == 0 || toTell.length() == 0 ||
        tellMessage.length() == 0)
      return;

    storeTell(channel, user.getNick(), toTell, channel, tellMessage, conn);
  }

  public void onPrivateSubCommand(String command, IRCUser user,
                                  String message, IRCConnection conn) {
    String channelTell = StringUtils.getFirstArg(message).trim();

    String tellMessage =
        message.substring(StringUtils.getFirstArg(message).length()).trim();

    String toTell = StringUtils.getFirstArg(message).trim();
    tellMessage = message.substring(StringUtils.getFirstArg(tellMessage).
        length()).trim();

    if (message.trim().length() == 0 || channelTell.length() == 0 ||
        toTell.length() == 0 || tellMessage.length() == 0)
      return;

    storeTell(user.getNick(), user.getNick(), toTell, channelTell, tellMessage, conn);
  }

  private void storeTell(String channel, String sender, String toTell,
                         String tellChannel, String tellMessage, IRCConnection conn) {
    getStore(bot).appendStringList(toTell + ":" + tellChannel,
        sender + " said: " + tellMessage);
    conn.sendMessage(channel, sender + ": I will tell " + toTell + " asap");
  }

  public String getHelp(String parameters) {
    return "Tell is a module to leave message for people on your channel.";
  }

  public void onMissingCommand(String command, String channel, IRCUser user, IRCConnection conn) {
    /* NO_COMMANDS so cannot use this */
  }
}
