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

/**
 * A single channel that we log - for the listing page
 */
public class LogChannel {

  private String name, topic;

  public LogChannel(String name, String topic) {
    this.name = name;
    this.topic = topic;
  }

  public String getName() {
    return name;
  }

  public String getLink() {
    return name.replaceFirst("#", "%23") + "/";
  }

  public String getTopic() {
    return topic;
  }

  public String getMarkupTopic() {
    return StringUtils.markupLinks(topic);
  }
}
