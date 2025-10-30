# SkillLink - Job Matching Mobile Application

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.oracle.com/java/)
[![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/)
[![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)](https://gradle.org/)

SkillLink is a comprehensive Android mobile application that connects customers with skilled workers for various job categories. The platform facilitates job posting, worker discovery, communication, and payment processing through an intuitive mobile interface.

## 🚀 Features

### For Customers
- **Job Posting**: Create detailed job posts with descriptions, requirements, and budget
- **Worker Discovery**: Browse and search for qualified workers by skill category
- **Real-time Chat**: Communicate directly with potential workers
- **Payment Integration**: Secure payment processing with multiple options
- **Feedback System**: Rate and review completed work
- **Save List**: Bookmark favorite workers and job posts
- **Profile Management**: Manage customer profile and preferences

### For Workers
- **Job Browsing**: Browse available jobs in relevant categories
- **Profile Creation**: Create comprehensive worker profiles with skills and experience
- **Real-time Messaging**: Chat with customers about job requirements
- **Wallet Management**: Track earnings and manage payments
- **Feedback Collection**: Receive ratings and reviews from customers
- **Job Applications**: Apply for jobs that match skills and availability

### Technical Features
- **Firebase Integration**: Real-time database and authentication
- **Push Notifications**: Chat and job-related notifications
- **Media Sharing**: Share images and documents in chat
- **Offline Support**: Basic offline functionality with network state detection
- **Multi-user Authentication**: Separate flows for customers and workers
- **Responsive UI**: Material Design based responsive interface

## 🏗️ Architecture

### App Structure
The application follows a modular architecture with clear separation of concerns:

- **Activities**: Main user interface screens
- **Fragments**: Reusable UI components within activities
- **Adapters**: Data binding for RecyclerViews
- **Models**: Data models for entities
- **Services**: Background services for notifications and messaging
- **Receivers**: Network change and system event handling

### Key Components
- **Authentication Flow**: Separate sign-in/sign-up for customers and workers
- **Navigation**: Fragment-based navigation system
- **Data Management**: Firebase Firestore for real-time data
- **Image Handling**: Media upload and display capabilities
- **Chat System**: Real-time messaging with Firebase

## 🛠️ Technologies Used

### Core Technologies
- **Android SDK**: Native Android development
- **Java**: Primary programming language
- **Gradle**: Build automation and dependency management

### Third-party Services
- **Firebase**: Authentication, Firestore database, Cloud Messaging
- **Google Play Services**: Location and other Google APIs

### UI/UX
- **Material Design**: Modern UI components and styling
- **Custom Drawables**: Custom icons and backgrounds
- **Animations**: Smooth transitions and animations

## 📁 Project Structure

```
skilllink/
├── app/
│   ├── src/main/
│   │   ├── java/lk/javainstitute/skilllink/
│   │   │   ├── adapter/          # RecyclerView adapters
│   │   │   ├── auth/             # Authentication activities
│   │   │   ├── cactivity/        # Customer activities
│   │   │   ├── cfragments/       # Customer fragments
│   │   │   ├── model/            # Data models
│   │   │   ├── service/          # Background services
│   │   │   ├── receiver/         # Broadcast receivers
│   │   │   ├── wactivity/        # Worker activities
│   │   │   └── wfragments/       # Worker fragments
│   │   ├── res/
│   │   │   ├── drawable/         # Custom icons and graphics
│   │   │   ├── layout/           # Activity and fragment layouts
│   │   │   ├── menu/             # Menu definitions
│   │   │   ├── values/           # Colors, strings, themes
│   │   │   └── mipmap/           # App icons
│   │   └── AndroidManifest.xml   # App configuration
│   ├── build.gradle.kts          # App-level build configuration
│   └── google-services.json      # Firebase configuration
├── gradle/
│   ├── libs.versions.toml        # Dependency versions
│   └── wrapper/                  # Gradle wrapper
├── build.gradle.kts              # Root build configuration
├── settings.gradle.kts           # Project settings
└── gradlew                       # Gradle wrapper script
```

## 🚦 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 8 or later
- Android SDK API level 21+ (Android 5.0)
- Firebase account and project setup
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/skilllink.git
   cd skilllink
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the project directory and select it

3. **Firebase Setup**
   - Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Download the `google-services.json` file from Firebase Console
   - Place it in the `app/` directory
   - Enable Authentication (Email/Password)
   - Set up Firestore Database
   - Enable Cloud Messaging for notifications

4. **Build Configuration**
   ```bash
   # Sync project with Gradle files
   ./gradlew clean
   ./gradlew build
   ```

5. **Run the Application**
   - Connect an Android device or start an emulator
   - Click "Run" in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

### Configuration

#### Firebase Configuration
Update the following in `google-services.json`:
- Authentication providers (Email/Password)
- Firestore database rules
- Cloud Messaging settings

#### App Constants
Review and update any hardcoded values in:
- API endpoints
- Firebase configuration
- App-specific constants in resource files

## 🏃‍♂️ Running the App

### Development Build
```bash
./gradlew installDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Running Tests
```bash
./gradlew test
./gradlew connectedAndroidTest
```

## 📱 User Flow

### Customer Journey
1. **Registration/Sign-in** → Customer account creation
2. **Profile Setup** → Add personal and business information
3. **Job Posting** → Create job with description and budget
4. **Worker Selection** → Browse and contact suitable workers
5. **Communication** → Chat with workers about requirements
6. **Job Completion** → Mark job as complete and leave feedback
7. **Payment** → Process payment through integrated system

### Worker Journey
1. **Registration/Sign-in** → Worker account creation
2. **Profile Setup** → Add skills, experience, and portfolio
3. **Job Discovery** → Browse available jobs by category
4. **Application** → Apply for suitable jobs
5. **Communication** → Discuss details with customers
6. **Work Execution** → Complete assigned tasks
7. **Payment Collection** → Receive payment after job completion

## 🔧 Development

### Code Style
- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use meaningful variable and method names
- Add comprehensive comments for complex logic
- Follow Android best practices for UI/UX

### Architecture Patterns
- **MVP/MVVM**: Consider implementing for better testability
- **Repository Pattern**: For data layer abstraction
- **Dependency Injection**: Consider using Dagger/Hilt for better modularity

### Testing Strategy
- **Unit Tests**: Test business logic and data models
- **Integration Tests**: Test Firebase integration
- **UI Tests**: Test user interactions and flows

## 🔐 Security Considerations

- All Firebase security rules should be properly configured
- User input validation and sanitization
- Secure storage of sensitive data
- HTTPS communication for all network requests
- Regular security updates for dependencies

## 📊 Performance Optimization

- **Image Optimization**: Implement image compression and caching
- **Database Queries**: Optimize Firebase queries for better performance
- **Memory Management**: Proper handling of contexts and resources
- **Background Tasks**: Efficient use of background services

## 🐛 Known Issues

- [List any known issues and limitations]
- Network connectivity handling improvements needed
- Image upload size limitations
- Chat message pagination needed

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines
- Follow existing code style and conventions
- Add tests for new features
- Update documentation for significant changes
- Ensure all tests pass before submitting PR

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Java Institute Development Team** - *Initial work* - [Your Organization](https://javainstitute.lk)

## 🙏 Acknowledgments

- Firebase team for excellent backend services
- Android development community for resources and support
- Material Design team for UI/UX guidelines

## 📞 Support

For support and questions:
- **Email**: support@skilllink.lk
- **Documentation**: [Project Wiki](https://github.com/yourusername/skilllink/wiki)
- **Issues**: [GitHub Issues](https://github.com/yourusername/skilllink/issues)

## 🔄 Version History

- **v1.0.0** - Initial release with basic job matching functionality
- **v1.1.0** - Added payment integration and chat features
- **v1.2.0** - Enhanced UI and performance improvements
- **v2.0.0** - Major architecture update and new features (planned)

---

Made with ❤️ by the SkillLink Development Team
