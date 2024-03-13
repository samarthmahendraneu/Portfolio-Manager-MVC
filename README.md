# README Documentation for Portfolio Management System

## Overview
The Portfolio Management System is a Java-based application designed to manage stock portfolios. 
It allows users to create new portfolios, add stocks to them, examine portfolio details, calculate portfolio values, and save or load portfolios from files. 
This Program was built on the Model-View-Controller (MVC) architecture, ensuring a clear separation of functionalities

## Getting Started
### Prerequisites
- Java Development Kit (JDK) 8 or higher
- An IDE that supports Java.
- Internet connection for stock data retrieval via Alpha Vantage API

### Installation
1. Clone the repository or download the source code.
2. Open the project in your preferred Java IDE.
3. Ensure that the JDK is correctly set up in your project settings.
4. Obtain a free API key from Alpha Vantage for stock data retrieval.
5. Insert your API key into the designated place in the code (if applicable).

### Running the Application
- Compile the Java files in the project.
- Run the `Main` class to start the application.
- Follow the on-screen prompts to interact with the system.

## Features
- **Create New Portfolio:** Users can create new portfolios by providing a unique name. Stocks can then be added to the portfolio by specifying the stock symbol, quantity, and purchase date.
- **Examine Portfolio:** Users can view the details of a specific portfolio, including the stocks it contains, their quantities, purchase prices, and dates.
- **Calculate Portfolio Value:** The system can calculate the total value of a portfolio on a given date, taking into account the current market prices of the stocks.
- **Save Portfolio:** Portfolios can be saved to a specified file path, allowing for persistence and retrieval.
- **Load Portfolio:** Previously saved portfolios can be loaded from a file path.

## Design Considerations
- **Model-View-Controller (MVC):** This architecture was chosen to separate the application logic (model), the user interface (view), and the control flow (controller), facilitating maintainability and scalability.
- **Error Handling:** The system is designed to gracefully handle errors, such as invalid user inputs or issues with stock data retrieval, without crashing.
- **Stock Data Retrieval:** The application integrates with the Alpha Vantage API to retrieve stock data. A mechanism is in place to handle API rate limits and ensure the application remains functional even without an internet connection, by using cached or user-provided data.

## Testing
Comprehensive testing has been conducted to ensure the reliability of the system. This includes unit tests for individual components and integration tests for the overall application. Users are encouraged to report any bugs or issues for continuous improvement.
