<div align="center">
  <h1>IR Remote Xposed</h1>
  <p><strong>Universal Mod for IR Remote</strong> <br /> Fix crashes, bypass network and hardware restrictions to use IR Remote on any Android device.</p>
</div>

<div align="center">

[![Status](https://img.shields.io/badge/status-active-success.svg)]()
[![Platform](https://img.shields.io/badge/platform-Android-green.svg)]()
[![Root](https://img.shields.io/badge/root-required-red.svg)]()
[![Xposed](https://img.shields.io/badge/framework-LSPosed-blue.svg)]()
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)]()

</div>

## Introduction

**IR Remote Xposed** is a dedicated Xposed/LSPosed module designed to port the **IR Remote** app to non-Oppo and non-ColorOS devices. It operates automatically in the background to inject necessary framework dependencies and bypass strict SDK checks, making the app fully seamless on custom ROMs and unrelated brands. 

There's no standalone App UI for this module. Just install, activate, and you are ready to go!

## Key Features

-   **Crash Fix (NoClassDefFoundError)**:
    -   Dynamically injects `com.oplus.content.OplusFeatureConfigManager` to ART ClassLoader, resolving missing framework crashes on startup.
-   **Network & DRM Bypass**:
    -   Overrides "No Network Connection" restrictions within `b7.j0` and Kookong SDK.
-   **Hardware API Filter Bypass**:
    -   Tricks the Kookong IR Server into recognizing your device as an original "OPPO" device to fetch remote databases without signature verification errors.

## Screenshots

<div align="center">
    <img src="images/screenshot_001.png" width="300" style="margin: 5px;" alt="Screenshot 1" />
    <img src="images/screenshot_002.png" width="300" style="margin: 5px;" alt="Screenshot 2" />
    <img src="images/screenshot_003.png" width="300" style="margin: 5px;" alt="Screenshot 3" />
</div>

## Download Target Application

You will need the original IR Remote app. Download it here:
**[IR Remote (APKMirror)](https://www.apkmirror.com/apk/oneplus-ltd/ir-remote-2/)**

## Requirements

-   **Rooted Android Device**
-   **LSPosed Framework** (Zygisk or Riru) installed and active.
-   The **IR Remote** application installed.

## Installation

1.  **Download Module**: Get the latest APK from the Releases page.
2.  **Install**: Install the APK on your device.
3.  **Activate**:
    -   Open **LSPosed Manager**.
    -   Enable **IR Remote Xposed** module.
    -   Ensure the scope automatically ticks the **IR Remote** (`com.oplus.consumerIRApp`) application.
4.  **Enjoy**: Force stop the IR Remote app and open it again. No reboot required!

## Disclaimer

This software is provided "as is", without warranty of any kind. Modifying system behavior via Xposed carries inherent risks. The developer is **not responsible** for any damage or broken functionality. 

## Author

**Nguyễn Văn Khánh** (KhanhNguyen9872)
-   GitHub: [@KhanhNguyen9872](https://github.com/KhanhNguyen9872)

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.