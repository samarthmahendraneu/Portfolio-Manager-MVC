# Portfolio Management System Design Overview

The Portfolio Management System is a Java-based application that allows users to manage their stock portfolios. It follows the Model-View-Controller (MVC) design pattern to separate concerns and enhance maintainability. The system provides functionalities such as creating new portfolios, examining and calculating portfolio values, and saving or loading portfolios from files.

## Components

### Main
- Entry point of the application.
- Initializes the system and starts the user interface.

### Payload
- A data structure used to encapsulate the result of an operation, including any data, messages, or error states.
- Variables:
  - `data`: Object
  - `message`: String
  - `error`: boolean

### Portfolio
- Represents a stock portfolio with a name and a list of stocks.
- Provides methods to add stocks to the portfolio.
- Variables:
  - `name`: String
  - `stocks`: List<Tradable>

### PortfolioController
- Handles the business logic for portfolio operations.
- Interacts with the `PortfolioServiceInterface` to perform operations like creating portfolios, adding stocks, and calculating values.
- Variables:
  - `portfolioService`: PortfolioServiceInterface

### PortfolioControllerInterface
- An interface defining the operations that a portfolio controller must support.

### PortfolioInterface
- An interface defining the operations that a portfolio must support.

### PortfolioMenuController
- Manages the interaction between user inputs and portfolio operations through a menu interface.
- Implements the `PortfolioMenuControllerInterface`.
- Variables:
  - `portfolioController`: PortfolioControllerInterface
  - `view`: View

### PortfolioMenuControllerInterface
- An interface defining the operations that the menu controller must support.

### PortfolioService
- Provides services related to portfolio management, such as listing portfolio names, checking if a portfolio exists, and saving/loading portfolios.
- Interacts with the `StockServiceInterface` to retrieve stock data.
- Variables:
  - `numberOfPortfolios`: int

### PortfolioServiceInterface
- An interface defining the services that a portfolio service must provide.

### Stock
- Represents an individual stock within a portfolio, including details like symbol, quantity, purchase price, and date.
- Variables:
  - `symbol`: String
  - `quantity`: int
  - `purchasePrice`: BigDecimal
  - `purchaseDate`: LocalDate

### StockDataCache
- Caches stock data to reduce the need for repeated API calls.

### StockInfo
- Encapsulates detailed information about a stock, such as open, high, low, close prices, and volume for a specific date.

### StockService
- Responsible for fetching stock data from external sources, such as an API.
- Parses and caches data to improve performance and reduce dependency on external services.

### StockServiceInterface
- An interface defining the operations that a stock service must support.

### Tradable
- An interface representing a tradable item, such as a stock, with properties like quantity, purchase price, and date.

### View
- Responsible for displaying information to the user.
- Variables:
  - `displayError(String, Appendable)`: void
  - `displayPortfolioDetails(String, List<Tradable>, Appendable)`: void
  - `displaySaveSuccess(String, Appendable)`: void
  - `displayMainMenu(Appendable)`: void
  - `displayPortfolioValue(String, String, String, Appendable)`: void
  - `displayAvailablePortfolios(List<String>, Appendable)`: void
  - `displayLoadSuccess(Appendable)`: void
