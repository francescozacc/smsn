package net.fortytwo.myotherbrain.flashcards.decks.vocab;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * User: josh
 * Date: 3/19/11
 * Time: 8:20 PM
 */
public class SwedishVocabulary extends VocabularyDeck {
    private static final Logger LOGGER = Logger.getLogger(SwedishVocabulary.class.getName());

    public SwedishVocabulary() throws IOException {
        super("swedish_vocabulary", "Swedish vocabulary");
    }

    @Override
    public Dictionary createVocabulary() throws IOException {
        Dictionary dict = new Dictionary();

        Locale locale = findSwedishLocale();
        if (null == locale) {
            LOGGER.warning("locale for Swedish vocabulary is not available.  Loading an empty dictionary.");
        } else {
            VocabularySource omegaWiki = new VocabularySource("OmegaWiki");
            omegaWiki.setUrl("http://www.dicts.info/uddl.php");
            omegaWiki.setTimestamp("2011-03-24T07:46:30+01:00");

            InputStream is = SwedishVocabulary.class.getResourceAsStream("OmegaWiki_Swedish_English.txt");
            try {
                VocabularyParsers.parseDictsInfoList(is, dict, Locale.GERMAN, omegaWiki);
            } finally {
                is.close();
            }

            VocabularySource wiktionary = new VocabularySource("Wiktionary");
            wiktionary.setUrl("http://www.dicts.info/uddl.php");
            wiktionary.setTimestamp("2011-03-24T07:47:47+01:00");

            is = SwedishVocabulary.class.getResourceAsStream("Wiktionary_Swedish_English.txt");
            try {
                VocabularyParsers.parseDictsInfoList(is, dict, Locale.GERMAN, wiktionary);
            } finally {
                is.close();
            }
        }

        return dict;
    }

    private Locale findSwedishLocale() {
        for (Locale l : NumberFormat.getAvailableLocales()) {
            if (l.getISO3Language().equals("swe")) {
                return l;
            }
        }

        return null;
    }
}
