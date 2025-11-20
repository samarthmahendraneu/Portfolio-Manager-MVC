# Portfolio Management System Design Overview

The Portfolio Management System is a sophisticated Java application tailored for users aiming to meticulously manage their stock portfolios. By adhering to the model-view-Controller (MVC) architectural pattern, the system ensures a clear separation of concerns, thus fostering maintainability and scalability. Users are equipped with comprehensive functionalities including the creation and examination of portfolios, value calculations, as well as robust file management capabilities for portfolio data persistence.

## Components

### Utilities

#### DateUtils
- A utility class providing methods to calculate dates based on specific business rules, such as determining the last working day of a month or year, adjusting for weekends, and deciding data resolution based on the date range.
- Enhances the system's capability to handle date-based calculations more effectively, supporting features like historical data analysis and reporting.

### File Management

#### FileIO
- An interface defining methods for reading from and writing to files, encapsulating the file operation logic.
- Enables the Portfolio Management System to offer functionalities for saving portfolios to and loading portfolios from external files, thus allowing data persistence and easy data sharing.

### Main
- Serves as the application's entry point, initiating the system setup and triggering the user interface.

### model

#### Portfolio and Tradable
- Core entities representing stock portfolios and tradable items (stocks) within those portfolios.
- Offer methods for portfolio manipulation such as adding or selling stocks, calculating portfolio values, and maintaining a collection of stock data.

### Controller

#### PortfolioController and PortfolioMenuController
- Act as the intermediaries between the model and view, handling business logic for portfolio operations and user interactions through a menu-driven interface.
- Utilize services provided by `PortfolioServiceInterface` and `StockServiceInterface` to perform operations and fetch stock data.

### Transactions Package

The `Transactions` package within the `model` is designed to encapsulate the transactional details of stock trades, including both purchases and sales. It defines a base interface and concrete classes to represent transaction-specific information.

### Service

#### PortfolioService and StockService
- Provide domain-specific services related to portfolio management and stock data retrieval.
- Interact with external data sources and caches to fetch and store stock information, enhancing system performance and reducing dependency on external services.

### view
- Manages all user interface and display logic, presenting information to users and collecting user inputs.
- Utilizes `view` components to display errors, portfolio details, and success messages.

## Unified Controller Design

The cornerstone of our system's design is the unified controller,
capable of interacting with both the GUI and textual views. This controller is designed to be agnostic of the view type, enabling a dynamic interaction model where the view can be switched without altering the controller logic.

### Implementation Details

- **UniviedViewInterface**: To facilitate a unified controller design, we introduced an `IView` interface, which both GUI and textual views implement. This interface defines common methods for displaying messages, requesting input, and handling user actions, ensuring that the controller can interact with either view through a unified set of operations.


- **Controller Initialization**: Upon startup, the application determines the preferred view type based on user input or configuration settings. The chosen view is then instantiated and passed to the controller along with the model. This flexibility allows users to select their preferred interaction mode without changing the underlying system behavior.


- **Listener Setup**: The controller sets up listeners for various user actions (e.g., creating a new portfolio, examining a portfolio, etc.). These listeners are defined within the `GUIView` interface and implemented differently in the GUI and textual views, allowing for appropriate handling of user input according to the view type.

### Advantages

- **Flexibility**: Users can choose between a GUI or a textual interface, catering to different preferences and usage contexts.
- **Maintainability**: Changes to the controller logic affect both views simultaneously, reducing redundancy and the potential for inconsistencies.
- **Scalability**: Adding new features or views is simplified, as the unified controller design abstracts away the specifics of user interaction.

# Stock Data Cache Design

## Overview

The `StockDataCache` class is a crucial component of the stock management system designed to cache stock data and reduce the number of API calls required to fetch stock information. This class provides a fast and efficient way to access stock data by storing it in memory.

## Key Features

- **Cache Stock Data**: Allows adding stock data to an in-memory cache for quick retrieval. This reduces the dependency on external API calls for frequently accessed data.

- **Fetch Stock Data**: Enables the retrieval of cached stock data by stock symbol and date, providing immediate access to stock information without the need for external requests.

- **Data Persistence**: Supports saving and loading the cache to and from a file. This feature is essential for persisting the cache between application runs, thereby reducing API usage over time.

- **Data Integrity**: Implements checks to determine if specific stock data is already available in the cache before making external API calls.

## Implementation Details

- The cache is implemented as a `Map<String, Map<LocalDate, StockInfo>>` where the key is the stock symbol and the value is another map. The nested map's key is the date for which stock data is available, and the value is the `StockInfo` object containing the stock data.

- **Adding Stock Data**: The `addStockData` method allows adding new stock data to the cache. It checks if the symbol exists in the cache; if not, it creates a new entry. It then adds the stock data to the symbol's map.

- **Fetching Stock Data**: The `getStockData` method retrieves stock data from the cache. It returns null if the data is not found, indicating that an API call may be necessary to fetch the data.

- **Saving and Loading Cache**: The `saveCacheToFile` and `loadCacheFromFile` methods handle the persistence of the cache. They use simple file I/O operations to read from and write to a CSV file. This file stores stock data across application sessions.
# Tradable Interface Design

## Overview

The `Tradable` interface defines the contract for tradable assets within the stock management system. It encapsulates the common functionalities and attributes of assets that can be bought or sold in the financial market, such as stocks and bonds.

## Key Methods

- **getSymbol**: Returns the symbol representing the tradable asset in the financial markets.

- **getQuantity**: Returns the current quantity owned of the tradable asset.

- **getQuantity(LocalDate date)**: Returns the quantity of the tradable asset on a given date, accounting for any buy or sell transactions up to that date.

- **sell**: Updates the quantity of the tradable asset by subtracting the amount sold and records the selling price and date of the transaction.

- **buy**: Increases the quantity of the tradable asset by adding the amount bought and records the purchase price and date of the transaction.

- **calculateInvestment**: Calculates the total money invested in the tradable asset up to a specific date.

- **calculateValue**: Calculates the current market value of the tradable asset on a given date, utilizing a `StockServiceInterface` to fetch the latest stock prices.

- **getActivityLog**: Retrieves a log of all buy and sell transactions (activity) of the tradable asset, mapped by date.

- **toString**: Provides a string representation of the tradable asset, typically including its symbol, current quantity, and other relevant information.

## Usage Scenario

Implementations of the `Tradable` interface allow for the creation of various types of tradable assets within the system. Each asset can maintain a history of transactions and can calculate its own investment and current value based on market data provided by a `StockServiceInterface`.

### Enhancements
- **Date Handling**: With `DateUtils`, the system now includes advanced date manipulation capabilities, supporting dynamic resolution adjustment and end-of-period calculations to cater to various reporting needs.
- **File Operations**: Through the `FileIO` interface, the system has enhanced file management functionalities, allowing users to persist portfolio data across sessions and facilitating easy data exchange.

By incorporating these new functionalities, the Portfolio Management System not only maintains its robust portfolio management features but also introduces improved date handling and file operation capabilities, further enriching the user experience and system efficiency.
