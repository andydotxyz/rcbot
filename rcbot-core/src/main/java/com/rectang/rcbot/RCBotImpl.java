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
import org.headsupdev.irc.IRCServiceManager;
import org.headsupdev.irc.IRCUser;

import java.io.File;

/**
 * Default implementation of the RCBot role
 *
 */
public class RCBotImpl extends RCBot {

  private String ownerNick, ownerLogin, ownerHost;
  private long startTime;

  private IRCConnection connection;
  private StorageImpl config;

  private IRCServiceManager manager;

  private String id;

  public RCBotImpl(String id) {
    RCBot.setInstance(this);
    this.id = id;
    this.startTime = System.currentTimeMillis();

    manager = new ProxyIRCServiceManager(this);
  }

  private void init() {
    config = StorageImpl.getInstance("config", this);

    ownerNick = getConfig().getString("owner.nick");
    ownerLogin = getConfig().getString("owner.login");
    ownerHost = getConfig().getString("owner.host");
  }

  public String getId() {
    return id;
  }

  public long getStartTime() {
    return startTime;
  }

  public void connect() throws Exception {
    connection = manager.connect(getConfig().getString("bot.server"),
        getConfig().getString("bot.nick"), getConfig().getString("bot.password"),
        getConfig().getString("bot.user"), getConfig().getString("bot.name"));
    String channels[] = getConfig().getString("bot.channels").split(",");

    for (int i = 0; i < channels.length; i++) {
      String channel = channels[i].trim();
      connection.join(channel);
    }

    String nickServPass = getConfig().getString("nickserv.pass");
    if (nickServPass != null && !nickServPass.equals(""))
      connection.sendMessage("nickserv", "identify " + nickServPass);
  }

  public Config getConfig() {
    return config;
  }

  public boolean isOwner(IRCUser user) {
    if (ownerNick == null || ownerNick.equals("") ||
        ownerNick.equals(user.getNick()))
      if (ownerLogin == null || ownerLogin.equals("") ||
          user.getLogin().endsWith(ownerLogin))
        if (ownerHost == null || ownerHost.equals("") ||
            ownerHost.equals(user.getHost()))
          return true;
    return false;
  }

  public File getDataRoot() {
    return new File(getId() + "_data");
  }

  public void start() {
    System.out.println("Starting RCBot[" + id + "]");

    init();
  }

  public void stop() {
    System.out.println("Stopping RCBot");
  }

  public IRCConnection getConnection() {
    return connection;
  }
}
