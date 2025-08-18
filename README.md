# 📱 Chatter

Chatter is a simple real-time chat application built with **Kotlin**, **MVVM architecture**, and **Firebase**.  
It allows users to register, log in, and send/receive real-time messages using **Firebase Authentication** and **Cloud Firestore**.  
The app is designed to be beginner-friendly, clean, and easy to extend.

---

## ✨ Features
- 🔐 **User Authentication** (Sign up & Login with Firebase Auth)  
- 💬 **Real-time Messaging** with Firebase Firestore  
- 🔔 **Push Notifications** with Firebase Cloud Messaging (FCM) *(optional to extend)*  
- 🧩 **MVVM Architecture** with Repository pattern  
- 📱 **Fragments + Navigation Component (NavGraph)**  
- 🎨 Beginner-friendly UI with XML layouts  

---

## 🛠️ Tech Stack
- **Language:** Kotlin  
- **Architecture:** MVVM + LiveData + Coroutines  
- **UI:** XML layouts, Fragments, Navigation Graph  
- **Backend:** Firebase Authentication, Firestore Database  
- **Other:** Firebase Cloud Messaging (for notifications)  

---

## 📂 Project Structure
com.muhammadosman.chatter
│
├── data
│   ├── models        # Data classes (User, Message)
│   └── repositories  # Firebase repository (ChatRepository)
│
├── ui
│   ├── auth          # Login & Signup Fragments + ViewModels
│   ├── chat          # Chat screen Fragment + ViewModel
│   └── main          # Main Activity & Navigation
│
├── utils             # Helper classes & constants
│
└── ChatterApp.kt     # Application class

