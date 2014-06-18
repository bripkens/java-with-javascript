package de.bripkens.nashorn;

import java.util.Map;

/**
 * @author Ben Ripkens <ben.ripkens@codecentric.de>
 */
public class StrengthCalculator {

  private final Runner runner;

  public StrengthCalculator() {
    runner = new Runner();
    runner.execFile("/bower_components/zxcvbn/zxcvbn.js");
  }

  public Strength calculate(String password) {
    String escapedPassword = password.replace("'", "\\'");
    runner.execCode("output.strength = zxcvbn('" + escapedPassword + "');");
    Map<String, Object> result = (Map<String, Object>) runner.get("output.strength");

    return new Strength(((Double) result.get("entropy")).intValue(),
      (int) result.get("score"),
      ((Double) result.get("crack_time")).intValue());
  }

  public static class Strength {
    private final int entropy;

    private final int score;

    private final int crackTimeSeconds;

    public Strength(int entropy, int score, int crackTimeSeconds) {
      this.entropy = entropy;
      this.score = score;
      this.crackTimeSeconds = crackTimeSeconds;
    }

    public int getEntropy() {
      return entropy;
    }

    public int getScore() {
      return score;
    }

    public int getCrackTimeSeconds() {
      return crackTimeSeconds;
    }
  }

}
