package com.mycompany.main;

// Imports for user input, random numbers, lists and JSON file storage
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class Main {

    // Scanner for user input
    static Scanner input = new Scanner(System.in);

    // Registration details
    static String username;
    static String password;
    static String cellphone;

    static String registeredUsername;
    static String registeredPassword;
    static String registeredCellphone;

    // Message information
    static int totalMessages;
    static int messagesEntered = 0;
    static int messagesSent = 0;

    // JSON file used to store messages
    static String jsonFile = "stored_messages.json";

    // ===== ARRAYS (populated by code, no hard-coding) =====

    // Sent messages (parallel arrays)
    static List<String> sentMessages = new ArrayList<>();
    static List<String> sentRecipients = new ArrayList<>();

    // Disregarded messages (parallel arrays)
    static List<String> disregardedMessages = new ArrayList<>();
    static List<String> disregardedRecipients = new ArrayList<>();

    // Stored messages (parallel arrays) - read from the JSON file
    static List<String> storedMessages = new ArrayList<>();
    static List<String> storedRecipients = new ArrayList<>();
    static List<String> storedIDs = new ArrayList<>();
    static List<String> storedHashes = new ArrayList<>();

    // All message hashes and IDs (parallel arrays holding every message)
    static List<String> messageHashes = new ArrayList<>();
    static List<String> messageIDs = new ArrayList<>();
    static List<String> messageRecipients = new ArrayList<>();
    static List<String> messageTexts = new ArrayList<>();

    // Check username
    public static boolean checkUserName(String username) {

        return username.length() == 5
                && username.contains("_");
    }

    // Check password complexity
    public static boolean checkPasswordComplexity(String password) {

        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[^a-zA-Z0-9].*");
    }

    // Check South African cellphone number
    public static boolean checkCellphoneNumber(String number) {

        return number.matches("^\\+27[0-9]{9}$");
    }

    // Register the user
    public static void registerUser() {

        boolean validRegistration = false;

        while (!validRegistration) {

            System.out.println("\n===== QUICKCHAT REGISTRATION =====");

            System.out.print("Enter your username: ");
            username = input.nextLine();

            System.out.print("Enter your password: ");
            password = input.nextLine();

            System.out.print("Enter cellphone number (+27): ");
            cellphone = input.nextLine();

            boolean usernameCorrect = checkUserName(username);
            boolean passwordCorrect = checkPasswordComplexity(password);
            boolean phoneCorrect = checkCellphoneNumber(cellphone);

            if (usernameCorrect && passwordCorrect && phoneCorrect) {

                registeredUsername = username;
                registeredPassword = password;
                registeredCellphone = cellphone;

                System.out.println("User registered successfully!");

                validRegistration = true;

            } else {

                System.out.println("\nRegistration failed.");

                if (!usernameCorrect) {
                    System.out.println(
                            "Username must be exactly 5 characters "
                            + "and contain an underscore (_).");
                }

                if (!passwordCorrect) {
                    System.out.println(
                            "Password must have at least 8 characters, "
                            + "an uppercase letter, lowercase letter, "
                            + "number and special character.");
                }

                if (!phoneCorrect) {
                    System.out.println(
                            "Cellphone number must start with +27 "
                            + "followed by exactly 9 digits.");
                }

                System.out.println("Please enter your details again.");
            }
        }
    }

    // Log in using registered details
    public static boolean loginUser(String username, String password) {

        return username.equals(registeredUsername)
                && password.equals(registeredPassword);
    }

    // Display login status
    public static String returnLoginStatus(boolean loginSuccessful) {

        if (loginSuccessful) {
            return "Login successful. Welcome "
                    + registeredUsername + "!";
        } else {
            return "Username or password incorrect. Please try again.";
        }
    }

    // Ask the user to log in
    public static boolean performLogin() {

        int attempts = 0;

        while (attempts < 3) {

            System.out.println("\n===== QUICKCHAT LOGIN =====");

            System.out.print("Enter your username: ");
            String loginUsername = input.nextLine();

            System.out.print("Enter your password: ");
            String loginPassword = input.nextLine();

            boolean successful = loginUser(
                    loginUsername, loginPassword);

            System.out.println(returnLoginStatus(successful));

            if (successful) {
                return true;
            }

            attempts++;
        }

        System.out.println("Too many failed attempts. Exiting QuickChat.");
        return false;
    }

    // Generate a random 10-digit message ID
    public static String generateMessageID() {

        long number = ThreadLocalRandom.current().nextLong(
                1_000_000_000L, 10_000_000_000L);

        return String.valueOf(number);
    }

    // Generate the message hash
    public static String createMessageHash(
            String messageID, int messageNumber, String message) {

        String[] words = message.trim().split("\\s+");

        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        return (messageID.substring(0, 2) + ":"
                + messageNumber + ":"
                + firstWord + lastWord).toUpperCase();
    }

    // ===== ARRAY METHODS =====

    // Clear every array
    public static void clearAllData() {

        sentMessages.clear();
        sentRecipients.clear();
        disregardedMessages.clear();
        disregardedRecipients.clear();
        storedMessages.clear();
        storedRecipients.clear();
        storedIDs.clear();
        storedHashes.clear();
        messageHashes.clear();
        messageIDs.clear();
        messageRecipients.clear();
        messageTexts.clear();
    }

    // Add a message to the arrays using a hash that already exists
    public static void addMessageWithHash(String messageID,
            String messageHash, String recipient,
            String message, String flag) {

        // Every message goes into the ID and hash arrays
        messageIDs.add(messageID);
        messageHashes.add(messageHash);
        messageRecipients.add(recipient);
        messageTexts.add(message);

        // Then into the array that matches its flag
        if (flag.equalsIgnoreCase("Sent")) {

            sentMessages.add(message);
            sentRecipients.add(recipient);

        } else if (flag.equalsIgnoreCase("Stored")) {

            storedMessages.add(message);
            storedRecipients.add(recipient);
            storedIDs.add(messageID);
            storedHashes.add(messageHash);

        } else {

            disregardedMessages.add(message);
            disregardedRecipients.add(recipient);
        }
    }

    // Add a message to the arrays (the hash is created here)
    public static String addMessage(String messageID, int messageNumber,
            String recipient, String message, String flag) {

        String messageHash = createMessageHash(
                messageID, messageNumber, message);

        addMessageWithHash(messageID, messageHash,
                recipient, message, flag);

        return messageHash;
    }

    // Populate the arrays with the Part 3 test data
    public static void loadTestData() {

        clearAllData();

        addMessage("1000000001", 1, "+27834557896",
                "Did you get the cake?", "Sent");

        addMessage("1000000002", 2, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.",
                "Stored");

        addMessage("1000000003", 3, "+27834484567",
                "Yohoooo, I am at your gate.", "Disregard");

        addMessage("1000000004", 4, "0838884567",
                "It is dinner time!", "Sent");

        addMessage("1000000005", 5, "+27838884567",
                "Ok, I am leaving without you.", "Stored");
    }

    // Find the longest message in a list (returns "" if the list is empty)
    public static String getLongestMessage(List<String> messages) {

        String longest = "";

        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).length() > longest.length()) {
                longest = messages.get(i);
            }
        }

        return longest;
    }

    // Find the position of a text in a list, ignoring upper/lower case
    public static int indexOfIgnoreCase(List<String> list, String value) {

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equalsIgnoreCase(value.trim())) {
                return i;
            }
        }

        return -1;
    }

    // Search for a message ID (returns its position or -1)
    public static int findIndexByID(String messageID) {

        return indexOfIgnoreCase(messageIDs, messageID);
    }

    // Find all the messages sent or stored for a recipient
    public static List<String> searchByRecipient(String recipient) {

        List<String> results = new ArrayList<>();

        for (int i = 0; i < sentRecipients.size(); i++) {
            if (sentRecipients.get(i).equals(recipient.trim())) {
                results.add(sentMessages.get(i));
            }
        }

        for (int i = 0; i < storedRecipients.size(); i++) {
            if (storedRecipients.get(i).equals(recipient.trim())) {
                results.add(storedMessages.get(i));
            }
        }

        return results;
    }

    // Delete a stored message using its hash
    public static String deleteMessageByHash(String hash) {

        int position = indexOfIgnoreCase(storedHashes, hash);

        if (position == -1) {
            return "Message with hash " + hash.trim() + " not found.";
        }

        String deletedMessage = storedMessages.get(position);
        String deletedHash = storedHashes.get(position);

        // Remove from the stored arrays
        storedMessages.remove(position);
        storedRecipients.remove(position);
        storedIDs.remove(position);
        storedHashes.remove(position);

        // Remove from the arrays that hold every message
        int allPosition = indexOfIgnoreCase(messageHashes, deletedHash);

        if (allPosition != -1) {
            messageHashes.remove(allPosition);
            messageIDs.remove(allPosition);
            messageRecipients.remove(allPosition);
            messageTexts.remove(allPosition);
        }

        return "Message: \"" + deletedMessage + "\" successfully deleted.";
    }

    // ===== JSON METHODS =====

    // Make text safe to place inside JSON
    public static String jsonEscape(String text) {

        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Save stored messages in a JSON file
    public static void saveStoredMessages() {

        StringBuilder json = new StringBuilder();

        json.append("[\n");

        for (int i = 0; i < storedMessages.size(); i++) {

            json.append("  {\n");
            json.append("    \"messageID\": \"")
                    .append(jsonEscape(storedIDs.get(i))).append("\",\n");
            json.append("    \"messageHash\": \"")
                    .append(jsonEscape(storedHashes.get(i))).append("\",\n");
            json.append("    \"recipient\": \"")
                    .append(jsonEscape(storedRecipients.get(i))).append("\",\n");
            json.append("    \"message\": \"")
                    .append(jsonEscape(storedMessages.get(i))).append("\"\n");
            json.append("  }");

            if (i < storedMessages.size() - 1) {
                json.append(",");
            }

            json.append("\n");
        }

        json.append("]");

        try {
            Files.writeString(Path.of(jsonFile), json.toString());
            System.out.println("Stored messages saved to JSON file.");

        } catch (IOException e) {
            System.out.println("Could not save messages to the JSON file.");
        }
    }

    // Read the JSON text into a list of {id, hash, recipient, message}
    public static List<String[]> parseStoredJson(String content) {

        List<String[]> results = new ArrayList<>();
        List<String> current = null;

        int i = 0;

        while (i < content.length()) {

            char c = content.charAt(i);

            if (c == '{') {

                current = new ArrayList<>();

            } else if (c == '}') {

                if (current != null) {

                    // The strings in an object come as key, value, key, value
                    Map<String, String> fields = new HashMap<>();

                    for (int j = 0; j + 1 < current.size(); j += 2) {
                        fields.put(current.get(j), current.get(j + 1));
                    }

                    if (fields.containsKey("messageID")
                            && fields.containsKey("messageHash")
                            && fields.containsKey("recipient")
                            && fields.containsKey("message")) {

                        results.add(new String[]{
                            fields.get("messageID"),
                            fields.get("messageHash"),
                            fields.get("recipient"),
                            fields.get("message")});
                    }
                }

                current = null;

            } else if (c == '"') {

                StringBuilder text = new StringBuilder();
                i++;

                while (i < content.length() && content.charAt(i) != '"') {

                    char ch = content.charAt(i);

                    if (ch == '\\' && i + 1 < content.length()) {

                        i++;
                        char escaped = content.charAt(i);

                        if (escaped == 'n') {
                            text.append('\n');
                        } else if (escaped == 'r') {
                            text.append('\r');
                        } else if (escaped == 't') {
                            text.append('\t');
                        } else if (escaped == 'u'
                                && i + 4 < content.length()) {
                            text.append((char) Integer.parseInt(
                                    content.substring(i + 1, i + 5), 16));
                            i += 4;
                        } else {
                            text.append(escaped);
                        }

                    } else {
                        text.append(ch);
                    }

                    i++;
                }

                if (current != null) {
                    current.add(text.toString());
                }
            }

            i++;
        }

        return results;
    }

    // Read the JSON file into the stored messages arrays
    // Reference: Oracle (n.d.) Java Platform SE API: java.nio.file.Files
    // readString. (Replace with your own full reference in your report.)
    public static void loadStoredMessages() {

        Path path = Path.of(jsonFile);

        if (!Files.exists(path)) {
            return;
        }

        try {
            String content = Files.readString(path);

            List<String[]> loaded = parseStoredJson(content);

            for (String[] item : loaded) {
                addMessageWithHash(item[0], item[1], item[2], item[3], "Stored");
            }

            System.out.println(loaded.size()
                    + " stored message(s) loaded from the JSON file.");

        } catch (IOException e) {
            System.out.println("Could not read the JSON file.");
        }
    }

    // ===== DISPLAY METHODS =====

    // Display the message details
    public static void displayMessage(
            String messageID, String messageHash,
            String recipient, String message) {

        System.out.println("\n===== MESSAGE DETAILS =====");
        System.out.println("Message ID: " + messageID);
        System.out.println("Message Hash: " + messageHash);
        System.out.println("Recipient: " + recipient);
        System.out.println("Message: " + message);
    }

    // Create and process a message
    public static void sendMessages() {

        if (messagesEntered >= totalMessages) {

            System.out.println(
                    "You have already entered your maximum number of messages.");
            return;
        }

        System.out.println("\n===== SEND MESSAGE =====");

        // Enter recipient cellphone number
        String recipient;

        while (true) {

            System.out.print("Enter recipient number (+27): ");
            recipient = input.nextLine();

            if (checkCellphoneNumber(recipient)) {
                break;
            }

            System.out.println(
                    "Invalid number. Use +27 followed by 9 digits.");
        }

        // Enter message
        String message;

        while (true) {

            System.out.print("Enter your message: ");
            message = input.nextLine();

            // Message must contain at least one non-space character
            if (message.trim().isEmpty()) {
                System.out.println("Message cannot be empty.");
            } else if (message.length() >= 250) {
                System.out.println(
                        "Please enter a message of less than 250 characters.");
            } else {
                break;
            }
        }

        // Generate message information
        String messageID = generateMessageID();

        int messageNumber = messagesEntered + 1;

        String messageHash = createMessageHash(
                messageID, messageNumber, message);

        // Display message information before choosing an action
        displayMessage(messageID, messageHash, recipient, message);

        // Choose what to do with the message
        System.out.println("\nChoose an option:");
        System.out.println("1. Send Message");
        System.out.println("2. Disregard Message");
        System.out.println("3. Store Message to send later");

        int choice = readInteger("Enter your choice: ");

        while (choice < 1 || choice > 3) {
            System.out.println("Invalid choice. Choose 1, 2 or 3.");
            choice = readInteger("Enter your choice: ");
        }

        // Count each message that has been entered
        messagesEntered++;

        if (choice == 1) {

            addMessage(messageID, messageNumber, recipient, message, "Sent");

            messagesSent++;

            System.out.println("Message successfully sent");

        } else if (choice == 2) {

            addMessage(messageID, messageNumber, recipient, message, "Disregard");

            System.out.println("Press 0 to delete the message.");

            String deleteChoice = input.nextLine();

            if (deleteChoice.equals("0")) {
                System.out.println("Message disregarded.");
            } else {
                System.out.println("Message not sent or stored.");
            }

        } else {

            addMessage(messageID, messageNumber, recipient, message, "Stored");

            saveStoredMessages();

            System.out.println("Message successfully stored");
        }

        // Show how many messages have been entered
        System.out.println("Messages entered: "
                + messagesEntered + " of " + totalMessages);
    }

    // ===== STORED MESSAGES MENU =====

    // a. Display the sender and recipient of all stored messages
    public static void displayStoredSendersAndRecipients() {

        System.out.println("\n===== SENDER AND RECIPIENT =====");

        if (storedMessages.isEmpty()) {
            System.out.println("There are no stored messages.");
            return;
        }

        for (int i = 0; i < storedMessages.size(); i++) {
            System.out.println((i + 1) + ". Sender: " + registeredUsername
                    + " (" + registeredCellphone + ")"
                    + " | Recipient: " + storedRecipients.get(i));
        }
    }

    // b. Display the longest stored message
    public static void displayLongestStoredMessage() {

        System.out.println("\n===== LONGEST STORED MESSAGE =====");

        if (storedMessages.isEmpty()) {
            System.out.println("There are no stored messages.");
            return;
        }

        System.out.println(getLongestMessage(storedMessages));
    }

    // c. Search for a message ID and display the recipient and message
    public static void searchByIDMenu() {

        System.out.print("Enter the message ID: ");
        String id = input.nextLine();

        int position = findIndexByID(id);

        if (position == -1) {
            System.out.println("Message ID not found.");
        } else {
            System.out.println("Recipient: " + messageRecipients.get(position));
            System.out.println("Message: " + messageTexts.get(position));
        }
    }

    // d. Search for all the messages for a particular recipient
    public static void searchByRecipientMenu() {

        System.out.print("Enter the recipient number: ");
        String recipient = input.nextLine();

        List<String> results = searchByRecipient(recipient);

        if (results.isEmpty()) {
            System.out.println("No messages found for that recipient.");
        } else {
            System.out.println("\nMessages for " + recipient.trim() + ":");
            for (int i = 0; i < results.size(); i++) {
                System.out.println((i + 1) + ". " + results.get(i));
            }
        }
    }

    // e. Delete a message using the message hash
    public static void deleteByHashMenu() {

        System.out.print("Enter the message hash: ");
        String hash = input.nextLine();

        String result = deleteMessageByHash(hash);

        System.out.println(result);

        if (result.endsWith("successfully deleted.")) {
            saveStoredMessages();
        }
    }

    // f. Display a report of all the stored messages
    public static void displayStoredReport() {

        System.out.println("\n===== STORED MESSAGES REPORT =====");

        if (storedMessages.isEmpty()) {
            System.out.println("There are no stored messages.");
            return;
        }

        for (int i = 0; i < storedMessages.size(); i++) {
            System.out.println("\nMessage " + (i + 1));
            System.out.println("Sender: " + registeredUsername
                    + " (" + registeredCellphone + ")");
            System.out.println("Recipient: " + storedRecipients.get(i));
            System.out.println("Message ID: " + storedIDs.get(i));
            System.out.println("Message Hash: " + storedHashes.get(i));
            System.out.println("Message: " + storedMessages.get(i));
        }
    }

    // Stored messages menu
    public static void storedMessagesMenu() {

        int choice;

        do {

            System.out.println("\n===== STORED MESSAGES =====");
            System.out.println("1. Display sender and recipient of all stored messages");
            System.out.println("2. Display the longest stored message");
            System.out.println("3. Search for a message ID");
            System.out.println("4. Search for messages for a recipient");
            System.out.println("5. Delete a message using the message hash");
            System.out.println("6. Display a report of all stored messages");
            System.out.println("7. Back to main menu");

            choice = readInteger("Choose an option: ");

            switch (choice) {
                case 1:
                    displayStoredSendersAndRecipients();
                    break;
                case 2:
                    displayLongestStoredMessage();
                    break;
                case 3:
                    searchByIDMenu();
                    break;
                case 4:
                    searchByRecipientMenu();
                    break;
                case 5:
                    deleteByHashMenu();
                    break;
                case 6:
                    displayStoredReport();
                    break;
                case 7:
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1 to 7.");
            }

        } while (choice != 7);
    }

    // Display the menu
    public static void displayMenu() {

        System.out.println("\n===== QUICKCHAT =====");
        System.out.println("1. Send Messages");
        System.out.println("2. Show Recently Sent Messages");
        System.out.println("3. Quit");
        System.out.println("4. Stored Messages");
    }

    // Read an integer safely
    public static int readInteger(String prompt) {

        while (true) {

            System.out.print(prompt);

            try {
                int number = Integer.parseInt(input.nextLine().trim());
                return number;

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    // Main method
    public static void main(String[] args) {

        System.out.println("Welcome to QuickChat.");

        // Register first
        registerUser();

        // Login before allowing access to messaging
        boolean loginSuccessful = performLogin();

        if (!loginSuccessful) {
            input.close();
            return;
        }

        // Read the JSON file into the stored messages arrays
        loadStoredMessages();

        // Ask how many messages the user wants to enter
        while (true) {

            totalMessages = readInteger(
                    "How many messages would you like to enter? ");

            if (totalMessages > 0) {
                break;
            }

            System.out.println("Enter a number greater than zero.");
        }

        int option;

        // Continue until the user chooses Quit
        do {

            displayMenu();

            option = readInteger("Choose an option: ");

            if (option == 1) {

                if (messagesEntered < totalMessages) {
                    sendMessages();
                } else {
                    System.out.println(
                            "You have reached your message limit.");
                }

            } else if (option == 2) {

                // This feature is still in development
                System.out.println("Coming Soon.");

            } else if (option == 3) {

                System.out.println("\n===== QUICKCHAT SUMMARY =====");
                System.out.println("Messages entered: " + messagesEntered);
                System.out.println("Messages successfully sent: " + messagesSent);
                System.out.println("Messages stored: " + storedMessages.size());

                System.out.println("Thank you for using QuickChat.");

            } else if (option == 4) {

                storedMessagesMenu();

            } else {

                System.out.println(
                        "Invalid option. Please choose 1, 2, 3 or 4.");
            }

        } while (option != 3);

        input.close();
    }
}
