package de.bripkens.nashorn;

import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;

/**
 * @author Ben Ripkens <ben.ripkens@codecentric.de>
 */
public class EnvJsTest extends AbstractNashornTest {

  @Before
  public void before() throws ScriptException {
    super.before();
    engine.eval(slurp("/env.nashorn.1.2.js"));
  }

  @Test
  public void shouldHandleSimpleGetRequests() throws ScriptException {
    engine.eval(slurp("/shouldHandleSimpleGetRequests.js"));
  }

}
