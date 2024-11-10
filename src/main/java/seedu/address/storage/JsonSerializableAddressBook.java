package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons) {
        this.persons.addAll(persons);
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType(boolean skipDuplicate) throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            if (skipDuplicate && !jsonAdaptedPerson.isValidPerson()) {
                continue;
            }
            if (jsonAdaptedPerson.hasEmptyContactInfo()) {
                jsonAdaptedPerson.fillEmptyContactInfo();
            }
            jsonAdaptedPerson.clearInvalidContactInfo();
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person) && !skipDuplicate) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            addressBook.addPerson(person);
        }
        return addressBook;
    }

    /**
     * Merges two instances of {@Code AddressBook}, adding all instances of {@Code Person} from the source to the
     * target.
     * @param target the {@Code AddressBook} that the Persons will be added to
     * @param source the {@Code AddressBook} that the Persons will be added from
     * @return the target {@Code AddressBook} after adding the source {@Code AddressBook}
     */
    public JsonSerializableAddressBook mergeAddressBook(JsonSerializableAddressBook target,
                                                        JsonSerializableAddressBook source) {
        target.persons.addAll(source.persons);
        return target;
    }

}