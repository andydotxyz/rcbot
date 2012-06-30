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

package com.rectang.rcbot;

import java.util.regex.Pattern;

public class StringUtils {

  private static Pattern HTTPPattern = Pattern.compile("(^|\\s)(http://.+?)(\\s|$)");
  private static Pattern HTTPSPattern = Pattern.compile("(^|\\s)(https://.+?)(\\s|$)");
  private static Pattern WWWPattern = Pattern.compile("(^|\\s)(www\\..+?)(\\s|$)");

  private static Pattern EmailPattern = Pattern.compile("(^|\\s)(\\S+?)@(\\S+?)\\.(\\S+?)(\\s|$)");

  public static String getFirstArg(String in) {
    String args = in.trim();
    int pos = args.indexOf(" ");
    if (pos == -1)
      pos = args.length();

    return args.substring(0, pos);
  }

  public static boolean isChannel(String channel) {
    return (channel != null) && (channel.length() > 1) &&
      (channel.charAt(0) == '#');
  }

  public static String formatTimeOffset(long offset) {
    if (offset == 0)
      return "no time";
    long tmp = offset;
    StringBuffer ret = new StringBuffer();

    long sec = tmp % 60;
    timeOffsetPrepend(sec, "second", "", ret);
    tmp = tmp / 60;

    long min = tmp % 60;
    if (min > 0) {
      timeOffsetPrepend(min, "minute", " and ", ret);
      tmp = tmp / 60;

      long hour = tmp % 24;
      if (hour > 0) {
        timeOffsetPrepend(hour, "hour", ", ", ret);
        tmp = tmp / 24;

        long day = tmp % 365;
        if (day > 0) {
          timeOffsetPrepend(day, "day", ", ", ret);
          tmp = tmp / 365;

          long year = tmp;
          if (year > 0) {
            timeOffsetPrepend(year, "year", ", ", ret);
          }
        }
      }
    }
    return ret.toString();
  }

  private static void timeOffsetPrepend(long num, String prefix, String postfix,
                                        StringBuffer s) {
    s.insert(0, postfix);
    if (num != 1)
      s.insert(0, "s");
    s.insert(0, prefix);
    s.insert(0, " ");
    s.insert(0, num);
  }

  public static String markupLinks(String in) {
    if (in == null)
      return null;
    if (in.equals(""))
      return "";
    String ret = HTTPPattern.matcher(in).replaceAll("$1<a href=\"$2\">$2</a>$3");
    ret = HTTPSPattern.matcher(ret).replaceAll("$1<a href=\"$2\">$2</a>$3");
    ret = WWWPattern.matcher(ret).replaceAll("$1<a href=\"http://$2\">$2</a>$3");
    return ret;
  }

  public static String obscureEmail(String in) {
    return EmailPattern.matcher(in).replaceAll("$1$2@XXX.$4$5");
  }
}
