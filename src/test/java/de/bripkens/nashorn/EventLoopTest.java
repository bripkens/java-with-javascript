package de.bripkens.nashorn;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;

import jdk.nashorn.internal.objects.NativeArray;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Ben Ripkens <ben.ripkens@codecentric.de>
 */
@SuppressWarnings("restriction")
public class EventLoopTest extends AbstractNashornTest {

  private Map<String, Object> output;

  @Override
  @Before
  public void before() throws ScriptException {
    super.before();

    output = new HashMap<>();

    engine.eval(slurp("/event_loop.js"));
    engine.getBindings(ScriptContext.ENGINE_SCOPE).put("output", output);
  }

  @After
  public void after() throws ScriptException {
    engine.eval("shutdown()");
  }

  @Test
  public void shouldRegisterMissingSetXFunctions() {
    ScriptContext context = engine.getContext();
    Bindings globals = context.getBindings(ScriptContext.ENGINE_SCOPE);

    assertThat(globals.get("setTimeout"), is(not(nullValue())));
    assertThat(globals.get("clearTimeout"), is(not(nullValue())));
    assertThat(globals.get("setInterval"), is(not(nullValue())));
    assertThat(globals.get("clearInterval"), is(not(nullValue())));

    assertThat(globals.get("main"), is(not(nullValue())));
  }

  @Test
  public void shouldExecuteAsPartOfEventLoop() throws ScriptException {
    engine.eval(slurp("/shouldExecuteAsPartOfEventLoop.js"));
    assertThat(output.get("foo"), is("bar"));
  }

  @Test
  public void shouldInterleaveExecutions() throws Exception {
    engine.eval(slurp("/shouldInterleaveExecutions.js"));

    Object numbers = output.get("numbers");
    assertThat(numbers, is(instanceOf(NativeArray.class)));

    NativeArray arr = (NativeArray) numbers;
    assertThat(arr.getLength(), is(5L)); // take care - long value!
    assertThat(arr.get(0), is(10));
    assertThat(arr.get(1), is(20));
    assertThat(arr.get(2), is(30));
    assertThat(arr.get(3), is(40));
    assertThat(arr.get(4), is(50));
  }

  @Test
  public void shouldClearTimeout() throws Exception {
    engine.eval(slurp("/shouldClearTimeout.js"));

    Object numbers = output.get("numbers");
    assertThat(numbers, is(instanceOf(NativeArray.class)));

    NativeArray arr = (NativeArray) numbers;
    assertThat(arr.getLength(), is(2L)); // take care - long value!
    assertThat(arr.get(0), is(1));
    assertThat(arr.get(1), is(2));
  }

  @Test
  public void shouldSupportIntervals() throws Exception {
    engine.eval(slurp("/shouldSupportIntervals.js"));
    assertThat((double) output.get("iterationCount"), is(5.0));
  }

  @Test
  public void shouldSupportMultipleMainCallsInSameEngine() throws Exception {
    engine.eval(join(//
        "main(function() {", //
        "  output.numbers = [];", //
        "  output.numbers.push('a');", //
        "});"//
    ));

    NativeArray arr = (NativeArray) output.get("numbers");
    assertThat(arr.getLength(), is(1L));
    assertThat(arr.get(0), is("a"));

    engine.eval(join(//
        "main(function() {", //
        "  output.numbers.push('b');", //
        "});"));

    assertThat(arr.getLength(), is(2L));
    assertThat(arr.get(0), is("a"));
    assertThat(arr.get(1), is("b"));
  }

  @Test
  public void shouldHandleNestedAsyncOperations() throws Exception {
    engine.eval(join(//
        "main(function() {", //
        "  setTimeout(function() {", //
        "    setTimeout(function() {", //
        "      output.val = 'done';", //
        "    }, 20);", //
        "  }, 20);", "});"//
        ));

    assertThat(output.get("val"), is("done"));
  }
}
