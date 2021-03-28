package com.github.gv2011.util.num;

import static com.github.gv2011.util.num.BigDecimalUtils.toEngineering;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class BigDecimalUtilsTest {
  
  @Parameters(name="{0}")
  public static final Object[][] params(){
    return new Object[][]{
      new Object[]{      "0"    ,       0L,  0,   "0"        },
      new Object[]{      "1"    ,       1L,  0,   "1"        },
      new Object[]{     "10"    ,      10L,  0,   "10"       },
      new Object[]{    "100"    ,     100L,  0,  "100"       },
      new Object[]{   "1000"    ,       1L, -3,    "1E+3"    },
      new Object[]{"1000000"    ,       1L, -6,    "1E+6"    },
      new Object[]{"1002000"    ,    1002L, -3,    "1.002E+6"},
      new Object[]{      "0.1"  ,     100L,  3,    "0.100"   },
      new Object[]{      "0.01" ,      10L,  3,    "0.010"   },
      new Object[]{      "0.001",       1L,  3,    "0.001"   },
      new Object[]{      "1.2"  ,    1200L,  3,    "1.200"   },
      new Object[]{   "1000.2"  , 1000200L,  3, "1000.200"   },
    };
  }
  
  private final String dec;
  private final long mantissa;
  private final int exponent;
  private final String out;
  

  public BigDecimalUtilsTest(String dec, long mantissa, int exponent, String out) {
    this.dec = dec;
    this.mantissa = mantissa;
    this.exponent = exponent;
    this.out = out;
  }


  @Test
  public void testToEngineering() {
    final BigDecimal eng = toEngineering(new BigDecimal(dec));
    
    assertThat(eng, is(new BigDecimal(BigInteger.valueOf(mantissa), exponent)));
    assertThat(eng.toString(), is(out));
  }

}
