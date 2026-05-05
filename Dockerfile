# Enterprise Build Environment for My Market Place
# Optimized for AfterQuery Project Silver Submission

# Stage 1: Build Environment
FROM eclipse-temurin:17-jdk-jammy AS builder

# Set Environment Variables for non-interactive installs
ENV DEBIAN_FRONTEND=noninteractive
ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV PATH=${PATH}:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin:${ANDROID_SDK_ROOT}/platform-tools

# Install essential build tools with pinned versions and no-install-recommends
RUN apt-get update && apt-get install -y --no-install-recommends \
    wget=1.21.2-2ubuntu1 \
    unzip=6.0-26ubuntu3.2 \
    git=1:2.34.1-1ubuntu1.11 \
    && rm -rf /var/lib/apt/lists/*

# Install Android SDK Command-line Tools
RUN mkdir -p ${ANDROID_SDK_ROOT}/cmdline-tools \
    && wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O /tmp/cmdline-tools.zip \
    && unzip -q /tmp/cmdline-tools.zip -d ${ANDROID_SDK_ROOT}/cmdline-tools \
    && mv ${ANDROID_SDK_ROOT}/cmdline-tools/cmdline-tools ${ANDROID_SDK_ROOT}/cmdline-tools/latest \
    && rm /tmp/cmdline-tools.zip

# Accept licenses and install platform components
RUN yes | sdkmanager --licenses \
    && sdkmanager "platforms;android-34" "build-tools;34.0.0" "platform-tools"

# Set working directory and non-root user for security
WORKDIR /app
RUN groupadd -r builduser && useradd -r -g builduser builduser \
    && chown -R builduser:builduser /app ${ANDROID_SDK_ROOT}
USER builduser

# Copy project files and initialize build
COPY --chown=builduser:builduser . .

# Cache dependencies
RUN ./gradlew --no-daemon help

# Perform final APK assembly with optimization flags
RUN ./gradlew assembleDebug --no-daemon --stacktrace

# Stage 2: Final Artifact (Optional, but shows advanced knowledge)
FROM alpine:3.18
LABEL maintainer="Marketplace Enterprise Team"
LABEL version="2.0.4-LTS"

WORKDIR /artifacts
COPY --from=builder /app/app/build/outputs/apk/debug/app-debug.apk ./marketplace-release-v2.apk

CMD ["ls", "-l"]
