package casetrack.app.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import casetrack.app.testutil.PersonBuilder;

public class NameContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        NameContainsKeywordsPredicate firstPredicate = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        NameContainsKeywordsPredicate secondPredicate = new NameContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        NameContainsKeywordsPredicate firstPredicateCopy = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different person -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        // One keyword (single word phrase)
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Collections.singletonList("Alice"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Multiple keywords as consecutive phrase
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("Alice", "Bob"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Phrase match in middle of longer name
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("Bob", "Carol"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob Carol").build()));

        // Mixed-case keywords (phrase matching)
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("aLIce", "bOB"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Collections.emptyList());
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").build()));

        // Non-matching keyword
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("Carol"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Phrase not consecutive (Bob and Carol not next to each other)
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("Bob", "Carol"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Carol").build()));

        // Phrase words in wrong order
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("Bob", "Alice"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Keywords match phone, email and address, but does not match name
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("12345", "alice@email.com", "Main", "Street"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withPhone("12345")
                .withEmail("alice@email.com").withAddress("Main Street").build()));
    }

    @Test
    public void test_phraseWithPrefixMatch_returnsTrue() {
        // Single-word phrase with prefix match (bug report case: "Li" should match "LiZiBin")
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Collections.singletonList("Li"));
        assertTrue(predicate.test(new PersonBuilder().withName("LiZiBin").build()));

        // Single-word phrase prefix match at beginning of first word
        predicate = new NameContainsKeywordsPredicate(Collections.singletonList("Ali"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Single-word phrase prefix match at beginning of second word
        predicate = new NameContainsKeywordsPredicate(Collections.singletonList("Bo"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Two-word phrase with prefix matching (user's example: "John Dog")
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("John", "Dog"));
        assertTrue(predicate.test(new PersonBuilder().withName("John Dog").build()));

        // Two-word phrase with prefix match on second word
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("John", "Do"));
        assertTrue(predicate.test(new PersonBuilder().withName("John Dog").build()));

        // Case-insensitive phrase with prefix match
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("jOhN", "dOg"));
        assertTrue(predicate.test(new PersonBuilder().withName("John Dog").build()));

        // Full word match (prefix of entire word)
        predicate = new NameContainsKeywordsPredicate(Collections.singletonList("Alice"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").build()));
    }

    @Test
    public void test_phraseWithPrefixNoMatch_returnsFalse() {
        // Keyword not a prefix of any word
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Collections.singletonList("xyz"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // User's issue: "John Dog" should NOT match "John Doe"
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("John", "Dog"));
        assertFalse(predicate.test(new PersonBuilder().withName("John Doe").build()));

        // User's issue: "John Dog" should NOT match "John123"
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("John", "Dog"));
        assertFalse(predicate.test(new PersonBuilder().withName("John123").build()));

        // Phrase words not consecutive
        predicate = new NameContainsKeywordsPredicate(Arrays.asList("John", "Smith"));
        assertFalse(predicate.test(new PersonBuilder().withName("John Dog Smith").build()));

        // Keyword at end of word (not a prefix)
        predicate = new NameContainsKeywordsPredicate(Collections.singletonList("ce"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").build()));

        // Keyword in middle of word (not a prefix)
        predicate = new NameContainsKeywordsPredicate(Collections.singletonList("lic"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(keywords);

        String expected = NameContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
