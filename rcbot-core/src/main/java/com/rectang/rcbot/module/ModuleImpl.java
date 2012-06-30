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

import org.headsupdev.irc.IRCCommand;
import org.headsupdev.irc.AbstractIRCListener;
import org.headsupdev.irc.IRCConnection;
import org.headsupdev.irc.IRCUser;

public abstract class ModuleImpl extends AbstractIRCListener implements IRCCommand {

  public static final String EMPTY_COMMAND = "";
  public static final Collection NO_COMMANDS;

  static {
    NO_COMMANDS = new Vector();
    NO_COMMANDS.add(EMPTY_COMMAND);
  }

  public abstract String getTitle();

/*
  public void sendMessage(String channel, String message) {
    if (message == null || message.length() == 0)
      return;

    if (message.indexOf('\n') == -1)
      RCBotImpl.getInstance().sendMessage(channel, message);
    else {
      String[] lines = message.split("\n");
      for (int i = 0; i < lines.length; i++) {
        RCBotImpl.getInstance().sendMessage(channel, lines[i]);
      }
    }
  }
*/

  protected final StorageImpl getStore(RCBot bot) {
    return StorageImpl.getInstance(getId(), bot);
  }

  protected String getConfigKey(String key) {
    return ConfigImpl.getModuleConfigKey(getId(), key);
  }

  public abstract Collection getCommands();

  public final void onPrivateCommand(IRCUser ircUser, String message, IRCConnection conn) {
    doSubCommand(ircUser.getNick(), ircUser, message, true, conn);
  }

  public final void onCommand(String channel, IRCUser ircUser,
                              String message, IRCConnection conn) {
    doSubCommand(channel, ircUser, message, false, conn );
  }

  private void doSubCommand(String channel, IRCUser ircUser, String message,
                             boolean priv, IRCConnection conn) {
    if (message == null || message.length() == 0) {
      if (getCommands().equals(NO_COMMANDS) ||
          getCommands().contains(EMPTY_COMMAND)) {
        if (priv)
          onPrivateSubCommand(EMPTY_COMMAND, ircUser, message, conn);
        else
          onSubCommand(EMPTY_COMMAND, channel, ircUser, message, conn);
        return;
      } else {
        onMissingCommand(EMPTY_COMMAND, channel, ircUser, conn);
        return;
      }
    }

    String command = StringUtils.getFirstArg(message);
    String subMessage = message.substring(command.length()).trim();
    if (getCommands().contains(command))
      if (priv)
        onPrivateSubCommand(command, ircUser, subMessage, conn);
      else
        onSubCommand(command, channel, ircUser, subMessage, conn);
    else if (getCommands().contains(EMPTY_COMMAND))
      if (priv)
        onPrivateSubCommand(EMPTY_COMMAND, ircUser, message, conn);
      else
        onSubCommand(EMPTY_COMMAND, channel, ircUser, message, conn);
    else
      onMissingCommand(command, channel, ircUser, conn);
  }

  public String getHelp(String parameters) {
    return "No help available for command \"" + getId() + "\"";
  }

  public abstract void onSubCommand(String command, String channel,
                                     IRCUser user, String message, IRCConnection conn);

  public void onPrivateSubCommand(String command, IRCUser user,
                                   String message, IRCConnection conn) {
    onSubCommand(command, user.getNick(), user, message, conn);
  }

  public abstract void onMissingCommand(String command, String channel,
                                         IRCUser ircUser, IRCConnection conn);
}
