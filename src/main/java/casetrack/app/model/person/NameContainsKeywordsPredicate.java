package casetrack.app.model.person;

import java.util.List;
import java.util.function.Predicate;

import casetrack.app.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name} matches any of the keywords given.
 */
public class NameContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    public NameContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        if (keywords.isEmpty()) {
            return false;
        }
        String[] nameWords = person.getName().fullName.split("\\s+");
        outer: for (int i = 0; i <= nameWords.length - keywords.size(); i++) {
            for (int j = 0; j < keywords.size(); j++) {
                if (!nameWords[i + j].toLowerCase().startsWith(keywords.get(j).toLowerCase())) {
                    continue outer;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NameContainsKeywordsPredicate)) {
            return false;
        }

        NameContainsKeywordsPredicate otherNameContainsKeywordsPredicate = (NameContainsKeywordsPredicate) other;
        return keywords.equals(otherNameContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
