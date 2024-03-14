# Portfolio Management System Documentation

## Introduction
Welcome to the Portfolio Management System, a Java-based tool crafted for the seamless management of stock portfolios. This application empowers users to craft new portfolios, incorporate stocks, delve into portfolio specifics, evaluate portfolio worth, and preserve or retrieve portfolios from files. Developed with the Model-View-Controller (MVC) architecture at its core, it guarantees a neat delineation of roles and functionalities.

## Getting Started

### Before You Begin
- You'll need the Java Development Kit (JDK), version 8 or above.
- A Java-compatible Integrated Development Environment (IDE).
- An active internet connection for fetching stock data through the Alpha Vantage API.

### Setting Up
1. Either clone the repository or download the source files.
2. Launch your chosen Java IDE and open the project.
3. Confirm that your project settings are correctly aligned with the JDK setup.
4. Secure a complimentary API key from Alpha Vantage for stock data access.
5. Embed your API key in the specified location within the code (if required).

### Launching the Application
- Compile the Java files within the project.
- Execute the `Main` class to initiate the application.
- Navigate the system via the on-screen instructions to interact with it.

## Key Features
- **Portfolio Creation:** Furnish a distinctive name to birth a new portfolio. Augment this portfolio by adding stocks, detailing the stock symbol, quantity, and acquisition date.
- **Portfolio Exploration:** Peruse the intricacies of a chosen portfolio, including its stock components, quantities, acquisition prices, and dates.
- **Value Assessment:** Ascertain the aggregate value of a portfolio for a specified date, reflecting the prevailing market prices of the included stocks.
- **Portfolio Preservation:** Portfolios can be earmarked to a chosen file path for enduring storage and future access.
- **Portfolio Retrieval:** Revisit and load portfolios previously stored at a file path.

## Design Insights
- **Model-View-Controller (MVC) Approach:** This structural design was selected to segregate the application logic (model), the user interface (view), and the flow control (controller), promoting ease of maintenance and expandability.
- **Error Management:** The framework is engineered to elegantly tackle errors, such as inaccurate user inputs or complications in stock data fetching, ensuring the application's stability.
- **Stock Data Fetching:** Integration with the Alpha Vantage API facilitates stock data acquisition. A strategy is in place to manage API rate limits and guarantee operational continuity even in the absence of an internet connection, by leaning on cached or manually inputted data.

## Assurance and Support
Thorough testing underpins the system's dependability, encompassing both component-specific unit tests and comprehensive integration tests for the entire application. We welcome users to report any anomalies or concerns, aiding us in perpetually enhancing the system.
