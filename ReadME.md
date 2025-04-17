# Split(NotSo)Wise :money_with_wings:

Create a client-server application with functionality similar to [Splitwise](https://www.splitwise.com/).

Splitwise aims to simplify bill splitting between friends and roommates and reduce arguments like "I'm the only one buying beer in this dorm."

## Task

Create a console-based client application that accepts user commands, sends them to the server for processing, receives the response, and displays it to the user in a readable format.

*Note*: The commands and outputs shown here are just examples. You are free to rename and reformat them. The only requirement is that they should be intuitive. For user convenience, you may implement a `help` command.

### Functional Requirements

- User registration with username and password; Registered users are stored in a file on the server, which serves as a database. When the server is restarted, it can load the registered users into memory.

- Login;
- A registered user can:
    - Add already registered users to their Friend List using their usernames. For example:
        ```bash
        $ add-friend <username>
        ```
    - Create a group consisting of several already registered users:
        ```bash
        $ create-group <group_name> <username> <username> ... <username>
        ```
        Groups are created by a user and include three or more users. You can think of “friend” relationships as a group of two people.

    - Add an amount paid by them to the obligations of:
        - Another user from their friend list:
        ```bash
        $ split <amount> <username> <reason_for_payment>
        ```
        - A group they are part of:
        ```bash
        $ split-group <amount> <group_name> <reason_for_payment>
        ```

    - Get their status – amounts they owe to friends and groups, and amounts owed to them. For example:
        ```bash
        $ get-status
        Friends:
        * Pavel Petrov (pavel97): Owes you 10 BGN

        Groups
        * 8thDecember
        - Pavel Petrov (pavel97): Owes you 25 BGN
        - Hristo Hristov (ico_h): Owes you 25 BGN
        - Harry Georgiev (harryharry): You owe 5 BGN
        ```
        You may display all groups and friends or only those with unsettled debts.

- A newly added amount is split equally among all members of a group or split in half when shared with a friend.

- When user A owes user B money, the debt can only be "cleared" (with an appropriate command) by user B:
    ```bash
    $ payed <amount> <username>
    ```
    Example:
    ```bash
    $ get-status
    Friends:
    * Pavel Petrov (pavel97): Owes you 10 BGN
    * Hristo Hristov (ico_h): You owe 5 BGN

    $ payed 5 pavel97
    Pavel Petrov (pavel97) payed you 5 BGN.
    Current status: Owes you 5 BGN

    $ get-status
    Friends:
    * Pavel Petrov (pavel97): Owes you 5 BGN
    * Hristo Hristov (ico_h): You owe 5 BGN
    ```

- When user A owes user B money (e.g., 5 BGN) but before repaying adds another amount they paid (e.g., 5 BGN), the debts are recalculated (A now owes 2.50 BGN, B is owed 2.50 BGN).
    ```bash
    $ get-status
    Friends:
    * Pavel Petrov (pavel97): Owes you 10 BGN
    * Hristo Hristov (ico_h): You owe 5 BGN

    $ split 5 ico_h limes and oranges
    Splitted 5 BGN between you and Hristo Hristov.
    Current status: You owe 2.50 BGN

    $ get-status
    Friends:
    * Pavel Petrov (pavel97): Owes you 5 BGN
    * Hristo Hristov (ico_h): You owe 2.50 BGN
    ```

- Each time a user logs into the system, they receive notifications if friends have added amounts or cleared debts.
For example:
    ```bash
    $ login alex alexslongpassword
    Successful login!
    No notifications to show.
    ```
    or
    ```bash
    $ login alex alexslongpassword
    Successful login!
    *** Notifications ***
    Friends:
    Misho approved your payment 10 BGN [Mixtape beers].

    Groups:
    * Roomates:
    You owe Gery 20 BGN [Tanya Bday Present].

    * Family:
    You owe Alex 150 BGN [Surprise trip for mom and dad]
    ```

- The user can view a history of their payments. This history is saved in a file on the server.

- (***Extra Task***) The server provides currency conversion functionality. The default currency is Bulgarian lev (BGN), but the user can switch it at any time using an appropriate command (e.g., `switch-currency EUR`). All debts are then converted to the selected currency.

    Use an HTTP request to a public API (e.g., https://exchangeratesapi.io/) to get the current exchange rates and process the response.

### Non-functional Requirements

- The server must support multiple users concurrently.

## Error Messages

If the program is misused, appropriate error messages must be shown to the user.

In case of a program error, the user should only see a relevant message. Technical error information and stack traces should be logged to a file – no specific format is required.

For example, instead of displaying "IO exception occurred: connection reset" during a login failure due to a network issue, it would be better to show "Unable to connect to the server. Try again later or contact the administrator by providing the logs in <path_to_logs_file>".

When a server-side error occurs, a suitable message is shown in the console and logged, along with additional info such as which user triggered the error (if applicable) and the full stack trace.

## Clarifications

- As you may guess, this is not a banking system, and we are not concerned with how the payments themselves happen.

    Imagine Anna and Eva are roommates. Anna pays the rent for both and records it in the app. Then Eva gives Anna the money, and Anna records that Eva repaid her. Eva cannot record it herself.

- User input validation is mandatory – handle all scenarios you can think of, like null, wrong formatting, invalid data types, etc.

- The example commands and outputs are for guidance only. You are free to use other commands if they make more sense to you.

- Any additional functionalities you think of are welcome.


