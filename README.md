# Jago Modal Syariah **Jago Modal Syariah** is an Android-based *Securities
Crowdfunding* application designed to support financial inclusion and funding for real-sector
MSME projects. This application is directly integrated with the **Bank Jago Syariah** ecosystem
and features a Sharia compliance verification system (*Musyarakah* & *Mudharabah*)
governed by the Sharia Supervisory Board (DPS). 

## 🚀 Key Features - **Investor / Retail Funder:** - Explore active MSME funding campaigns (Culinary, Fashion, Agritech, etc.). 
  - Real-time profit-sharing calculator simulation (*expected yield*).
  - Multi-step *checkout* process
  with Digital Agreement (*Akad*) signing & *Ijab Qabul*.
  - Bank Jago Syariah PIN verification &
  Auto-debit from Sharia Pockets (*Kantong Syariah*).
  - Track monthly profit-sharing distribution
  history via *Digital Ledger*.

- **Issuer (MSME Owner):**
  - Submission of Sharia business capital
financing proposals.
  - Legal document upload (*NIB, NPWP, & Financial Statements*).
  - Operational Pocket management & profit-sharing transfer feature to investors.
  - Fund return feature (*100% Refund*) if funding targets are not met.

- **Sharia Supervisory Board (DPS / Admin):**
  - Verification and Sharia compliance audit (Free from *Gharar, Maysir, & Riba*).
  - Inspection worksheet and issuance of Sharia Opinion Letter with *SHA-256 E-Signature*.
  - Scheduling guidance & consultation sessions for MSME business governance.

## Technology & Architecture 
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
(Material3)
- **Architecture:** MVVM (Model-View-ViewModel) with StateFlow & Coroutines
- **Min SDK:** 24 (Android 7.0) | **Target SDK:** 36
- **Testing:** Robolectric & Roborazzi
(Screenshot Testing)

## 💻 How to Run the Application 
### **Prerequisites** 
1. [Android Studio](https://developer.android.com/studio) (Ladybug / Jellyfish version recommended). 
2. Android SDK version 34/36.
3. Android Emulator or Physical Device (Minimum Android 7.0 / API
24).
4. Java Development Kit (JDK) 11 or newer.

### **Installation Steps** 
1. **Clone the Repository** ```bash git clone https://github.com/username/jago-modal-syariah.git cd
jago-modal-syariah ```
2. **Open the Project in Android Studio**
   a. Launch Android Studio.
   b. Select **Open** and navigate to the cloned repository directory.
   c. Allow Gradle to download
dependencies (*Gradle Sync*).
3. **Environment Configuration (.env)**
   a. Create a `.env` file in the root directory of the project.
   b. Copy the configuration from `.env.example` and insert your
Gemini API Key: ```env GEMINI_API_KEY=your_gemini_api_key_here ``` 4. **Run the
Application** a. Select your Target Device (Emulator or Physical Smartphone via USB
Debugging). b. Click the **Run** button (`Shift + F10` or the Play icon on the top toolbar).
