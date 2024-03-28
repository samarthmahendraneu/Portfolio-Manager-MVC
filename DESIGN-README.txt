# Portfolio Management System Design Overview

The Portfolio Management System is a sophisticated Java application tailored for users aiming to meticulously manage their stock portfolios. By adhering to the Model-View-Controller (MVC) architectural pattern, the system ensures a clear separation of concerns, thus fostering maintainability and scalability. Users are equipped with comprehensive functionalities including the creation and examination of portfolios, value calculations, as well as robust file management capabilities for portfolio data persistence.

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

### Model

#### Portfolio and Tradable
- Core entities representing stock portfolios and tradable items (stocks) within those portfolios.
- Offer methods for portfolio manipulation such as adding or selling stocks, calculating portfolio values, and maintaining a collection of stock data.

### Controller

#### PortfolioController and PortfolioMenuController
- Act as the intermediaries between the model and view, handling business logic for portfolio operations and user interactions through a menu-driven interface.
- Utilize services provided by `PortfolioServiceInterface` and `StockServiceInterface` to perform operations and fetch stock data.

### Transactions Package

The `Transactions` package within the `Model` is designed to encapsulate the transactional details of stock trades, including both purchases and sales. It defines a base interface and concrete classes to represent transaction-specific information.

### Service

#### PortfolioService and StockService
- Provide domain-specific services related to portfolio management and stock data retrieval.
- Interact with external data sources and caches to fetch and store stock information, enhancing system performance and reducing dependency on external services.

### View
- Manages all user interface and display logic, presenting information to users and collecting user inputs.
- Utilizes `View` components to display errors, portfolio details, and success messages.

### Enhancements
- **Date Handling**: With `DateUtils`, the system now includes advanced date manipulation capabilities, supporting dynamic resolution adjustment and end-of-period calculations to cater to various reporting needs.
- **File Operations**: Through the `FileIO` interface, the system has enhanced file management functionalities, allowing users to persist portfolio data across sessions and facilitating easy data exchange.

By incorporating these new functionalities, the Portfolio Management System not only maintains its robust portfolio management features but also introduces improved date handling and file operation capabilities, further enriching the user experience and system efficiency.
