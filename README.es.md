> [Ver en ingles/See in english](https://github.com/LuisMiSanVe/LangToSQL_Lite/tree/main)

<img src="https://github.com/LuisMiSanVe/LuisMiSanVe/blob/main/Resources/LangToSQL/LangToSQLLite_banner.png" style="width: 100%; height: auto;" alt="LangToSQL Lite Banner">

# <img src="https://github.com/LuisMiSanVe/LangToSQL_Lite/blob/main/LangToSQL/app/src/main/res/drawable/logo.png" width="40" alt="Logo de LangToSQL Lite"> LangToSQL Lite | Aplicación Asistida por IA para SQLite
[![image](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![image](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/studio)
[![image](https://img.shields.io/badge/json-5E5C5C?style=for-the-badge&logo=json&logoColor=white)](https://www.newtonsoft.com/json)
[![image](https://img.shields.io/badge/Google%20Gemini-8E75B2?style=for-the-badge&logo=googlegemini&logoColor=white)](https://aistudio.google.com/app/apikey)
[![image](https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)](https://developer.android.com/studio)

>[!NOTE]
> Dale un vistazo a las otras versiones del programa:
>- [WinForms](https://github.com/LuisMiSanVe/LangToSQL/tree/main)
>- [REST API](https://github.com/LuisMiSanVe/LangToSQL_API/tree/main)
>- [ChatBot](https://github.com/LuisMiSanVe/LangToSQL_ChatBot/tree/main)
>- [NuGet](https://github.com/LuisMiSanVe/LangToSQL_NuGet/tree/main)
>- [LLM](https://github.com/LuisMiSanVe/LangToSQL_LLM/tree/main)

Esta App usa la IA de Google 'Gemini 2.0 Flash' para generar consultas a bases de datos SQLite.  
La IA convierte lenguaje natural a consultas SQL usando un método con sus ventajas y desventajas.

## 📋 Prerequisitos
Para que el programa funcione, necesiatarás una base de datos SQLite y una clave de la API de Gemini.

## 🛠️ Instalación
Puedes usar o bien una clave de API de Gemini o un servidor local de LLM, para ello recomiendo [LM Studio](https://lmstudio.ai/).

Obten tu clave de la API de Gemini yendo aquí: [Google AI Studio](https://aistudio.google.com/app/apikey). Asegúrate de tener tu sesión de Google abierta, y entonces dale al botón que dice 'Crear clave de API' y sigue los pasos para crear tu proyecto de Google Cloud y conseguir tu clave de API. **Guárdala en algún sitio seguro**.  
Google permite el uso gratuito de esta API sin añadir ninguna forma de pago, pero con algunas limitaciones.

En Google AI Studio, puedes monitorizar el uso de la IA haciendo clic en 'Ver datos de uso' en la columna de 'Plan' en la tabla con todos tus proyectos. Recomiendo monitorizarla desde la pestaña de 'Cuota y límites del sistema' y ordenando por 'Porcentaje de uso actual', ya que es donde más información obtienes.

Ya tienes todo lo que necesitas para hacer funcionar el programa.  
Simplemente pon los datos que acabas de conseguir en las pantallas de configuración del programa.

## 📖 Sobre la aplicación
**[Método de traducción de Lenguaje Natural a SQL:](https://gist.github.com/LuisMiSanVe/2da8e2d932a06ef408b3ee8468d0d820)**  
Este método mapea la estructura de la base de datos en un JSON que Gemini analiza ([con este prompt](https://gist.github.com/LuisMiSanVe/b189c8920d2dcedf5fd46485f3403d51)) para crear una consulta SQL, la cual es ejecutada en la base de datos SQLite directamente.  
Ya que este método no mapea los valores de la base de datos el uso de tokens es menor, y los datos que devuelve son mas fiables pues es el mismo Servidor el que los devuelve. Sin embargo, no evita completamente los errores que cometa la IA. A veces, la consulta SQL fallará debido a que la IA inventa columnas que no existen, en ese caso deberás comprobar la consulta generada para que identifiques el fallo.

## 🚀 Lanzamientos
Una versión será lanzada solo cuando se cumplan los siguientes puntos:\
Nuevas funciones importantes y arreglos de fallos criticos causarán la salida inmediata de una nueva versión, mientras que otros cambios o arreglos menores deberán esperar una semana desde que se incluyeron en el repositorio antes de ser incluidos en la nueva versión, para que otros posibles cambios puedan ser añadidos también.
>[!NOTE]
>Estos posibles nuevos cambios no alargarán la espera de la salida de la nueva versión a más de una semana.

El número de la versión seguirá este formato: \
\[Añadido Importante\].\[Añadido Menor\].\[Arreglos de Errores\]

## 💻 Tecnologías usadas
- Lenguaje de programación: [Java](https://www.java.com/)
- Otros:
  - [SQLite](https://sqlite.org/) 
  - [LM Studio](https://lmstudio.ai/)
  - Gemini API Key (2.0 Flash)
- Imagenes (Fuente original de los iconos, luego retocados por mí):
    - [FreeIcons](https://freeicons.io/)
    - [Depositphotos](https://depositphotos.com/vector/coarse-halftone-dots-pattern-gradient-in-vector-format-82396024.html)
- IDE Recomendado: [Android Studio](https://developer.android.com/studio)
