# WeatherSync

WeatherSync is a weather forecasting app that allows users to get current weather information for their chosen location. The app fetches data from the OpenWeatherMap API and presents it in a clean and user-friendly interface using Jetpack Compose. It follows the **MVVM (Model-View-ViewModel)** architecture for maintainable code.

## Features

- Get current weather data based on the user's location or manually searched city.
- Displays detailed weather information including temperature, humidity, wind speed, and weather description.
- Dynamic UI with Jetpack Compose for a smooth and responsive experience.
- Favorites feature: Add favorite locations to quickly check the weather in multiple cities.
- Real-time data fetched from the OpenWeatherMap API.

## Tech Stack

- **Kotlin**: Programming language used for the app.
- **Jetpack Compose**: UI toolkit for building native Android UIs.
- **MVVM Architecture**: Ensures separation of concerns for better scalability and testability.
- **OpenWeatherMap API**: Provides weather data.
- **Coroutines**: For asynchronous programming and smooth background tasks.
- **Unit Testing**: Ensures core functionality works as expected.

## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/YoussefFayad14/WeatherSync.git
    ```
   
2. Navigate to the project directory:
    ```bash
    cd WeatherSync
    ```

3. Open the project in Android Studio.

4. Sync the Gradle files to download all dependencies.

5. Add your OpenWeatherMap API key in the `strings.xml` file:
    ```xml
    <string name="open_weather_api_key">YOUR_API_KEY</string>
    ```

6. Build and run the app on an emulator or physical device.

## Usage

1. Launch the app and enter a city name to view its weather details.
2. Add cities to your favorites list for quick access.
3. The app will display current weather details including temperature, wind speed, and humidity.


## Screenshots
![photo_1_2025-04-03_01-36-49](https://github.com/user-attachments/assets/28f48109-f1d6-46d5-a2ad-34bf5ac86e35)
![photo_2_2025-04-03_01-36-49](https://github.com/user-attachments/assets/645d97c0-6514-487b-84b7-b927736451b7)
![photo_3_2025-04-03_01-36-49](https://github.com/user-attachments/assets/f6036af7-1128-4614-b855-b795e0d58e7a)
![photo_4_2025-04-03_01-36-49](https://github.com/user-attachments/assets/0068612f-6d09-4cc6-a635-17b28cff8a9f)
![photo_5_2025-04-03_01-36-49](https://github.com/user-attachments/assets/99009e53-4493-49ea-9228-6bbc918bc12d)
![photo_6_2025-04-03_01-36-49](https://github.com/user-attachments/assets/5e1e0eb0-111f-446c-9fe7-8fab501bb484)
![photo_7_2025-04-03_01-36-49](https://github.com/user-attachments/assets/2135fbae-3e9b-41f3-9f6d-eb9d2b0d65bd)



## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-name`).
3. Commit your changes (`git commit -am 'Add new feature'`).
4. Push to the branch (`git push origin feature-name`).
5. Create a new Pull Request.

## License

WeatherSync is open-source software licensed.
