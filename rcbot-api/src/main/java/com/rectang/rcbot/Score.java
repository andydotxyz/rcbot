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

package com.rectang.rcbot;

public abstract class Score {

  private static Score instance;

  public static Score getInstance() {
    return instance;
  }

  protected static void setInstance(Score score) {
    instance = score;
  }

  public abstract int getScore(String nick);

  public abstract int incrementScore(String nick);

  public abstract int incrementScore(String nick, int by);

  public abstract int decrementScore(String nick);

  public abstract int decrementScore(String nick, int by);

  public abstract int setScore(String nick, int score);
}
