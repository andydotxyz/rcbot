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

public class StatUserBean implements Comparable {

  private static final DateFormat SHORT_DATE =
      DateFormat.getDateInstance(DateFormat.MEDIUM);
  private String nick, quote;
  private int lines;
  private Date lastSeen;

  public StatUserBean(String nick) {
    this.nick = nick;
    this.lines = 0;
    this.lastSeen = new Date();
    this.quote = "";
  }

  public String getNick() {
    return nick;
  }

  public void setNick(String nick) {
    this.nick = nick;
  }

  public String getQuote() {
    return quote;
  }

  public String getMarkupQuote() {
    return StringUtils.markupLinks(StringUtils.obscureEmail(quote));
  }

  public void setQuote(String quote) {
    this.quote = quote;
  }

  public int getLines() {
    return lines;
  }

  public void setLines(int lines) {
    this.lines = lines;
  }

  public void incLines() {
    lines++;
  }

  public Date getLastSeen() {
    return lastSeen;
  }

  public String getShortLastSeen() {
    return SHORT_DATE.format(lastSeen);
  }

  public void setLastSeen(Date lastSeen) {
    this.lastSeen = lastSeen;
  }

  public int compareTo(Object o) {
    if (!(o instanceof StatUserBean))
      return 0;
    StatUserBean user = (StatUserBean) o;

    return user.getLines() - getLines();
  }
}
