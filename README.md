> [See in spanish/Ver en español](https://github.com/LuisMiSanVe/LangToSQL_Lite/blob/main/README.es.md)

<img src="https://github.com/LuisMiSanVe/LuisMiSanVe/blob/main/Resources/LangToSQL/LangToSQLLite_banner.png" style="width: 100%; height: auto;" alt="LangToSQL Lite Banner">

# <img src="https://github.com/LuisMiSanVe/LangToSQL_Lite/blob/main/LangToSQL/app/src/main/res/drawable/logo.png" width="40" alt="LangToSQL Lite Logo"> LangToSQL Lite | AI-Assisted App for SQLite
[![image](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![image](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/studio)
[![image](https://img.shields.io/badge/json-5E5C5C?style=for-the-badge&logo=json&logoColor=white)](https://www.newtonsoft.com/json)
[![image](https://img.shields.io/badge/Google%20Gemini-8E75B2?style=for-the-badge&logo=googlegemini&logoColor=white)](https://aistudio.google.com/app/apikey)
[![image](https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)](https://developer.android.com/studio)

>[!NOTE]
> Check out other versions of this program:
>- [WinForms](https://github.com/LuisMiSanVe/LangToSQL/tree/main)
>- [REST API](https://github.com/LuisMiSanVe/LangToSQL_API/tree/main)
>- [ChatBot](https://github.com/LuisMiSanVe/LangToSQL_ChatBot/tree/main)
>- [NuGet](https://github.com/LuisMiSanVe/LangToSQL_NuGet/tree/main)
>- [LLM](https://github.com/LuisMiSanVe/LangToSQL_LLM/tree/main)

This App uses Google's AI 'Gemini 2.5 Flash' to make queries to SQLite databases.  
The AI interprets natural language into SQL queries using one method, with its pros and cons.

## 📋 Prerequisites
To make this program work, you'll need a SQLite Database and a Gemini API Key.

## 🛠️ Setup
You can use either a Gemini API key or use a local LLM Server, I recommend using [LM Studio](https://lmstudio.ai/).

Obtain your Gemini API Key by visiting [Google AI Studio](https://aistudio.google.com/app/apikey). Ensure you are logged into your Google account, then press the blue button that says 'Create API key' and follow the steps to set up your Google Cloud Project and retrieve your API key. **Make sure to save it in a safe place**.  
Google allows free use of this API without adding billing information, but there are some limitations.

In Google AI Studio, you can monitor the AI's usage by clicking 'View usage data' in the 'Plan' column where your projects are displayed. I recommend monitoring the 'Quota and system limits' tab and sorting by 'actual usage percentage,' as it provides further more detailed information.

You now have everything needed to make the program work.  
Simply put that data you just got into the settings in the app.

## 📖 About the App
**[Natural Language to SQL Translation Method:](https://gist.github.com/LuisMiSanVe/2da8e2d932a06ef408b3ee8468d0d820)**  
This method maps the database structure into a JSON that Gemini analyzes ([with this prompt](https://gist.github.com/LuisMiSanVe/b189c8920d2dcedf5fd46485f3403d51)) to create an SQL query, which is then run on the SQLite Database, returning the requested data.  
Since this method does not map the database values, token usage is lower, and the data is more reliable since it directly comes from the SQLite Database. However, it does not completely prevent AI-generated errors. Occasionally, the SQL query might fail due to non-existing columns, in which case you should check the generated query to detect the error.

## 🚀 Releases
The version will be released using these versioning policies:\
New major features and critical bug fixes will cause the immediate release of a new version, while other minor changes or fixes will wait one week since the time the change is introduced in the repository before being included in the new version, so that other potential changes can be added.
>[!NOTE]
>These potencial new changes will not increase the wait time for the new version beyond one week.

The version number will follow this format: \
\[Major Feature\].\[Minor Feature\].\[Bug Fixes\]

## 💻 Technologies Used
- Programming Language: [Java](https://www.java.com/)
- Other:
  - [SQLite](https://sqlite.org/)
  - [LM Studio](https://lmstudio.ai/)
  - Gemini API Key (2.5 Flash)
  - Images (Icons source, later retouched by me):
    - [FreeIcons](https://freeicons.io/)
    - [Depositphotos](https://depositphotos.com/vector/coarse-halftone-dots-pattern-gradient-in-vector-format-82396024.html)
- Recommended IDE: [Android Studio](https://developer.android.com/studio)
