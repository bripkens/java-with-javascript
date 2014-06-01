package de.bripkens.nashorn;

import org.junit.After;
import org.junit.Before;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import java.util.HashMap;

/**
 * @author Ben Ripkens <ben.ripkens@codecentric.de>
 */
public abstract class AbstractEventLoopTest extends AbstractNashornTest {

  @Override
  @Before
  public void before() throws ScriptException {
    super.before();
    engine.eval(slurp("/event_loop.js"));
  }

  @After
  public void after() throws ScriptException {
    engine.eval("shutdown()");
  }

  public String inEventLoop(String code) {
    return "main(function() {'use strict';" + code + "\n});";
  }
}
