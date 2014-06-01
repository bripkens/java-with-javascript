package de.bripkens.nashorn;

import org.junit.Test;

import javax.script.ScriptException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Ben Ripkens <ben.ripkens@codecentric.de>
 */
public class XmlHttpRequestTest extends AbstractEventLoopTest {

  @Test
  public void shouldHandleSimpleGetRequests() throws ScriptException {
    engine.eval(inEventLoop(slurp("/shouldHandleSimpleGetRequests.js")));

    assertThat(output.get("responseArrived"), is(true));
  }



}
