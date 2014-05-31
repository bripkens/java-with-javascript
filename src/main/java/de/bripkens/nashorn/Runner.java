package de.bripkens.nashorn;

import org.apache.commons.io.IOUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Ben Ripkens <ben.ripkens@codecentric.de>
 */
public class Runner {

  private static final String WRAPPER_PRE = "main(function() {'use strict';\n";
  private static final String WRAPPER_POST = "\n});";

  private ScriptEngine engine;

  public Runner() {
    ScriptEngineManager manager = new ScriptEngineManager();
    engine = manager.getEngineByName("nashorn");

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
}
