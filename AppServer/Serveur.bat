@echo off
setlocal

:: Définir le classpath
set CLASSPATH=.

:: Compiler tous les fichiers Java dans les sous-dossiers
javac -d . *.java 

:: Vérifier si la compilation a réussi
if %errorlevel% neq 0 (
    echo Erreur lors de la compilation.
    exit /b %errorlevel%
)

:: Exécuter les classes avec le bon package (si applicable)
start "Serveur" cmd /k java serveur.Serveur
start "ServeurBackup" cmd /k java serveur.ServeurBackup

endlocal
