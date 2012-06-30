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

import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.Collections;

public class StatBean {

  private Date startTime;
  private String channel, host, nick;
  private List topics, users;
  private transient boolean dirty = false;

  public StatBean(String channel) {
    this.startTime = new Date();
    this.channel = channel;
    this.topics = new Vector();
    this.users = new Vector();
  }

  public Date getStartTime() {
    return startTime;
  }

  void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public String getChannel() {
    return channel;
  }

  public void setHost(String host) {
    this.host = host;
  }


  public String getHost() {
    return host;
  }

  public void setDirty(boolean dirty) {
    this.dirty = dirty;
  }

  public boolean isDirty() {
    return dirty;
  }

  public void setNick(String nick) {
    this.nick = nick;
    setDirty(true);
  }

  public String getNick() {
    return nick;
  }

  public void addTopic(StatTopicBean topic) {
    topics.add(0, topic);
  }

  public void setTopics(List topics) {
    this.topics = topics;
    setDirty(true);
  }

  public List getTopics() {
    return topics;
  }

  public void setUsers(List users) {
    this.users = users;
    setDirty(true);
  }

  public List getUsers() {
    Collections.sort(users);
    return users;
  }
}
