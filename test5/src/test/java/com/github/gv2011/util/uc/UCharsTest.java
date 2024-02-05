package com.github.gv2011.util.uc;

import static com.github.gv2011.util.CharacterType.COMBINING_SPACING_MARK;
import static com.github.gv2011.util.CharacterType.DASH_PUNCTUATION;
import static com.github.gv2011.util.CharacterType.DECIMAL_DIGIT_NUMBER;
import static com.github.gv2011.util.CharacterType.FINAL_QUOTE_PUNCTUATION;
import static com.github.gv2011.util.CharacterType.FORMAT;
import static com.github.gv2011.util.CharacterType.INITIAL_QUOTE_PUNCTUATION;
import static com.github.gv2011.util.CharacterType.LINE_SEPARATOR;
import static com.github.gv2011.util.CharacterType.MODIFIER_LETTER;
import static com.github.gv2011.util.CharacterType.MODIFIER_SYMBOL;
import static com.github.gv2011.util.CharacterType.NON_SPACING_MARK;
import static com.github.gv2011.util.CharacterType.OTHER_LETTER;
import static com.github.gv2011.util.CharacterType.OTHER_NUMBER;
import static com.github.gv2011.util.CharacterType.OTHER_PUNCTUATION;
import static com.github.gv2011.util.CharacterType.OTHER_SYMBOL;
import static com.github.gv2011.util.CharacterType.PARAGRAPH_SEPARATOR;
import static com.github.gv2011.util.CharacterType.SPACE_SEPARATOR;
import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.icol.ICollections.emptySet;
import static com.github.gv2011.util.icol.ICollections.setOf;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static java.lang.Character.DIRECTIONALITY_COMMON_NUMBER_SEPARATOR;
import static java.lang.Character.DIRECTIONALITY_EUROPEAN_NUMBER;
import static java.lang.Character.DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR;
import static java.lang.Character.DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR;
import static java.lang.Character.DIRECTIONALITY_LEFT_TO_RIGHT;
import static java.lang.Character.DIRECTIONALITY_OTHER_NEUTRALS;
import static java.lang.Character.UnicodeScript.COMMON;
import static java.lang.Character.UnicodeScript.LATIN;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.Character.UnicodeScript;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import com.github.gv2011.util.CharacterType;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.icol.ISet;

class UCharsTest {

  private static final Logger LOG = getLogger(UCharsTest.class);

  @Test
  void testUCharInt() {
    final int maxCharacters = 100;
    final ISet<UnicodeScript> selectedScripts = setOf(COMMON, LATIN);
    final ISet<CharacterType> selectedTypes = emptySet();

    final ISet<Byte> directionalities = setOf(
      DIRECTIONALITY_LEFT_TO_RIGHT,
      DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR,
      DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR,
      DIRECTIONALITY_OTHER_NEUTRALS,
      DIRECTIONALITY_EUROPEAN_NUMBER,
      DIRECTIONALITY_COMMON_NUMBER_SEPARATOR
    );
    final ISet<CharacterType> excludedTypes = setOf(
      NON_SPACING_MARK,
      MODIFIER_LETTER, MODIFIER_SYMBOL,
      COMBINING_SPACING_MARK,
      DASH_PUNCTUATION, OTHER_LETTER, OTHER_NUMBER,
      FORMAT, LINE_SEPARATOR, PARAGRAPH_SEPARATOR, SPACE_SEPARATOR,
      OTHER_SYMBOL, DECIMAL_DIGIT_NUMBER,
      FINAL_QUOTE_PUNCTUATION, INITIAL_QUOTE_PUNCTUATION
    );

    final ISet<CharacterType> excludedTypesInCommon = emptySet();
    //LOWERCASE_LETTER, UPPERCASE_LETTER

    final ISet<CharacterType> excludedTypesInNonBmp = //emptySet();
      setOf(OTHER_PUNCTUATION)
    ;

    final ISet<Integer> included =
      Stream.concat(
        Stream.of("-","_"),
        IntStream.range(0, 10).mapToObj(Integer::toString)
      )
      .map(s->s.codePointAt(0)).collect(toISet())
    ;

    final boolean onlyNonBmp = false;

    Arrays.stream(UnicodeScript.values())
    .filter(s->selectedScripts.isEmpty() || selectedScripts.contains(s))
    .sorted(comparing(UnicodeScript::name))
    .forEach(script->{
      LOG.info("ẞ𝛀Script: "+script.name());

      final Map<Pair<CharacterType, Boolean>, ISet<UChar>> map = IntStream.range(0, Character.MAX_CODE_POINT+1)
        .filter(cp->cp>Character.MAX_VALUE || !Character.isSurrogate((char) cp))
        .filter(cp->!Character.isISOControl(cp) || included.contains(cp))
        .filter(cp->UnicodeScript.of(cp).equals(script))
        .filter(cp->directionalities.contains(Character.getDirectionality(cp)))
        .mapToObj(UChars::uChar)
        .filter(c->onlyNonBmp ? !c.isBmpCharacter() : true)
        .filter(c->selectedTypes.isEmpty() || selectedTypes.contains(c.type()))
        .filter(c->!excludedTypes.contains(c.type()) || included.contains(c.codePoint()))
        .filter(c->!c.script().equals(COMMON) || !excludedTypesInCommon.contains(c.type()) || included.contains(c.codePoint()))
        .filter(c->c.isBmpCharacter() || included.contains(c.codePoint()) || !excludedTypesInNonBmp.contains(c.type()))
        .collect(groupingBy(c->pair(c.type(),c.isBmpCharacter()), toISet()))
      ;
      map.keySet().stream()
      .sorted(
        Comparator.<Pair<CharacterType, Boolean>,String>comparing(p->p.getKey().name())
        .thenComparing(Pair::getValue)
      )
      .forEach(p->{
        LOG.info(
          " Type: {}\n{}",
          p.getKey().name(),
          map.get(p).stream().sorted().limit(maxCharacters)
          .map(uChar->
            " "+(uChar.isBmpCharacter() ? " " : "*") + " |"+uChar+"| "+
            Integer.toHexString(uChar.codePoint())+" "+uChar.type()+" "+
            uChar.name()+" "+Character.getDirectionality(uChar.codePoint())
          )
          .collect(joining("\n"))
        );
      });
    });
  }

}


