package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class MainTest {

    private static final String MESSAGE_2
            = "Where are you? You are late! I have asked you to be on time.";

    // Populate the arrays with the test data before every test
    @BeforeEach
    public void setUp() {
        Main.loadTestData();
    }

    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {
        List<String> expected = List.of(
                "Did you get the cake?", "It is dinner time!");
        assertEquals(expected, Main.sentMessages);
    }

    @Test
    public void testDisplayLongestMessage() {
        assertEquals(MESSAGE_2, Main.getLongestMessage(Main.messageTexts));
    }

    @Test
    public void testSearchForMessageID() {
        // Message 4
        int position = Main.findIndexByID("1000000004");
        assertEquals("0838884567", Main.messageRecipients.get(position));
        assertEquals("It is dinner time!", Main.messageTexts.get(position));
    }

    @Test
    public void testSearchMessagesForRecipient() {
        List<String> expected = List.of(
                MESSAGE_2, "Ok, I am leaving without you.");
        assertEquals(expected, Main.searchByRecipient("+27838884567"));
    }

    @Test
    public void testDeleteMessageUsingHash() {
        // Test Message 2
        String hash = Main.createMessageHash("1000000002", 2, MESSAGE_2);

        String result = Main.deleteMessageByHash(hash);

        assertEquals("Message: \"" + MESSAGE_2 + "\" successfully deleted.",
                result);
        assertFalse(Main.storedMessages.contains(MESSAGE_2));
        assertEquals(1, Main.storedMessages.size());
    }

    @Test
    public void testReadJsonFileIntoArray(@TempDir Path folder) {
        String originalFile = Main.jsonFile;
        Main.jsonFile = folder.resolve("test_messages.json").toString();

        List<String> expected = List.of(
                MESSAGE_2, "Ok, I am leaving without you.");

        Main.saveStoredMessages();
        Main.clearAllData();
        Main.loadStoredMessages();

        assertEquals(expected, Main.storedMessages);

        Main.jsonFile = originalFile;
    }
}
