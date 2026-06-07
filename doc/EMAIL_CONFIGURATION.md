# Documentation de l'envoi d'emails

La solution retenue pour l'envoi d'emails dans ce backend Spring Boot est `spring-boot-starter-mail`.

## Configuration
Il faut ajouter la dépendance `spring-boot-starter-mail` dans le fichier `pom.xml`.

La configuration du serveur SMTP doit être effectuée dans le fichier `src/main/resources/application.properties` :

```properties
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=your-username
spring.mail.password=your-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## Utilisation
La classe `org.springframework.mail.javamail.JavaMailSender` permet d'envoyer des emails de manière simple et asynchrone si nécessaire.
## Déploiement (ex: Render)
Pour le déploiement, les variables d'environnement suivantes doivent être configurées :
- `JDBC_URI` : URL de connexion à la base de données PostgreSQL.
- `MAIL_HOST` : Hôte du serveur SMTP.
- `MAIL_PORT` : Port du serveur SMTP (défaut: 587).
- `MAIL_USERNAME` : Nom d'utilisateur SMTP.
- `MAIL_PASSWORD` : Mot de passe SMTP.
