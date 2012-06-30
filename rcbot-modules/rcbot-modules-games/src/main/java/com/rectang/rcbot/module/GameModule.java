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

import java.util.Collection;
import java.util.Hashtable;

import org.headsupdev.irc.IRCConnection;
import org.headsupdev.irc.IRCUser;
import org.headsupdev.irc.IRCServiceManager;

/**
 * A module for playing games
 *
 * @plexus.component
 *   role="org.headsupdev.irc.IRCommand"
 *   role-hint="games"
 */
public class GameModule extends ModuleImpl {

  /**
   * @plexus.requirement
   *   role="org.headsupdev.irc.IRCServiceManager
   */
  private IRCServiceManager manager;

  /**
   * @plexus.requirement
   *   role="com.rectang.rcbot.RCBot"
   */
  private RCBot bot;

  /**
   * @plexus.requirement
   */
  private com.rectang.rcbot.Score scores;

  private Hashtable games;

  public GameModule() {
    games = new Hashtable();
    games.put("hilow", new HiLow(this));
  }

  public String getId() {
    return "games";
  }

  public String getTitle() {
    return "A collection of games to pass the time";
  }

  public Collection getCommands() {
    return games.keySet();
  }

  protected Score getScores() {
    return scores;
  }

  public void onSubCommand(String command, String channel, IRCUser user,
                           String message, IRCConnection conn) {
    if (!games.containsKey(command)) {
      conn.sendMessage(channel, "Game " + command + " not recognised");
      return;
    }

    String comd2 = StringUtils.getFirstArg(message).toLowerCase();
    String theMessage = message.substring(comd2.length()).trim();

    Game game = (Game) games.get(command);
    if (comd2.equals("new"))
      game.newGame(channel, user.getNick(), conn);
    else if (comd2.equals("guess"))
      game.guess(channel, user.getNick(), theMessage, conn);
    else if (comd2.equals("answer"))
      game.answer(channel, user.getNick(), theMessage, conn);
    else if (comd2.equals("help"))
      conn.sendMessage(channel, game.getHelp(theMessage));
    else {
      if (comd2.equals(""))
        conn.sendMessage(channel, "Games require an extra parameter, see \"help games\"");
      else
        conn.sendMessage(channel, "Game command " + comd2 + " not recognised");
    }
  }

  public void onPrivateSubCommand(String command, IRCUser user,
                                  String message, IRCConnection conn) {
    conn.sendMessage(user.getNick(), "Games can only be played in public");
  }

  public String getHelp(String parameters) {
    return "Some nice games - try the \"hilow\" command with\n" +
        "parameters \"new\" \"guess <num>\" or \"answer <num>\"";
  }

  public void onMissingCommand(String command, String channel, IRCUser user, IRCConnection conn) {
    conn.sendMessage(channel, "Game \"" + command +
        "\" could not be found");
  }
}
