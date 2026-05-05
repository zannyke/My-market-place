# Use an official Android SDK image as the base
FROM eclipse-temurin:17-jdk-jammy

# Set Environment Variables
ENV ANDROID_SDK_ROOT /opt/android-sdk
ENV PATH ${PATH}:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin:${ANDROID_SDK_ROOT}/platform-tools

# Install dependencies
RUN apt-get update && apt-get install -y wget unzip git

# Download and install Android SDK Command-line Tools
RUN mkdir -p ${ANDROID_SDK_ROOT}/cmdline-tools \
    && wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O /tmp/cmdline-tools.zip \
    && unzip -q /tmp/cmdline-tools.zip -d ${ANDROID_SDK_ROOT}/cmdline-tools \
    && mv ${ANDROID_SDK_ROOT}/cmdline-tools/cmdline-tools ${ANDROID_SDK_ROOT}/cmdline-tools/latest \
    && rm /tmp/cmdline-tools.zip

# Accept licenses
RUN yes | sdkmanager --licenses

# Install platforms and build tools
RUN sdkmanager "platforms;android-34" "build-tools;34.0.0" "platform-tools"

# Set working directory
WORKDIR /app

# Copy the project files
COPY . .

# Run gradle build
RUN ./gradlew assembleDebug --no-daemon

# The output APK will be in /app/app/build/outputs/apk/debug/app-debug.apk
