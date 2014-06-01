package de.bripkens.nashorn;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * @author Ben Ripkens <ben.ripkens@codecentric.de>
 */
@SuppressWarnings("restriction")
public class EngineTest extends AbstractNashornTest {

  @Test
  public void shouldProvideEngineDetails() {
    ScriptEngineFactory factory = engine.getFactory();

    assertThat(factory.getEngineName(), is("Oracle Nashorn"));
    assertThat(factory.getEngineVersion(), is("1.8.0_05"));
    assertThat(factory.getLanguageName(), is("ECMAScript"));
    assertThat(factory.getLanguageVersion(), is("ECMA - 262 Edition 5.1"));
  }

  @Test
  public void shouldHaveAGlobalObject() throws Exception {
    Object globalContext = engine.eval("this");
    assertThat(globalContext, is(not(nullValue())));
    assertThat(globalContext, is(instanceOf(ScriptObjectMirror.class)));
  }

  @Test
  public void shouldPermitGlobalMutation() throws Exception {
    engine.eval("this.foo = 'bar';");
    assertThat(engine.eval("this.foo;"), is("bar"));
  }

  @Test
  public void shouldNotShareGlobalsBetweenEngines() throws Exception {
    engine.eval("this.foo = 'bar';");
    assertThat(newEngine().eval("this.foo"), is(nullValue()));
  }

  @Test
  public void shouldBeCapableOfRetrievingTheGlobalScope() throws Exception {
    ScriptContext context = engine.getContext();
    Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
    assertThat(bindings.get("Math"), is(not(nullValue())));
    assertThat(bindings.get("Object"), is(not(nullValue())));
    assertThat(bindings.get("JSON"), is(not(nullValue())));

    // Danger: no setTimeout etc. available!
    assertThat(bindings.get("setTimeout"), is(nullValue()));
    assertThat(bindings.get("setInterval"), is(nullValue()));
  }

  @Test
  public void shouldSupportCompilation() {
    assertThat(engine, is(Matchers.<ScriptEngine> instanceOf(Compilable.class)));
  }

  @Test
  public void shouldCompileCode() throws Exception {
    Compilable compilable = (Compilable) engine;
    engine.eval(slurp("/shouldCompileCode.js"));
    CompiledScript compiledScript = compilable.compile("doIt()");
    Object result = compiledScript.eval();
    assertThat(result, is(instanceOf(String.class)));
  }

}
