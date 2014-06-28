package de.bripkens.nashorn;

import org.junit.Test;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Ben Ripkens <ben.ripkens@codecentric.de>
 */
public class StrengthCalculatorWithoutEventLoopTest extends AbstractNashornTest {

  @Test
  public void shouldCalculatePasswordStength() throws Exception {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("nashorn");

    // register global scope as 'window'
    Bindings globalScope = engine.getBindings(ScriptContext.ENGINE_SCOPE);
    globalScope.put("window", globalScope);

    // load dependency which registers global zxcvbn function
    engine.eval(slurp("/bower_components/zxcvbn/zxcvbn.js"));

    // you should _never_ pass user provided values via string concatenation
    // to Nashorn. Check out my other repository to see a better way of doing
    // this with Nashorn:
    // https://github.com/bripkens/nashorn-pw-check-example/blob/master/src/main/java/de/codecentric/StrengthChecker.java
    Map<String, Object> result;
    result = (Map<String, Object>) engine.eval("zxcvbn('myPasswordIsRatherLong');");

    assertThat(result.get("score"), is(2));
  }

}
