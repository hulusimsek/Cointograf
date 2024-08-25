# CryptoApp

CryptoApp is a mobile application that allows users to view and search for various cryptocurrency details. Designed for those who want to keep track of the latest price changes and details in the cryptocurrency market.

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [Architecture](#architecture)
- [Technologies and Libraries](#technologies-and-libraries)
- [Contributing](#contributing)
- [License](#license)

## Features

- **Cryptocurrency List**: A list of popular cryptocurrencies.
- **Detailed View**: Detailed price and market information of selected cryptocurrencies.
- **Search Function**: Allows users to quickly find specific cryptocurrencies.
- **Past Searches**: View and access previous cryptocurrency searches.

## Requirements

- Android Studio Arctic Fox or higher
- Kotlin 1.5 or higher
- Minimum Android SDK 21

## Installation

Clone the repository to your local machine:

```bash
git clone https://github.com/yourusername/cryptoapp.git
```
## Usage

Once the application is started, you can:

1. **Browse the List of Cryptocurrencies**: View the list of popular cryptocurrencies.
2. **View Details**: Access detailed price and market information for selected cryptocurrencies.
3. **Perform Searches**: Search for specific cryptocurrencies.
4. **View Past Searches**: Access your previous cryptocurrency searches.

## Architecture

CryptoApp is structured following the principles of Clean Architecture. The application consists of the following layers:

- **Data Layer**:
  - **Dependency Injection**: Managed using Hilt.
  - **Remote**: API calls and remote data sources.
  - **Repository**: Manages data and applies business logic.

- **Domain Layer**:
  - **Model**: Core data structures of the application.
  - **Repository (Interface)**: Interfaces for data access.
  - **Use Case**: Classes that handle business rules and application logic.

- **Presentation Layer**:
  - **Views**: User interface components and views.

## Technologies and Libraries

- **Kotlin**: A modern and safe programming language.
- **Android Jetpack**: Components for modern Android app development.
- **Hilt**: A library for dependency injection.
- **Kotlin Coroutines**: For asynchronous programming.
- **Flow**: For managing data streams.
- **Retrofit**: For API calls.
- **Room**: For local data storage.

## Contributing

If you want to contribute, please follow these steps:

1. **Fork this repository**.
2. **Create a new feature branch**:
    ```bash
    git checkout -b feature/YourFeature
    ```
3. **Make your changes and commit**:
    ```bash
    git commit -am 'Add new feature'
    ```
4. **Push your branch to remote**:
    ```bash
    git push origin feature/YourFeature
    ```
5. **Create a pull request**.

## License

This project is licensed under the [MIT License](LICENSE).
