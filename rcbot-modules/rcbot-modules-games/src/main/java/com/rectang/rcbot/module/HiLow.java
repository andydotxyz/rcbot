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

import org.headsupdev.irc.IRCConnection;

import java.util.Hashtable;

public class HiLow extends Game {

  private Hashtable games;
  private GameModule mod;
  
  public HiLow(GameModule mod) {
    super(mod);
    games = new Hashtable();
    this.mod = mod;
  }

  public void newGame(String channel, String sender, IRCConnection conn) {
    Integer game = (Integer) games.get(channel);
    if (game != null) {
      conn.sendMessage(channel, "There is a game in progress");
      return;
    }
    games.put(channel, new Integer((int) (Math.random() * 99.0) + 1));
    conn.sendMessage(channel, "I am thinking of a number between 1 and 100");
  }

  public void guess(String channel, String sender, String message, IRCConnection conn) {
    Integer game = (Integer) games.get(channel);
    if (game == null) {
      conn.sendMessage(channel, "There is no game in progress");
      return;
    }
    try {
      int guess = Integer.parseInt(message);
      if (game.intValue() > guess) {
        conn.sendMessage(channel, "Try higher, " + sender);
      } else if (game.intValue() < guess) {
        conn.sendMessage(channel, "Try lower, " + sender);
      } else {
        games.remove(channel);
        int score = getScores().incrementScore(sender, 10);
        conn.sendMessage(channel, "Well done, " + sender + " (" + score + ")");
      }
    } catch (NumberFormatException e) {
      conn.sendMessage(channel, "Invalid guess, " + sender + " - use a whole number");
    }
  }

  public void answer(String channel, String sender, String message, IRCConnection conn) {
    Integer game = (Integer) games.get(channel);
    try {
      int guess = Integer.parseInt(message);
      if (game.intValue() != guess) {
        int score = getScores().decrementScore(sender, 5);
        conn.sendMessage(channel, "Wrong guess, " + sender + " (" + score + ")");
      } else {
        games.remove(channel);
        int score = getScores().incrementScore(sender, 20);
        conn.sendMessage(channel, "Well done, " + sender + " (" + score + ")");
      }
    } catch (NumberFormatException e) {
      conn.sendMessage(channel, "Invalid guess, " + sender + " - use a whole number");
    }
  }

  public String getHelp(String parameters) {
    return "HiLow s a simple game of guessing numbers";
  }
}
