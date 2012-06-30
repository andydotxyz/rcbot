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

import org.codehaus.plexus.embed.Embedder;

public class Main2 {

  public static void main(String args[]) throws Exception {
    String id = "RCBot";

    if (args.length > 1) {
      doHelp();
      return;
    } else if (args.length == 1) {
      if (args[0].toLowerCase().equals("help")) {
        doHelp();
        return;
      } else
        id = args[0];
    }

    Embedder plexus = new Embedder();
    plexus.addContextValue("rcbot.id", id);
    plexus.start();

    RCBot bot = (RCBot) plexus.getContainer().lookup(RCBot.ROLE);
    bot.connect();
  }

  public static void doHelp() {
    System.out.println("Usage RCBot [BotID|help]");
  }
}
