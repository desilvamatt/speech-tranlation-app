# Translation Nation
A simple android implementation of an English :arrow_forward: French translator using the Google Cloud Translation API.

## Cloud Translation
Since the Cloud Java client libraries do not currently support Android, the translated text is obtained by sending an HTTP GET request to the https://www.googleapis.com/language/translate/v2 Service.

## Using the Application
* Add your own API key on Line 39 of:
`SpeechTranslationApp/app/src/main/java/com/example/speechtranslationapp/MainActivity.java`
* Launch the application
* Tap on the bubbles to prompt speech input
* Say something in English
* Wait for the response

## Application
![Default](/public/Default.jpg)
![Example](/public/Example.jpg)
