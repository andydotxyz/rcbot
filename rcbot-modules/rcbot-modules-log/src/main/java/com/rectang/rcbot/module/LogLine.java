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
import java.util.Hashtable;
import java.text.ParsePosition;
import java.text.DateFormat;

/**
 * A single line of log, provide utility functions for the velocity work
 */
public class LogLine {

  private Date time;
  private static final DateFormat SHORT_TIME =
      DateFormat.getTimeInstance(DateFormat.SHORT);
  private static final DateFormat SHORT_DATE_TIME =
      DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
  private String nick, message, styles;

  public LogLine(Log logger, String line, Hashtable nickhash) {
    styles = "";
    ParsePosition pos = new ParsePosition(0);
    time = logger.getTimeFormat().parse(line, pos);
    line = line.substring(pos.getIndex()).trim();
    nick = StringUtils.getFirstArg(line);
    message = line.substring(nick.length()).trim();
    if (nick.equals("*")) {
      nick = StringUtils.getFirstArg(message);
      message = message.substring(nick.length()).trim();
      styles += "action";
    } else {
      nick = nick.substring(0, nick.length() - 1); // remove :
    }

    int id = nickhash.size();
    if (nickhash.containsKey(nick))
      id = ((Integer) nickhash.get(nick)).intValue();
    else
      nickhash.put(nick, new Integer(id));
    styles += " nick" + id;

    message = StringUtils.obscureEmail(message);
    message = StringUtils.markupLinks(message);
  }

  public Date getTime() {
    return time;
  }

  public String getShortTime() {
    return SHORT_TIME.format(time);
  }

  public String getShortDateTime() {
    return SHORT_DATE_TIME.format(time);
  }

  public String getNick() {
    return nick;
  }

  public String getMessage() {
    return message;
  }

  public String getStyles() {
    return styles;
  }
}
