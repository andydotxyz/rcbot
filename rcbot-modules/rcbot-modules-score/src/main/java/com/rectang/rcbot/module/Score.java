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

/**
 * A score table for maintaining track of game results etc
 *
 * @plexus.component
 *   role="com.rectang.rcbot.Score"
 */
public class Score implements com.rectang.rcbot.Score {

  /**
   * @plexus.requirement
   */
  private com.rectang.rcbot.RCBot bot;

  public Storage getStore() {
    return StorageImpl.getInstance("score", bot);
  }

  public int getScore(String nick) {
    return getStore().getInt(nick);
  }

  public int incrementScore(String nick) {
    incrementScore(nick, 1);
    return getScore(nick);
  }

  public int incrementScore(String nick, int by) {
    setScore(nick, getScore(nick) + by);
    return getScore(nick);
  }

  public int decrementScore(String nick) {
    decrementScore(nick, 1);
    return getScore(nick);
  }

  public int decrementScore(String nick, int by) {
    setScore(nick, getScore(nick) - by);
    return getScore(nick);
  }

  public int setScore(String nick, int score) {
    return getStore().setInt(nick, score);
  }
}
