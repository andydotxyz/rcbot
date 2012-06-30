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

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

/**
 * FeedWatcher Tester.
 *
 * @author Andrew Williams
 * @since <pre>10/30/2006</pre>
 * @version 1.0
 */
public class FeedWatcherTest extends TestCase {
  public FeedWatcherTest(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    super.setUp();
  }

  public void tearDown() throws Exception {
    super.tearDown();
  }

  public void testAddFeed() {
    FeedWatcher watcher = new FeedWatcher();
    System.out.println(watcher.addFeed("channel",
        "http://handyande.co.uk/Coding_News/feed.xml"));
  }

  public static Test suite() {
    return new TestSuite(FeedWatcherTest.class);
  }
}
