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

import com.rectang.rcbot.StringUtils;

import java.util.Date;
import java.text.DateFormat;

public class StatTopicBean {

  private static final DateFormat SHORT_DATE =
      DateFormat.getDateInstance(DateFormat.MEDIUM);
  private String topic, nick;
  private Date when;

  public StatTopicBean(String topic, String nick, Date when) {
    this.topic = topic;
    this.nick = nick;
    this.when = when;
  }

  public String getTopic() {
    return topic;
  }

  public String getMarkupTopic() {
    return StringUtils.markupLinks(StringUtils.obscureEmail(topic));
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public String getNick() {
    return nick;
  }

  public void setNick(String nick) {
    this.nick = nick;
  }

  public Date getWhen() {
    return when;
  }

  public String getShortWhen() {
    return SHORT_DATE.format(when);
  }

  public void setWhen(Date when) {
    this.when = when;
  }
}
