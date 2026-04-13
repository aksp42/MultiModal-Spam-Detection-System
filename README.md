# 🛡️ Multi-Modal Email Spam Detection System (Text + Image) 📊

An advanced, end-to-end spam detection engine that goes beyond traditional text filtering. This system uses a **Late-Fusion AI Architecture** to analyze both email content and screenshots simultaneously, providing a robust defense against modern spam.

---

## 🚀 Overview

Traditional spam filters often fail when malicious content is embedded within images.  
This project bridges that gap by combining **Natural Language Processing (NLP)** and **Computer Vision (CV)** to build a smarter detection system.

The system is integrated with a live **Analytical Dashboard** to provide real-time monitoring and data-driven insights.

---

## 🔍 Model Performance

The system is powered by two deep learning models:

- 🧠 **DistilBERT (Text Model)** → **99.10% Accuracy**  
- 🖼️ **MobileNetV2 (Image Model)** → **99.13% Accuracy**

---

## ⚙️ How It Works (Decision Logic)

| Scenario        | Text Model | Image Model | Final Decision |
|----------------|------------|------------|----------------|
| Both Flagged   | SPAM       | SPAM       | ❌ SPAM         |
| Either Flagged | SPAM/HAM   | HAM/SPAM   | ⚠️ SUSPICIOUS   |
| Both Safe      | HAM        | HAM        | ✅ HAM          |

---

## 📊 Analytical Dashboard & Data Engineering

This project includes a real-time dashboard powered by **MySQL** and **Java Servlets**, focusing on Business Intelligence:

- 📈 **Real-Time Tracking** → Total scans, spam, and safe messages  
- ⚠️ **Suspicious Category** → Tracks conflicting predictions  
- 📊 **Confidence Metrics** → Average model confidence tracking  
- 🗄️ **Data Persistence** → Structured SQL pipeline for long-term analytics  

---

## 🛠️ Tech Stack

**AI / ML:** Python, PyTorch, Hugging Face Transformers, OpenCV  
**Backend:** Java (Servlets), JSP, Apache Tomcat  
**Database:** MySQL (JDBC)  
**Frontend:** HTML, CSS, JavaScript  

## 📂 Project Structure

```
.
├── WEB-INF
│   ├── mysql-connector-j-9.6.0.jar
│   └── servlet-api.jar
│
├── database_connectivity
│   ├── AnalyzeServlet.java
│   ├── DashboardServlet.java
│   ├── DatabaseManager.java
│   ├── DatabaseTest.java
│   └── t
│
├── models
│   ├── Model Code/
│   ├── text_model/
│   └── image_model.pth
│
├── License
├── README.md
├── about.html
├── analyze.html
├── app.py
├── dashboard.html
├── final_cleaned_data.csv
├── index.html
└── requirements.txt
```

## 🚀 Setup & Installation

### 1️⃣ Clone the Repository
git clone https://github.com/your-username/your-repo-name.git

### 2️⃣ Start Flask Server
cd Flask_Server  
python app.py  

### 3️⃣ Deploy Backend
- Move project / .war file to Apache Tomcat webapps folder  
- Start Tomcat server  

### 4️⃣ Database Setup
- Import .sql file into MySQL  
- Update credentials in DatabaseManager.java  

---

## ✨ Future Scope

- Integration with Email APIs (Gmail / Outlook)  
- Real-time alert notifications  
- Support for PDF / document-based spam detection  
- Enhanced dashboard with advanced BI insights  

---

## 👩‍💻 Developed By

**Akanksha Singh** - https://www.linkedin.com/in/akanksha-singh-4715a0351/
Video of the project : https://www.linkedin.com/feed/update/urn:li:activity:7435308264692711426/?originTrackingId=AvOMtZdGQbLQAvcHoStBjQ%3D%3D

📜 License
This project is licensed under the MIT License - see the LICENSE file for details.

---

## ⭐ Notes

Add screenshots like this:  
![Dashboard](path/to/image.png)  

 

---
