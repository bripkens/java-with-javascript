package de.bripkens.nashorn;

import org.apache.commons.io.IOUtils;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Ben Ripkens <ben.ripkens@codecentric.de>
 */
public class Runner {

  private static final String WRAPPER_PRE = "main(function() {'use strict';\n";
  private static final String WRAPPER_POST = "\n});";

  private static final Pattern KEY_ACCESS_PATTERN = Pattern.compile("^[a-z_][a-z_0-9]*(\\.[a-z_][a-z_0-9]*)*$", Pattern.CASE_INSENSITIVE);

  private ScriptEngine engine;

  public Runner() {
    ScriptEngineManager manager = new ScriptEngineManager();
    engine = manager.getEngineByName("nashorn");

    Map<String, Object> output = new HashMap<>();
    engine.getBindings(ScriptContext.ENGINE_SCOPE).put("output", output);

    // execFile(...) and execCode(...) cannot be used as both would apply
    // the wrapper. The wrapper cannot be used at this point because it
    // relies on the global main(...) function which is added by the
    // event loop file.
    exec(slurp("/event_loop.js"));
  }

  public void execFile(String path) {
    execCode(slurp(path));
  }

  public void execCode(String code) {
    exec(WRAPPER_PRE + code + WRAPPER_POST);
  }

  private void exec(String code) {
    try {
      engine.eval(code);
    } catch (ScriptException e) {
      throw new RuntimeException(e);
    }
  }

  private static String slurp(String filename) {
    InputStream in = Runner.class.getResourceAsStream(filename);
    try {
      return IOUtils.toString(in, "UTF-8");
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  public void shutdown() {
    try {
      engine.eval("shutdown()");
    } catch (ScriptException e) {
      throw new RuntimeException(e);
    }
  }

  public Object get(String key) {
    if (!KEY_ACCESS_PATTERN.matcher(key).matches()) {
      throw new IllegalArgumentException("You may only access global state via the get() method.");
    }
    try {
      return engine.eval(key);
    } catch (ScriptException e) {
      throw new RuntimeException(e);
    }
  }
}
