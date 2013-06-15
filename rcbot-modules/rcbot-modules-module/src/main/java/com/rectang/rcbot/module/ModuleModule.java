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

import org.headsupdev.irc.IRCServiceManager;
import org.headsupdev.irc.IRCUser;

/**
 * A module for controlling modules
 *
 */
public class ModuleModule extends ModuleImpl {

  private Vector commands;

  public Action(RCBot bot, IRCServiceManager manager) {
    super(bot, manager);

    commands = new Vector();
    commands.add("load");
    commands.add("unload");
    commands.add("reload");
  }

  public String getName() {
    return "module";
  }

  public String getTitle() {
    return "Module management commands";
  }

  public Collection getCommands() {
    return commands;
  }

  public void onSubCommand(String command, String channel, IRCUser user,
                           String message) {
    if (!bot.isOwner(user)) {
      manager.sendMessage(channel, "Sorry, " + user.getNick() +
          " only my owner can use module commands");
      return;
    }

    String arg1 = StringUtils.getFirstArg(message);
    manager.sendMessage(channel,
        "This module is not working, there is no module control in this runtime");
/*    if (command.equals("load")) {
      if (RCBotImpl.getInstance().getDPMR().loadModule(arg1))
        sendMessage(channel, "Loaded module " + arg1 + " sucessfully");
      else
        sendMessage(channel, "Could not load module " + arg1);
    } else if (command.equals("unload")) {
      if (RCBotImpl.getInstance().getDPMR().unloadModule(arg1))
        sendMessage(channel, "Unloaded module " + arg1 + " sucessfully");
      else
        sendMessage(channel, "Could not unload module " + arg1);
    } else if (command.equals("reload")) {
      if (RCBotImpl.getInstance().getDPMR().reloadModule(arg1))
        sendMessage(channel, "Reloaded module " + arg1 + " sucessfully");
      else
        sendMessage(channel, "Could not reload module " + arg1);
    }*/
  }

  public String getHelp(String channel) {
    return "Here you can load, unload and reload RCBot modules";
  }

  public void onMissingCommand(String command, String channel, IRCUser user) {
    manager.sendMessage(channel, "Module module does not understand command \"" +
        command + "\"\nTry help");
  }
}
