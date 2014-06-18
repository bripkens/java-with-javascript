package de.bripkens.nashorn;

import org.junit.Before;
import org.junit.Test;
import de.bripkens.nashorn.StrengthCalculator.Strength;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Ben Ripkens <ben.ripkens@codecentric.de>
 */
public class StrengthCalculatorTest {

  private StrengthCalculator calc;

  @Before
  public void before() {
    calc = new StrengthCalculator();
  }

  @Test
  public void shouldIdentifyPasswordStrength() {
    Strength strength = calc.calculate("password");
    assertThat(strength.getScore(), is(0));
    assertThat(strength.getCrackTimeSeconds(), is(0));
    assertThat(strength.getEntropy(), is(0));

    strength = calc.calculate("homer42Hij");
    assertThat(strength.getScore(), is(2));
    assertThat(strength.getCrackTimeSeconds(), is(68400)); // ~19 hours
    assertThat(strength.getEntropy(), is(30));
  }

  @Test
  public void shouldHandlePasswordsWithSingleQuotes() {
    Strength strength = calc.calculate("jo'2131k'dsa9'?!");
    assertThat(strength.getScore(), is(4));
    assertThat(strength.getCrackTimeSeconds(), is(2147483647)); // ~68 years
    assertThat(strength.getEntropy(), is(69));
  }

}
