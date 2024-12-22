@echo off

:: Vérifier si Java est installé
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Java n'est pas installé. Veuillez installer Java.
    pause
    exit /b
)

:: Répertoire du script
set SCRIPT_DIR=%~dp0

:: Se rendre dans le répertoire contenant le fichier Client.java
cd /d "%SCRIPT_DIR%"

:: Vérifier si le fichier .class existe, sinon le compiler
if not exist "Client.class" (
    echo Compilation du programme Java...
    javac Client.java
    if %errorlevel% neq 0 (
        echo Erreur de compilation.
        pause
        exit /b
    )
)

:: Exécution du client Java
java Client
