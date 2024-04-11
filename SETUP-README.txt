# SETUP-README.txt

## Running the Program from the JAR File

To run the Portfolio Management System from the JAR file, follow these steps:

1. Ensure that the JAR file is located in a directory that has write permissions, as the program may need to save and load files.
2. Open a terminal or command prompt window.
3. Navigate to the directory where the JAR file is located.
4. Run the command `java -jar PortfolioManagementSystem.jar` to start the program.

## Choosing an Interface
Upon launching the application, you're prompted to choose between the GUI and the textual interface:

Select the interface type:
1. Graphical User Interface (GUI)
2. Textual User Interface (Console)
Enter your choice (1/2):

For GUI: Enter 1. The GUI version provides a visual interface for interacting with the portfolio management functionalities.

For Textual Interface: Enter 2. The console version allows for text-based commands and outputs, suitable for those who prefer a command-line environment.
## Creating and Querying Portfolios


### Creating a Portfolio with 3 Different Stocks

1. Select option 1 from the main menu to create a new portfolio.
2. Enter a unique name for the portfolio when prompted.
3. Add the first stock:
   - Enter the stock symbol.
   - Enter the quantity (whole number).
   - Enter the purchase date in the format YYYY-MM-DD (must be a past weekday).
4. Repeat the process to add the second and third stocks.
5. Press `q` to finish adding stocks to the portfolio.

### Creating a Second Portfolio with 2 Different Stocks

1. Select option 1 from the main menu to create a new portfolio.
2. Enter a unique name for the second portfolio when prompted.
3. Add the first stock following the same steps as above.
4. Add the second stock.
5. Press `q` to finish adding stocks to the portfolio.

### Querying Portfolio Value on a Specific Date

1. Select option 3 from the main menu to calculate the portfolio value.
2. Enter the name of the portfolio you wish to query.
3. Enter the date (YYYY-MM-DD) for which you want to calculate the value.
4. The program will display the value of the portfolio on the specified date.

## Supported Stocks and Dates

Include a list of supported stock symbols and the date ranges for which their values can be determined. If your program has restrictions on the available data, specify them here. For example:

- AAPL (Apple Inc.) - Available from 2010-01-01 to present
- MSFT (Microsoft Corporation) - Available from 2010-01-01 to present
- GOOGL (Alphabet Inc.) - Available from 2010-01-01 to present

Note: The actual list of supported stocks and date ranges will depend on the data provided by the Alpha Vantage API and any limitations you have set within your program.
