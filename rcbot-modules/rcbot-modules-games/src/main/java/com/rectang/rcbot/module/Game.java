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

import com.rectang.rcbot.Score;
import org.headsupdev.irc.IRCConnection;

public abstract class Game {

  private GameModule mod;

  public Game(GameModule mod) {
    this.mod = mod;
  }

  protected Score getScores() {
    return mod.getScores();
  }

  public abstract void newGame(String channel, String sender, IRCConnection conn);

  public abstract void guess(String channel, String sender, String message, IRCConnection conn);

  public abstract void answer(String channel, String sender, String message, IRCConnection conn);

  public abstract String getHelp(String parameters);
}
