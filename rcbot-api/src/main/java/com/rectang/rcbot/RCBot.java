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

import org.headsupdev.irc.IRCConnection;
import org.headsupdev.irc.IRCUser;

import java.io.File;

public abstract class RCBot {

  private static RCBot instance;

  public static RCBot getInstance() {
    return instance;
  }

  protected static void setInstance(RCBot bot) {
    instance = bot;
  }

  public abstract String getId();

  public abstract long getStartTime();

  public abstract Config getConfig();

  public abstract boolean isOwner(IRCUser user);

  public abstract void connect() throws Exception;

  public abstract File getDataRoot();

  public abstract IRCConnection getConnection();
}
