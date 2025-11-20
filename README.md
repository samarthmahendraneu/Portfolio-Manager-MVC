# Portfolio Management System

The Portfolio Management System is a comprehensive Java-based application designed for managing and analyzing stock portfolios. It provides a wide range of functionalities, including portfolio creation, stock transactions recording, portfolio examination, and performance analysis.

## Features

- **Create a Portfolio**: Users can create multiple portfolios to manage different sets of stock investments.
- **Examine a Portfolio**: view detailed information about the stocks in a portfolio, including current holdings and transaction history.
- **Calculate Portfolio Value**: Determine the total value of a portfolio on a specified date.
- **Add Stock to Portfolio**: Purchase stocks to add to a portfolio by specifying the stock symbol, quantity, and purchase date.
- **Sell Stock from Portfolio**: Sell stocks from a portfolio, adjusting the portfolio's holdings accordingly.
- **Calculate Investment**: Analyze the total amount of money invested in a portfolio up to a specified date.
- **Save/Load Portfolio**: Portfolios can be saved to a file for later retrieval, allowing for persistent management over time.
- **Graph**: Plot a bar chart illustrating the performance of a stock or portfolio over a specified timeframe.
- **Inspect Stock Performance**: Check if a stock gained or lost on a specific day.
- **Calculate X-Day Moving Average**: Compute the moving average over a specified number of days for a given stock.
- **Crossover Days**: Identify the days within a specified period when a stock's closing price crossed over its opening price.
- **Moving Crossover Days**: Find the days where the stock's closing price crossed over its moving average within a specified period.
- **Dollar Cost Averaging**: Implement a dollar-cost averaging investment strategy by investing a fixed amount periodically in a portfolio.
- **Value Based Investment**: Invest a fixed amount in a portfolio containing multiple stocks with specified weights for each stock.

## Getting Started

### Menu Options

1. **Create a new portfolio**
2. **Examine a portfolio**
3. **Calculate portfolio value**
4. **Add Stock to Portfolio**
5. **Sell Stock from Portfolio**
6. **Calculate Investment**
7. **Save portfolio**
8. **Load portfolio**
9. **Graph**
10. **Inspect Stock performance**
11. **Calculate X-Day Moving Average**
12. **Crossover Days**
13. **Moving Crossover Days**
14. **Dollar Cost Averaging**
15. **Value Based Investment**
14. **Exit**

Select an option from the menu to perform the corresponding action. The system is designed to be intuitive and user-friendly, guiding you through each process step by step.

## Usage Example

After launching the application, you will be presented with the main menu. From here, you can choose to create a new portfolio, examine existing portfolios, add or sell stocks, and perform various analyses on your stock holdings.

### Create a New Portfolio

1. Select "1. Create a new portfolio" from the main menu.
2. Enter a name for your new portfolio when prompted.

### Add Stock to a Portfolio

1. Select "4. Add Stock to Portfolio" from the main menu.
2. Follow the prompts to specify the stock symbol, quantity, and purchase date.

### Inspect Stock Performance

1. Select "10. Inspect Stock performance" from the main menu.
2. Enter the stock symbol and the date you wish to inspect.

### Value Based Investment

- **Fixed Amount Investment**: Invest a fixed amount into an existing portfolio containing multiple stocks with equal weights for each stock.


### Dollar-Cost Averaging Investment Strategy

- **Fixed Amount Investment**: You can now invest a fixed amount into an existing portfolio containing multiple stocks with specified weights for each stock. This allows for the purchase of fractional shares based on the specified amount. For example, create a FANG portfolio (Facebook, Apple, Netflix, Google) and invest $2000 with specified weights for each stock.

- **Start-to-Finish Dollar-Cost Averaging Operation**: Implement "start-to-finish" dollar-cost averaging as a single operation. This feature enables you to create a portfolio of specified stocks and invest a fixed amount in the portfolio periodically (e.g., every 30 days) starting from a specific date until an end date, or indefinitely if the end date is not specified. Use the same specified weights for each transaction.

- **Handling Investments on Holidays**: For periodic investment strategies, if a scheduled investment day falls on a holiday, the program will automatically select the next available day for investment, using end-of-day prices for stock purchases.

### Future Extension to Other Long-Term Investment Strategies

- The design supports the addition of other high-level, time-based long-term investment strategies in the future, making the system flexible and scalable for further enhancements.


Continue exploring other options from the menu to fully utilize the capabilities of the Portfolio Management System.
## Installation

1. Ensure you have Java JDK 8 or higher installed on your system.
2. Clone the repository to your local machine:

